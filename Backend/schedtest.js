'use strict';

const eventlib = require("./eventlib.js");
const scheduler = require("./scheduler.js");
const assert = require("assert");

function assertArrEqual(ev1, ev2){
	assert(ev1.length === ev2.length);
	for (var i = 0; i < ev1.length; i++){
		if (!(ev1[i] instanceof eventlib.Event)){
			assert(ev1[i] === ev2[i]);
		} else {
			assert(ev1[i].equals(ev2[i]));
		}
	}
}

function testAddEvent(){
	const start1 = new Date(2020, 10, 24, 10, 45);
	const end1 = new Date(2020, 10, 24, 13, 50);

	// Restore scheduler to known state
	scheduler.reset().then((code) => {
		assert(code === 0);
	});

	const evid = scheduler.getNextID();
	const ev = new eventlib.Event(evid, "Test", "TestDesc", start1, end1);

	// Default event: scheduler.getEvent() with no events must not error
	scheduler.getEvent().then((evnt) => {
		assert(evnt === null);
	});

	scheduler.addEvent("Test", evid, "TestDesc", start1, end1).then((code) => {
		assert(code === evid);
	});

	const newID = scheduler.getNextID();
	assert(evid !== newID);

	// getEvent must return the last added event
	scheduler.getEvent(evid).then((evnt) => {
		assert(evnt instanceof eventlib.Event);
		assert(evnt.equals(ev));
	});
	
//	sched.addUser(newID);
//	const events3 = sched.getEvents();
//	assertArrEqual(events3, [ev]);  // Adding users must not add events
}

/*function testAddEventToUser(){
	var sched = new scheduler.Scheduler();

	const start1 = new Date(2020, 10, 24, 10, 45);
	const end1 = new Date(2020, 10, 24, 13, 50);
	const start2 = new Date(2020, 10, 24, 15, 20);
	const end2 = new Date(2020, 10, 24, 16, 30);
	const EID = sched.getNextID();
	const EID2 = sched.getNextID();
	const UID = sched.getNextID();

	var ev = new eventlib.Event(EID, null, null, start1, end1);
	var ev2 = new eventlib.Event(EID2, null, null, start2, end2);
	
	sched.addEvent(ev);
	sched.addEvent(ev2);
	sched.addUser(UID);
	
	assert(sched.addEventToUser(UID, EID));
	assert(!sched.addEventToUser(UID, EID));
	assert(sched.addEventToUser(UID, EID2));
	assertArrEqual(sched.getUserEvents(UID), [ev, ev2]);
}*/

testAddEvent();
//testAddEventToUser();
process.exit(0); // Stop timer from running infinitely
