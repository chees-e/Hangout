"use strict";

jest.mock("./database.js", () => {
    let testData = new Map([
        ["events", new Map()],
        ["users", new Map()],
        ["impls", new Map()],
        ["nextID", null ],
        ["lastID", { value : null} ]
    ]);
    const invalidpaths = [ "events/10" ];
    const clearfn = jest.fn()
        .mockImplementationOnce(() => (false))
        .mockImplementation(() => {
            testData = new Map([
                ["events", new Map()],
                ["users", new Map()],
                ["impls", new Map()],
                ["nextID", { value : 0 } ],
                ["lastID", { value : null} ]
            ]);
            return true;
        });
    return {
        setData: async (path, obj) => {
            let keys = path.split("/");
            let firstVal = testData.get(keys[0]);
            if (firstVal instanceof Map) {
            // Arbitrarily choose event 10 to fail
                if (invalidpaths.includes(path)) { 
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
        },
        getData: async (path) => {
            let keys = path.split("/");
            let firstVal = testData.get(keys[0]);
            if (firstVal instanceof Map) {
            // Arbitrarily choose event 10 to fail
                if (invalidpaths.includes(path)) { 
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
        clear: async () => {
            return clearfn();
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
    let Friend = class Friend {
        constructor(id){
            this.id = id;
        }
        sendNotification(msg) {
            return true;
        }
    };
    return new Object({
        Event : class Event{
            constructor(_id, host, name, desc, start, end, location, attendees) {
                this.id = _id;
                this.host = host;
                if (attendees) {
                    this.attendees = attendees;
                } else {
                    this.attendees = [];
                }
            }
            isValid() {
                return this.id !== null;
            }
            equals(other) {
                return other.id === this.id;
            }
            calculateScore(user) {
                const FRIEND_WEIGHT = 20;
                const ATTENDEE_WEIGHT = 1;
                if (this.attendees.includes(user)) {
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
        },
        User : class User {
            constructor(_id, name, device, pfp) {
                this.id = _id;
                this.name = name;
                this.device = device;
                this.pfp = pfp;
                this.events = [];
                this.friends = [];
                this.requestin = [];
                this.requestout = [];
            }
            addEvent(event) {
                if (this.events.includes(event.id)) {
                    return false;
                } else {
                    this.events.push(event.id);
                    return true;
                }
            }
            addFriend(_id) {
                if (_id === this.id) {
                    return false;
                }
                for (let friend of this.friends) {
                    if (friend.id === _id) {
                        return false;
                    }
                }
                this.friends.push(new Friend(_id));
                return true;
            }
            deleteFriend(_id) {
                if (this.isFriend(_id)) {
                    let idx = 0;
                    for (let friend of this.friends) {
                        if (friend.id === _id) {
                            this.friends.splice(idx, 1);
                            break;
                        }
                        idx++;
                    }
                    return true;
                } else {
                    return false;
                }
            }
            getEvents(){
                return this.events.slice();
            }
            getFriends(){
                return this.friends.slice();
            }
            isFriend(_id) {
                for (let friend of this.friends) {
                    if (friend.id === _id) {
                        return true;
                    }
                }
                return false;
            }
            addRequest(id, name, device, pfp, out) {
                if (id === this.id) {
                    return false;
                }
                
                if (out) {
                    this.requestout.push(id);
                } else {
                    this.requestin.push(id);
                }
                return true;
            }
            deleteRequest(id, out) {
                if (out) {
                    if (this.requestout.includes(id)) {
                        this.requestout.splice(this.requestout.indexOf(id), 1);
                        return true;
                    }
                    return false;
                } else {
                    if (this.requestin.includes(id)) {
                        this.requestin.splice(this.requestout.indexOf(id), 1);
                        return true;
                    }
                    return false;
                }
            }
            isRequesting(_id) {
                return (this.requestin.includes(_id) || this.requestout.includes(_id));
            }
            updateDevice(_device) {
                this.device = _device;
            }
            sendNotification(msg) {
                return;
            }
        },
        EventImpl : class EventImpl {
            constructor(_id) {
                this.id = _id;
            }
            importEvent(event) {
                return true;
            }
            equals(other) {
                return other.id === this.id;
            }
            conflicts(other) {
                return other.id >= this.id;
            }
        },
        Friend
    });
});

const eventlib = require("./eventlib.js");
const scheduler = require("./scheduler.js");
const assert = require("assert");


function assertArrEqual(ev1, ev2){
    expect(ev1.length).toBe(ev2.length);
    expect(ev1.every((elem, idx) => {
        let other = ev2.slice(idx, idx+1)[0];
        return elem.equals(other);
    })).toBe(true);
}

test("Testing addEvent", async () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const location = {"lat":0, "long":0};

    // Reset should fail the first time because of the mock
    expect(await scheduler.reset()).toBe(-1);

    // If nextID has no value, getNextID should return 1.
    expect(await scheduler.getNextID()).toBe(1);

    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    let evid = await scheduler.getNextID();
    const ev = new eventlib.Event(evid, "Test", "TestDesc", start1, end1, location);
    const invalidEvent = new eventlib.Event(null, null, null, null, null, null);

    // Default event: scheduler.getEvent() with no events must not error
    const defEvent = await scheduler.getEvent();
    expect(defEvent.equals(invalidEvent)).toBe(true);

    // Add a valid event
    expect(await scheduler.addEvent("Test", null, evid, "TestDesc", start1, end1, location)).toBe(evid);
    
    // Add the same event twice, should indicate a conflict
    expect(await scheduler.addEvent("Test", null, evid, "TestDesc", start1, end1, location)).toBe(-1);

    // Add an event which was predetermined to fail in the mock
    expect(await scheduler.addEvent("Test", null, 10, "testDesc", start1, end1, location)).toBe(-1);

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

test("Testing addUser", async () => {
    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);
    
    // Case 1: Adding user to scheduler with no users works
    expect(await scheduler.addUser("testUser", "name", "dev", "pfp")).toBe("testUser");
    // Case 2: Adding the same user twice does not work
    expect(await scheduler.addUser("testUser", "name", "dev2", "pfp")).toBe(-1);
    // Case 3: Adding another user should work
    expect(await scheduler.addUser("testUser2", "name", "dev", "pfp")).toBe("testUser2");
    // Case 4: Users should actually be added to the scheduler
    const users = await scheduler.getAllUsers();
    expect(users).toEqual([
        new eventlib.User("testUser", "name", "dev2", "pfp"),
        new eventlib.User("testUser2", "name", "dev", "pfp")
    ]);
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
    expect(await scheduler.addEvent(null, null, EID, null, start1, end1, location)).toBe(EID);

    // Add an event with start start2 and end end2
    const EID2 = await scheduler.getNextID();
    expect(EID !== EID2).toBe(true);
    expect(await scheduler.addEvent(null, null, EID2, null, start2, end2, location)).toBe(EID2);
    expect(await scheduler.addEvent(null, null, 3, null, start2, end2, location)).toBe(3);

    // Add a user with id UID
    expect(await scheduler.addUser(UID)).toBe(UID);

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

test("Testing removeEventFromUser", async () => {

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
    const ev = new eventlib.Event(EID, null, null, null, start1, end1, location);
    expect(await scheduler.addEvent(null, null, EID, null, start1, end1, location)).toBe(EID);

    // Add an event with start start2 and end end2
    const EID2 = await scheduler.getNextID();
    expect(EID !== EID2).toBe(true);
    const ev2 = new eventlib.Event(EID2);    
    expect(await scheduler.addEvent(null, null, EID2, null, start2, end2, location)).toBe(EID2);
    expect(await scheduler.addEvent(null, null, 3, null, start2, end2, location)).toBe(3);

    // Add a user with id UID
    expect(await scheduler.addUser(UID)).toBe(UID);
    // Cannot add the same user twice
    expect(await scheduler.addUser(UID)).toBe(-1);

    // Add events EID and EID2 to scheduler
    expect(await scheduler.addEventToUser(UID, EID)).toBe(0);
    expect(await scheduler.addEventToUser(UID, EID2)).toBe(0);

    // Case 1: Remove event from user which doesn't exist, should fail
    expect(await scheduler.removeEventFromUser(-1, -1)).toBe(-1);
    // Case 2: Remove event which doesn't exist, should fail
    expect(await scheduler.removeEventFromUser(UID, -1)).toBe(-1);
    // Case 3: Remove event which the user attends, should succeed
    expect(await scheduler.removeEventFromUser(UID, EID)).toBe(0);
    // Case 4: Remove the same event twice, should fail
    expect(await scheduler.removeEventFromUser(UID, EID)).toBe(-1);
    // Case 5: Adding the event back should not produce a conflict
    expect(await scheduler.addEventToUser(UID, EID)).toBe(0);
    // Case 6: Removing all events should leave the user with no events
    expect(await scheduler.removeEventFromUser(UID, EID)).toBe(0);
    expect(await scheduler.removeEventFromUser(UID, EID2)).toBe(0); 

    let user = await scheduler.getUser(UID);
    expect(user.events).toEqual([]);
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
    expect(await scheduler.addEvent(null, null, evid, null, start1, end1, location)).toBe(evid);
    expect(await scheduler.deleteEvent(evid)).toBe(0);
    
    // At this point the event should not exist
    expect(await scheduler.deleteEvent(evid)).toBe(-1);
    
    // Adding an event and then deleting another event should not work
    expect(await scheduler.addEvent(null, null, evid, null, start1, end1, location)).toBe(evid);
    expect(await scheduler.deleteEvent(-2)).toBe(-1);
    expect(await scheduler.deleteEvent(evid)).toBe(0);
});

test("Testing Friend Functionality", async () => {
    const UID1 = 1;
    const UID2 = 2;
    const UID3 = 3;
    
    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    // Add 3 users
    expect(await scheduler.addUser(UID1)).toBe(UID1);
    expect(await scheduler.addUser(UID2)).toBe(UID2);
    expect(await scheduler.addUser(UID3)).toBe(UID3);
    
    // Case 1: Adding yourself as a friend should not work
    expect(await scheduler.addFriend(UID1, UID1)).toBe(-2);
    // Case 2: An invalid user cannot add another user as a friend
    expect(await scheduler.addFriend(-1, UID1)).toBe(-1);
    // Case 3: Adding an invalid user as a friend should not work
    expect(await scheduler.addFriend(UID1, -1)).toBe(-1);
    // Case 4: Adding another user as a friend should work
    expect(await scheduler.addFriend(UID1, UID2)).toBe(0);
    
    const U1 = await scheduler.getUser(UID1);
    expect(U1.friends.length).toBe(1);
    expect(U1.friends[0].id).toBe(UID2);
    
    // Case 5: Removing invalid friend from a user should not work
    expect(await scheduler.deleteFriend(UID1, -1)).toBe(-1);
    // Case 6: Removing invalid friend from an invalid user should not work
    expect(await scheduler.deleteFriend(-1, -1)).toBe(-1);
    // Case 7: Removing user you are not friends with should not work
    expect(await scheduler.deleteFriend(UID1, UID3)).toBe(-1);
    // Case 8: Removing user you are friends with should work
    expect(await scheduler.deleteFriend(UID1, UID2)).toBe(0);
    // Case 9: Removing friend should remove friend for the other user
    expect(await scheduler.deleteFriend(UID2, UID1)).toBe(-1);

    const U2 = await scheduler.getUser(UID1);
    expect(U2.friends).toEqual([]);
});

test("Testing Request Functionality", async () => {
    const UID1 = 1;
    const UID2 = 2;
    const UID3 = 3;
    
    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    // Add 3 users
    expect(await scheduler.addUser(UID1)).toBe(UID1);
    expect(await scheduler.addUser(UID2)).toBe(UID2);
    expect(await scheduler.addUser(UID3)).toBe(UID3);
    
    // Case 1: Requesting yourself should not work
    expect(await scheduler.addRequest(UID1, UID1)).toBe(-1);
    // Case 2: An invalid user cannot add another user as a request
    expect(await scheduler.addRequest(-1, UID1)).toBe(-1);
    // Case 3: Adding an invalid user as a request should not work
    expect(await scheduler.addRequest(UID1, -1)).toBe(-1);
    // Case 4: Adding another user as a request should work
    expect(await scheduler.addRequest(UID1, UID2)).toBe(0);
    
    const U1 = await scheduler.getUser(UID1);
    expect(U1.requestout.length).toBe(1);
    expect(U1.requestout[0]).toBe(UID2);
    
    // Case 5: Removing invalid request from a user should not work
    expect(await scheduler.deleteRequest(UID1, -1)).toBe(-1);
    // Case 6: Removing invalid request from an invalid user should not work
    expect(await scheduler.deleteRequest(-1, -1)).toBe(-1);
    // Case 7: Removing user you have not requested should not work
    expect(await scheduler.deleteRequest(UID1, UID3)).toBe(-1);
    // Case 8: Removing user you have requested should work
    expect(await scheduler.deleteRequest(UID1, UID2)).toBe(0);

    const U2 = await scheduler.getUser(UID1);
    expect(U2.friends).toEqual([]);
});

test("Test Other Functions", async () => {
    const UID1 = "TestUser";
    const UID2 = "TestUser2";
    const UID3 = "TestUser3";
    const evid1 = 1;
    const evid2 = 2;
    const evid3 = 3;
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const location = {"lat":0, "long":0};
    
    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    // Add 3 users and 3 events
    expect(await scheduler.addUser(UID1)).toBe(UID1);
    expect(await scheduler.addUser(UID2)).toBe(UID2);
    expect(await scheduler.addFriend(UID1, UID2)).toBe(0);
    expect(await scheduler.addUser(UID3)).toBe(UID3);
    expect(await scheduler.addEvent(null, null, evid1, null, start1, end1, location, [UID1])).toBe(evid1);
    expect(await scheduler.addEvent(null, UID1, evid2, null, start1, end1, location)).toBe(evid2);
    expect(await scheduler.addEvent(null, null, evid3, null, start1, end1, location, [UID2])).toBe(evid3);

    const list1 = await scheduler.getHostEvents(UID1);
    expect(list1.length).toBe(1);
    
    const list2 = await scheduler.getAttendeeEvents(UID1);
    expect(list2.length).toBe(1);
    
    const list3 = await scheduler.searchEvents(UID1);
    expect(list3[0]).toEqual({
        attendees: [UID2],
        host: null,
        id: evid3
    });
    
    const list4 = await scheduler.searchFriends(UID1);
    let ids = [];
    for (let user of list4) {
        ids.push(user.id);
    }
    expect(ids.includes(UID1)).toBe(false);
    expect(ids.includes(UID2)).toBe(false);
    expect(ids.includes(UID3)).toBe(true);
});
