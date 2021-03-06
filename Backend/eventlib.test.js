"use strict";

const eventlib = require("./eventlib.js");
jest.mock("./longlat.js", () => {
    return {
        calculateLongLat : (location) => ([0, 0])
    };
});

jest.mock("./firebase.js", () => {
    return {
        sendNotif : (msg, token) => 0
    };
});

test("Testing Event.equals", () => {
    let ev = new eventlib.Event(null, null, null, null, null, null, null, null);
    let ev2 = new eventlib.Event(null, null, null, null, null, null, null, null);
    let location = "A location";
    expect(ev.equals(ev2)).toBe(false); // Invalid events
    expect(ev.hash()).toBe(null);       // Hash of invalid event

    ev = new eventlib.Event(1, "test host", null, null, new Date(), new Date(), location);
    ev2 = new eventlib.Event(ev.id, ev.host, ev.name, ev.desc, ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(true); // Copying fields
    
    ev2 = new eventlib.Event(2, "test host", ev.name, ev.desc, ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(false); // Different IDs

    ev2 = new eventlib.Event(1, "test host", "test", ev.desc, ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(true); // Different names should not influence equals
    
    ev2 = new eventlib.Event(1, "test host", ev.name, "test", ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(true); // Different descriptions should not influence equals

    ev2 = new eventlib.Event(1, "test host", ev.name, ev.desc, new Date(0), ev.end, location);
    expect(ev.equals(ev2)).toBe(false); // Different starts
    
    ev2 = new eventlib.Event(1, "test host", ev.name, ev.desc, ev.start, new Date(0), location);
    expect(ev.equals(ev2)).toBe(false); // Different ends
});


test("Testing User Friend handling", () => {
    const u1 = new eventlib.User(1, "1", "1", "1");
    const u2 = new eventlib.User(1, "1", "1", "1");
    
    // U1 should not be able to add itself
    expect(u1.addFriend(1)).toBe(false);
    
    // U1 becomes friends with U2
    expect(u1.addFriend(2)).toBe(true);
    expect(u1.isFriend(2)).toBe(true);

    // addFriend should not change U2
    expect(u2.isFriend(1)).toBe(false);

    // U2 adding U1 should work
    //expect(u2.addFriend(1)).toBe(true);
    //expect(u2.isFriend(1)).toBe(true);
    
    // U1 adding U2 again should not work
    expect(u1.addFriend(2)).toBe(false);
    
    // getFriends should return u2's id
    let u1f = u1.getFriends();
    expect(u1f.length).toBe(1);
    expect(u1f[0].id).toBe(2);

    // getFriends should return a copy, not a reference
    u1f[0].id = 42;
    expect(u1.getFriends()[0].id).toBe(2);
    
    expect(u1.deleteFriend(-1)).toBe(false);
    expect(u1.deleteFriend(2)).toBe(true);
	u1.sendNotification("test");
});

test("Testing Friend Request handling", () => {
    const u1 = new eventlib.User(1, "1", "1", "1");
    const u2 = new eventlib.User(2);
    
    // U1 should not be able to add itself
    expect(u1.addRequest(1, null, null, null, true)).toBe(false);
    
    // U1 becomes friends with U2
    expect(u1.addRequest(2, null, null, null, true)).toBe(true);
    expect(u1.isRequesting(2, null, null, null, true)).toBe(true);

    // addFriend should not change U2
    expect(u2.isRequesting(1, null, null, null, true)).toBe(false);

    // U2 adding U1 should work
    expect(u2.addRequest(1, null, null, null, true)).toBe(true);
    expect(u2.isRequesting(1, null, null, null, true)).toBe(true);
    
    // U1 adding U2 again should not work
    expect(u1.addRequest(2, null, null, null, true)).toBe(false);
    
    expect(u1.deleteRequest(2)).toBe(false);
    expect(u1.addRequest(2)).toBe(true);
    expect(u1.deleteRequest(2)).toBe(true);
    expect(u1.deleteRequest(2, true)).toBe(true);
    expect(u1.deleteRequest(2, true)).toBe(false);

	u1.sendNotification("test");
	u1.updateDevice("new device");
});

test("Testing User Event handling", () => {
    const u1 = new eventlib.User(1);
    
    const e1 = new eventlib.Event(0, 1, null, null, new Date(0), new Date(1), null);
    const e2 = new eventlib.Event(1, 1, null, null, new Date(0), new Date(1), null);
    const e3 = new eventlib.Event(0, 1, null, null, new Date(3), new Date(4), null);
    
    // e1 and e2 have different IDs, so adding them should work
    expect(u1.addEvent(e1)).toBe(true);
    expect(u1.addEvent(e2)).toBe(true);
    expect(u1.addEvent(e3)).toBe(false);
    
    // getEvents should return [0, 1], which corresponds to e1 and e2
    let events = u1.getEvents();
    expect(events.length).toBe(2);
    expect(events[0]).toBe(e1.id);
    expect(events[1]).toBe(e2.id);
    
    // the array returned by getEvents should be a copy, not a reference
    events[0] = 2;
    expect(u1.getEvents()[0]).toBe(e1.id);
});

test("Testing User getProfile", () => {
    const u1 = new eventlib.User(1);
    const e1 = new eventlib.Event(0, 0, null, null, new Date(0), new Date(1), null);

    expect(u1.addEvent(e1)).toBe(true);
    expect(u1.addFriend(2)).toBe(true);
    
    expect(u1.getProfile()).toBe(JSON.stringify({
        id : 1,
        events : [e1.id],
        friends : [{id:2}],
        requestin : [],
        requestout : []
    }));
    
});

test("Testing Event suggestion", () => {
    const ATTENDEE_WEIGHT = 1;
    const FRIEND_WEIGHT = 20;
    
    const u1 = new eventlib.User(1);
    const u2 = new eventlib.User(2);
    const u3 = new eventlib.User(3);
    const e1 = new eventlib.Event(0, null, null, null, new Date(0), new Date(1), null);

    expect(u1.addFriend(2)).toBe(true);
    e1.attendees.push(2);
    e1.attendees.push(3);
    expect(e1.calculateScore(u1)).toBe(ATTENDEE_WEIGHT + FRIEND_WEIGHT);
    e1.attendees.push(1);
    expect(e1.calculateScore(u1)).toBe(-1);
});

test("Testing EventImpl conflicts", () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);

    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 10, 24, 16, 30);
    
    const location = { lat : 0, long : 0 };
    
    const ev = new eventlib.Event(1, null, null, null, start1, end1, location);
    const ev2 = new eventlib.Event(2, null, null, null, start2, end2, location);
    const ev3 = new eventlib.Event(3, null, null, null, start1, end2, location);
    
    // Identical events
    const evImpl = new eventlib.EventImpl(0);
    const evImpl2 = new eventlib.EventImpl(0);
    const evImpl3 = new eventlib.EventImpl(0);
    const evImpl4 = new eventlib.EventImpl(0);
    
    evImpl.importEvent(ev);
    evImpl2.importEvent(ev);
    evImpl2.importEvent(ev2);
    evImpl3.importEvent(ev2);
    evImpl4.importEvent(ev3);
    
    // Not an EventImpl
    expect(evImpl.conflicts(null)).toBe(false);
    
    // Non-conflicting events
    expect(evImpl.conflicts(evImpl3)).toBe(false);
    expect(evImpl3.conflicts(evImpl)).toBe(false);
        
    // evImpl is a subset of evImpl2
    expect(evImpl2.conflicts(evImpl3)).toBe(true);
    expect(evImpl3.conflicts(evImpl2)).toBe(true);
    
    // Overlapping events
    expect(evImpl3.conflicts(evImpl4)).toBe(true);
    expect(evImpl4.conflicts(evImpl3)).toBe(true);
    
});

test("Testing EventImpl Serialize", () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 30, 13, 50);
    const location = { lat : 0, long : 0 };
    const ev = new eventlib.Event(1, null, null, null, start1, end1, location);
    const evImpl = new eventlib.EventImpl(0);
    
    expect(ev.isValid()).toBeTruthy();
    evImpl.importEvent(ev);
    
    let json = evImpl.serialize();
    const entries = Array.from(evImpl.timeslots.entries());
    expect(json.id).toBe(0);
    expect(json.timeslots).toStrictEqual(entries);
});
