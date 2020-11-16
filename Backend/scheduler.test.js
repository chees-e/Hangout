"use strict";

jest.mock("./database.js", () => {
    let testData = new Map([
        ["events", new Map()],
        ["users", new Map()],
        ["impls", new Map()],
        ["nextID", { value : 1 } ],
        ["lastID", { value : null} ]
    ]);
    const datafn = jest
        .fn()
        .mockImplementationOnce((path, obj) => (-1))
        .mockImplementation((path, obj) => {
            let keys = path.split("/");
            let firstVal = testData.get(keys[0]);
            if (firstVal instanceof Map) {
            // Arbitrarily choose event 10 to fail
                if (path === "events/10") { 
                    return -1;
                } else if (keys.length === 1) {
                    testData.set(keys[0], obj);
                    return 0;
                } else {
                    firstVal.set(keys[1], obj);
                    return 0;
                }
            } else {
                testData.set(keys[0], { value : obj });
                return 0;
            }
        });
    return {
        setData: async (path, obj) => {
            const serialize = (obj) => {
                if (obj instanceof require("./eventlib.js").EventImpl) {
                    return obj.serialize();
                } else {
                    return obj;
                }
            };
            return datafn(path, serialize(obj));
        },
        getData: async (path) => {
            let keys = path.split("/");
            let firstVal = testData.get(keys[0]);
            if (firstVal instanceof Map) {
            // Arbitrarily choose event 10 to fail
                if (path === "events/10") { 
                    return null;
                } else if (keys.length === 1) {
                    return firstVal;
                } else {
                    return firstVal.get(keys[1]);
                }
            } else {
                return testData.get(keys[0]);
            }
        },
        getKeys: async (path) => {
            let firstVal = testData.get(path);
            if (firstVal instanceof Map) {
                let ret = [];
                for (let _ of firstVal.keys()) {
                    ret.push({ id : _ });
                }
                return ret;
            } else {
                return [];
            }
        },
        hasKey: async (path) => {
            let keys = path.split("/");
            let firstVal = testData.get(keys[0]);
            if (firstVal instanceof Map) {
                return firstVal.has(keys[1]);
            } else {
                return testData.has(keys[0]);
            }
        },
        deleteKey: async (path) => {
            let keys = path.split("/");
            if (keys.length !== 2) {
                return -2;
            } else if (testData.has(keys[0]) && testData.get(keys[0]).has(keys[1])){
                let dat = testData.get(keys[0]);
                dat.delete(keys[1]);
                testData.set(keys[0], dat);
                return 0;
            } else {
                return -1;
            }
        }
    };
});

jest.mock("./eventlib.js", () => {
    let userEvents = [];
    let userFriends = [];
    return {
        Event : function (_id) {
            let attendees = [];
            return {
                id : _id,
                attendees : attendees,
                isValid: () => (_id !== null),
                equals: (other) => (other.id === _id),
                calculateScore : (user) => {
                    const FRIEND_WEIGHT = 20;
                    const ATTENDEE_WEIGHT = 1;
                    if (attendees.includes(user)) {
                        return -1;
                    }
                    let score = 0;
                    for (let otherUser of this.attendees) {
                        if (user.isFriend(otherUser)) {
                            score += FRIEND_WEIGHT;
                        } else {
                            score += ATTENDEE_WEIGHT;
                        }
                    }
                    return score;
                }
            };
        },
        User : function (_id) {
            return {
                id : _id,
                events : userEvents,
                friends : userFriends,
                addEvent : (event) => {
                    if (userEvents.includes(event.id)) {
                        return false;
                    } else {
                        userEvents.push(event.id);
                        return true;
                    }
                },
                addFriend : (_id) => {
                    if (userFriends.includes(_id)) {
                        return false;
                    } else {
                        userFriends.push(_id);
                        return true;
                    }
                },
                getEvents : () => (userEvents.slice()),
                getFriends : () => (userFriends.slice()),
                isFriend : (_id) => (userFriends.includes(_id))
            };
        },
        EventImpl : function (_id) {
            return {
                id : _id,
                importEvent : (event) => (true),
                equals : (other) => (other.id === _id),
                conflicts : (other) => (other.id >= _id)
            };
        }
    };
});

const eventlib = require("./eventlib.js");
const scheduler = require("./scheduler.js");
const assert = require("assert");


function assertArrEqual(ev1, ev2){
    expect(ev1.length).toBe(ev2.length);
    expect(ev1.every((elem, idx) => {
        let other = ev2.slice(idx, idx+1)[0];
        if (elem.hasOwnProperty("equals")) {
            return elem.equals(other);
        } else {
            return elem === other;
        }
    })).toBe(true);
}

