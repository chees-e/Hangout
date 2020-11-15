"use strict";

jest.unmock("./eventlib.js");

const eventlib = require("./eventlib.js");
const scheduler = require("./scheduler.js");
const assert = require("assert");

jest.mock("./database.js", () => {
    let testData = new Map([
        ["events", new Map()],
        ["users", new Map()],
        ["impls", new Map()],
        ["nextID", 1],
        ["lastID", null]
    ]);
    return {
        setData: async (path, obj) => {
            const serialize = (obj) => {
                if (obj instanceof require("./eventlib.js").EventImpl) {
                    return obj.serialize();
                } else {
                    return obj;
                }
            };
            obj = serialize(obj);
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
                testData.set(keys[0], obj);
                return 0;
            }
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
        }
    };
});

function assertArrEqual(ev1, ev2){
    expect(ev1.length).toBe(ev2.length);
    expect(ev1.every((elem, idx) => {
        let other = ev2.slice(idx, idx+1)[0];
        if (elem instanceof eventlib.Event) {
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

    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    let evid = await scheduler.getNextID();
    const ev = new eventlib.Event(evid, "Test", "TestDesc", start1, end1, location);

    // Default event: scheduler.getEvent() with no events must not error
    expect(await scheduler.getEvent()).toBe(null);

    expect(await scheduler.addEvent("Test", evid, "TestDesc", start1, end1, location))
        .toBe(evid);

    let newID = await scheduler.getNextID();
    assert(evid !== newID);

    // getEvent must return the last added event
    let evnt = await scheduler.getEvent(evid);
    expect(evnt instanceof eventlib.Event).toBe(true);
    expect(evnt.equals(ev)).toBe(true);
    
    await scheduler.addUser(newID);
    let events3 = await scheduler.getAllEvents();
    assertArrEqual(events3, [ev]);  // Adding users must not add events*/
});

test("Testing AddEventToUser", async () => {

    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const start2 = new Date(2020, 10, 24, 15, 20);
    const end2 = new Date(2020, 10, 24, 16, 30);
    const UID = "test UID";
    const location = {"lat":0, "long":0};

    // Restore scheduler to known state
    expect(await scheduler.reset()).toBe(0);

    const EID = await scheduler.getNextID();
    const ev = new eventlib.Event(EID, null, null, start1, end1, location);
    expect(await scheduler.addEvent(EID, null, null, start1, end1, location)).toBe(EID);

    const EID2 = await scheduler.getNextID();
    expect(EID !== EID2).toBe(true);
    const ev2 = new eventlib.Event(EID2, null, null, start2, end2, location);    
    expect(await scheduler.addEvent(EID2, null, null, start2, end2, location)).toBe(EID2);

    await scheduler.addUser(UID);
    
    expect(await scheduler.addEventToUser(UID, EID)).toBe(0);
    expect(await scheduler.addEventToUser(UID, EID)).toBe(-1);
    expect(await scheduler.addEventToUser(UID, -1)).toBe(-2);
    expect(await scheduler.addEventToUser(UID, EID2)).toBe(0);
    let user = await scheduler.getUser(UID);
    expect(user.events.length).toBe(2);
    expect(user.events[0].toString()).toBe(EID.toString());
    expect(user.events[1].toString()).toBe(EID2.toString());
});
