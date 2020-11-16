"use strict";

const eventlib = require("./eventlib.js");

test("Testing Event.equals", () => {
    let ev = new eventlib.Event(null, null, null, null, null, null);
    let ev2 = new eventlib.Event(null, null, null, null, null, null);
    let location = { "lat" : 0, "long" : 0 };
    expect(ev.equals(ev2)).toBe(false); // Invalid events

    ev = new eventlib.Event(1, null, null, new Date(), new Date(), location);
    ev2 = new eventlib.Event(ev.id, ev.name, ev.desc, ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(true); // Copying fields
    
    ev2 = new eventlib.Event(2, ev.name, ev.desc, ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(false); // Different IDs

    ev2 = new eventlib.Event(1, "test", ev.desc, ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(true); // Different names should not influence equals
    
    ev2 = new eventlib.Event(1, ev.name, "test", ev.start, ev.end, location);
    expect(ev.equals(ev2)).toBe(true); // Different descriptions should not influence equals

    ev2 = new eventlib.Event(1, ev.name, ev.desc, new Date(0), ev.end, location);
    expect(ev.equals(ev2)).toBe(false); // Different starts
    
    ev2 = new eventlib.Event(1, ev.name, ev.desc, ev.start, new Date(0), location);
    expect(ev.equals(ev2)).toBe(false); // Different ends
});


test("Testing User Friend handling", () => {
	const u1 = new eventlib.User(1);
	const u2 = new eventlib.User(2);
	
	// U1 becomes friends with U2
	expect(u1.addFriend(2)).toBe(true);
	expect(u1.isFriend(2)).toBe(true);

	// addFriend should not change U2
	expect(u2.isFriend(1)).toBe(false);

	// U2 adding U1 should work
	expect(u2.addFriend(1)).toBe(true);
	expect(u2.isFriend(1)).toBe(true);
	
	// U1 adding U2 again should not work
	expect(u1.addFriend(2)).toBe(false);
	
	// getFriends should return u2's id
	let u1f = u1.getFriends();
	expect(u1f.length).toBe(1);
	expect(u1f[0]).toBe(2);

	// getFriends should return a copy, not a reference
	u1f[0] = 42;
	expect(u1.getFriends()[0]).toBe(2);
});

test("Testing User Event handling", () => {
	const u1 = new eventlib.User(1);
	
	const e1 = new eventlib.Event(0, null, null, new Date(0), new Date(1), null);
	const e2 = new eventlib.Event(1, null, null, new Date(0), new Date(1), null);
	const e3 = new eventlib.Event(0, null, null, new Date(3), new Date(4), null);
	
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
	const e1 = new eventlib.Event(0, null, null, new Date(0), new Date(1), null);

	expect(u1.addEvent(e1)).toBe(true);
	expect(u1.addFriend(2)).toBe(true);
	
	expect(u1.getProfile()).toBe(JSON.stringify({
		id : 1,
		events : [e1.id],
		friends : [2]
	}));
});

test("Testing EventImpl equals", () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);

    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 10, 24, 16, 30);
    
    const ev = new eventlib.Event(1, null, null, start1, end1, null);
    const ev2 = new eventlib.Event(2, null, null, start1, end1, null);
    const ev3 = new eventlib.Event(3, null, null, start2, end2, null);
    
    // Identical events
    const evImpl = new eventlib.EventImpl(0);
    let evImpl2 = new eventlib.EventImpl(0);
    
    evImpl.importEvent(ev);
    evImpl2.importEvent(ev);
    
    expect(evImpl.equals(evImpl2)).toBe(true);
    // Not an EventImpl
    expect(evImpl.equals(null)).toBe(false);
    
    // Different IDs
    evImpl2 = new eventlib.EventImpl(0);
    evImpl2.importEvent(ev2);
    
    expect(evImpl.equals(evImpl2)).toBe(false);
    
    // evImpl is a subset of evImpl2
    evImpl2 = new eventlib.EventImpl(0);
    evImpl2.importEvent(ev);
    evImpl2.importEvent(ev3);
    
    expect(evImpl2.equals(evImpl)).toBe(false);
});

test("Testing EventImpl attends", () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);

    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 12, 24, 16, 30);
    
    const ev = new eventlib.Event(1, null, null, start1, end1, null);
    const ev2 = new eventlib.Event(2, null, null, start1, end1, null);
    const ev3 = new eventlib.Event(3, null, null, start2, end2, null);
    
    // Identical eventimpls
    const evImpl = new eventlib.EventImpl(0);
    let evImpl2 = new eventlib.EventImpl(0);

    evImpl.importEvent(ev);
    evImpl2.importEvent(ev);
    
    expect(evImpl.attends(evImpl2)).toBe(true);

    // Different event IDs
    evImpl2 = new eventlib.EventImpl(0);
    evImpl2.importEvent(ev2);
    
    expect(evImpl.attends(evImpl2)).toBe(false);
    
    // evImpl is a subset of evImpl2
    evImpl2 = new eventlib.EventImpl(0);
    evImpl2.importEvent(ev);
    evImpl2.importEvent(ev3);
    
    expect(evImpl2.attends(evImpl)).toBe(true);
});

test("Testing EventImpl conflicts", () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);

    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 10, 24, 16, 30);
    
    const ev = new eventlib.Event(1, null, null, start1, end1, null);
    const ev2 = new eventlib.Event(2, null, null, start2, end2, null);
    
    // Identical events
    const evImpl = new eventlib.EventImpl(0);
    const evImpl2 = new eventlib.EventImpl(0);
    const evImpl3 = new eventlib.EventImpl(0);
    
    evImpl.importEvent(ev);
    evImpl2.importEvent(ev);
    evImpl2.importEvent(ev2);
    evImpl3.importEvent(ev2);
    // Not an EventImpl
    expect(evImpl.conflicts(null)).toBe(false);
    
    // Non-conflicting events
    expect(evImpl.conflicts(evImpl3)).toBe(false);
    expect(evImpl3.conflicts(evImpl)).toBe(false);
        
    // evImpl is a subset of evImpl2
    expect(evImpl2.conflicts(evImpl3)).toBe(true);
    expect(evImpl3.conflicts(evImpl2)).toBe(true);
});