test("Testing addEvent", async () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const location = {"lat":0, "long":0};

    // Reset should fail the first time because of the mock
    expect(await scheduler.reset()).toBe(-1);

    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    let evid = await scheduler.getNextID();
    const ev = new eventlib.Event(evid, "Test", "TestDesc", start1, end1, location);
    const invalidEvent = new eventlib.Event(null, null, null, null, null, null);

    // Default event: scheduler.getEvent() with no events must not error
    const defEvent = await scheduler.getEvent();
    expect(defEvent.equals(invalidEvent)).toBe(true);

    // Add a valid event
    expect(await scheduler.addEvent("Test", evid, "TestDesc", start1, end1, location)).toBe(evid);
    
    // Add the same event twice, should indicate a conflict
    expect(await scheduler.addEvent("Test", evid, "TestDesc", start1, end1, location)).toBe(-1);

    // Add an event which was predetermined to fail in the mock
    expect(await scheduler.addEvent("Test", 10, "testDesc", start1, end1, location)).toBe(-1);

    // getEvent with no argument should return the last added event
    let evnt1 = await scheduler.getEvent();
    expect(ev.equals(evnt1)).toBe(true);

    let newID = await scheduler.getNextID();
    assert(evid !== newID);

    // getEvent must return the last added event
    let evnt = await scheduler.getEvent(evid);
    expect(ev.equals(evnt)).toBe(true);
    
    await scheduler.addUser(newID);
    let events3 = await scheduler.getAllEvents();
    assertArrEqual(events3, [ev]);  // Adding users must not add events

    // Add an invalid event, should reserve the next available ID
    let nextID = await scheduler.getNextID();
    expect(await scheduler.addEvent(null, null, null, null, null, null)).toBe(nextID);
});

test("Testing AddEventToUser", async () => {

    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 10, 24, 16, 30);
    const UID = 3;
    const location = {"lat":0, "long":0};

    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    // Add an event with start start1 and end end1
    const EID = await scheduler.getNextID();
    const ev = new eventlib.Event(EID, null, null, start1, end1, location);
    expect(await scheduler.addEvent(null, EID, null, start1, end1, location)).toBe(EID);

    // Add an event with start start2 and end end2
    const EID2 = await scheduler.getNextID();
    expect(EID !== EID2).toBe(true);
    const ev2 = new eventlib.Event(EID2);    
    expect(await scheduler.addEvent(null, EID2, null, start2, end2, location)).toBe(EID2);
    expect(await scheduler.addEvent(null, 3, null, start2, end2, location)).toBe(3);

    // Add a user with id UID
    expect(await scheduler.addUser(UID)).toBe(UID);
    // Cannot add the same user twice
    expect(await scheduler.addUser(UID)).toBe(-1);

    // Case 1: UID valid, EID valid, no conflict: expects 0
    expect(await scheduler.addEventToUser(UID, EID)).toBe(0);
    // Case 2: UID valid, EID valid, conflict: expects -1
    expect(await scheduler.addEventToUser(UID, 3)).toBe(-1);
    // Case 3: UID valid, EID invalid: expects -2
    expect(await scheduler.addEventToUser(UID, -1)).toBe(-2);
    // Case 4: UID valid, EID valid, no conflict: expects 0
    expect(await scheduler.addEventToUser(UID, EID2)).toBe(0);
    // Case 5: UID invalid, EID valid: expects -2
    expect(await scheduler.addEventToUser(-1, EID2)).toBe(-2);
    
    let user = await scheduler.getUser(UID);
    expect(user.events.length).toBe(2);
    expect(user.events[0].toString()).toBe(EID.toString());
    expect(user.events[1].toString()).toBe(EID2.toString());
});

test("Testing deleteEvent", async () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const location = {"lat":0, "long":0};

    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    let evid = await scheduler.getNextID();

    // Deleting an event which does not exist should return -1
    expect(await scheduler.deleteEvent(evid)).toBe(-1);
    
    // Adding an event and then deleting it should work
    expect(await scheduler.addEvent(null, evid, null, start1, end1, location)).toBe(evid);
    expect(await scheduler.deleteEvent(evid)).toBe(0);
    
    // At this point the event should not exist
    expect(await scheduler.deleteEvent(evid)).toBe(-1);
    
    // Adding an event and then deleting another event should not work
    expect(await scheduler.addEvent(null, evid, null, start1, end1, location)).toBe(evid);
    expect(await scheduler.deleteEvent(-2)).toBe(-1);
    expect(await scheduler.deleteEvent(evid)).toBe(0);
});
