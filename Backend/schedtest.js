'use strict';

var eventlib = require("./eventlib.js");
var scheduler = require("./scheduler.js");
var assert = require("assert");

function testAddEvent(){
	var start1 = new Date(2020, 10, 24, 10, 45);
	var end1 = new Date(2020, 10, 24, 13, 50);
	var sched = new scheduler.Scheduler();
	var ev = new eventlib.Event(sched.getNextID(), null, null, start1, end1);
	var newID = sched.getNextID();

	assert(ev.id !== newID);
	const events1 = sched.getEvents();
	
	assert(events1.length === 0); // sched.getEvents() must be a valid array
	
	sched.addEvent(ev);
	const events2 = sched.getEvents();
	
	assert(events1.length === 0);  // sched.getEvents() must not change prior values
	assert(events2.length === 1);
	assert(ev.equals(events2[0])); // ev must be the only value in events2
	
	sched.addUser(newID);
	const events3 = sched.getEvents();
	
	assert(events3.length === 1);  // Adding users must not add events
	assert(events2[0] === events3[0]);
}

testAddEvent();
