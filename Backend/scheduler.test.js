"use strict";

jest.unmock("./eventlib.js");

jest.mock("./database.js", () => {
    let testData = {
        events : {},
        users : {},
        impls : {},
        nextID : 1,
        lastID : null
    };
    return {
        setData: (path, obj) => {
            let keys = path.split("/");
            // Arbitrarily choose event 10 to fail
            if ((keys[0] === "events") || (keys[0] === "users") || (keys[0] === "impls")){
                if (path === "events/10") { 
                    return -1;
                } else if (keys.length === 1) {
                    testData[keys[0]] = obj;
                    return 0;
                } else {
                    testData[keys[0]][keys[1]] = obj;
                    return 0;
                }
            } else {
                testData[keys[0]] = obj;
                return 0;
            }
        },
        getData: (path) => {
            let keys = path.split("/");
            // Arbitrarily choose event 10 to fail
            if ((keys[0] === "events") || (keys[0] === "users") || (keys[0] === "impls")){
                if (path === "events/10") { 
                    return null;
                } else if (keys.length === 1) {
                    return testData[keys[0]];
                } else if (testData[keys[0]].hasOwnProperty(keys[1])) {
                    return testData[keys[0]][keys[1]];
                } else {
                    return null;
                }
            } else {
                return testData[keys[0]];
            }
        },
        hasKey: () => {
            return false;
        }
    };
});

const eventlib = require("./eventlib.js");
const scheduler = require("./scheduler.js");
const assert = require("assert");

function assertArrEqual(ev1, ev2){
    expect(ev1.length).toBe(ev2.length);
    for (var i = 0; i < ev1.length; i++){
        if (!(ev1[i] instanceof eventlib.Event)){
            expect(ev1[i]).toBe(ev2[i]);
        } else {
            expect(ev1[i].equals(ev2[i])).toBe(true);
        }
    }
}

test("Testing addEvent", () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const location = {"lat":0, "long":0};

    // Restore scheduler to known state
    scheduler.reset().then((code) => {
        expect(code).toBe(0);
    });

    const evid = scheduler.getNextID();
    const ev = new eventlib.Event(evid, "Test", "TestDesc", start1, end1, location);

    // Default event: scheduler.getEvent() with no events must not error
    scheduler.getEvent().then((evnt) => {
        expect(evnt).toBe(null);
    });

    scheduler.addEvent("Test", evid, "TestDesc", start1, end1, location).then((code) => {
        expect(code).toBe(evid);
    });

    const newID = scheduler.getNextID();
    assert(evid !== newID);

    // getEvent must return the last added event
    scheduler.getEvent(evid).then((evnt) => {
        expect(evnt instanceof eventlib.Event).toBe(true);
        expect(evnt.equals(ev)).toBe(true);
    });
    
    scheduler.addUser(newID);
    let events3 = scheduler.getAllEvents();
    assertArrEqual(events3, [ev]);  // Adding users must not add events
});

test("Testing AddEventToUser", () => {

    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 10, 24, 16, 30);
    const EID = scheduler.getNextID();
    const UID = "test UID";
    const location = {"lat":0, "long":0};

    // Restore scheduler to known state
    scheduler.reset().then((code) => {
        expect(code).toBe(0);
    });

    const ev = new eventlib.Event(EID, null, null, start1, end1, location);
    const EID2 = scheduler.getNextID();
    expect(EID !== EID2).toBe(true);
    const ev2 = new eventlib.Event(EID2, null, null, start2, end2, location);
    
    scheduler.addEvent(EID, null, null, start1, end1, location);
    scheduler.addEvent(EID2, null, null, start2, end2, location);
    scheduler.addUser(UID);
    
    scheduler.addEventToUser(UID, EID).then((code) => {
        expect(code).toBe(0);
    });
    scheduler.addEventToUser(UID, EID).then((code) => {
        expect(code).toBe(-1);
    });
    scheduler.addEventToUser(UID, -1).then((code) => {
        expect(code).toBe(-2);
    });
    scheduler.addEventToUser(UID, EID2).then((code) => {
        expect(code).toBe(0);
    });
    scheduler.getUser(UID).then((user) => {
        expect(user.events.length).toBe(2);
        expect(user.events[0]).toBe(EID);
        expect(user.events[1]).toBe(EID2);
    });
});
