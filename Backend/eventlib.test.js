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
    const end2 = new Date(2020, 10, 24, 16, 30);
    
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
    
    // Non-conflicting events
    expect(evImpl.conflicts(evImpl3)).toBe(false);
        
    // evImpl is a subset of evImpl2
    expect(evImpl2.conflicts(evImpl3)).toBe(true);
});
