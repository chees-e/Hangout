'use strict';

var eventlib = require("./eventlib.js");
var scheduler = require("./scheduler.js");
var assert = require("assert");

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
	var sched = new scheduler.Scheduler();

	const start1 = new Date(2020, 10, 24, 10, 45);
	const end1 = new Date(2020, 10, 24, 13, 50);
	const ev = new eventlib.Event(sched.getNextID(), null, null, start1, end1);
	const newID = sched.getNextID();

	assert(ev.id !== newID);
	const events1 = sched.getEvents();
	
	assertArrEqual(events1, []);

	assert(events1.length === 0); // sched.getEvents() must be a valid array
	
	sched.addEvent(ev);
	const events2 = sched.getEvents();
	
	assertArrEqual(events1, []);   // sched.getEvents() must not change prior values
	assertArrEqual(events2, [ev]); // ev must be the only value in events2
	
	sched.addUser(newID);
	const events3 = sched.getEvents();
	
	assertArrEqual(events3, [ev]);  // Adding users must not add events
}

function testAddEventToUser(){
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
}

testAddEvent();
testAddEventToUser();
