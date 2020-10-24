'use strict';

var eventlib = require("./eventlib.js");
var assert = require("assert");

var Event = eventlib.Event;
var User = eventlib.User;
var EventImpl = eventlib.EventImpl;

function testEventEquals(){
	var ev = new eventlib.Event(null, null, null);
	var ev2 = new eventlib.Event(null, null, null);
	assert(!ev.equals(ev2)); // Invalid events

	ev = new eventlib.Event(1, new Date(), new Date());
	ev2 = new eventlib.Event(ev.id, ev.start, ev.end);
	assert(ev.equals(ev2)); // Copying fields
	
	ev2 = new eventlib.Event(2, ev.start, ev.end);
	assert(!ev.equals(ev2)); // Different IDs
	
	ev2 = new eventlib.Event(1, new Date(0), ev.end);
	assert(!ev.equals(ev2)); // Different starts
	
	ev2 = new eventlib.Event(1, ev.start, new Date(0));
	assert(!ev.equals(ev2)); // Different ends
}

function testEventImpl(){
	var start1 = new Date(2020, 10, 24, 10, 45);
	var end1 = new Date(2020, 10, 24, 13, 50);

	var start2 = new Date(2020, 10, 24, 15, 20);
	var end2 = new Date(2020, 10, 24, 16, 30);
	
	var ev = new eventlib.Event(1, start1, end1);
	var ev2 = new eventlib.Event(2, start1, end1);
	var ev3 = new eventlib.Event(3, start2, end2);
	
	// Identical events
	var evImpl = new eventlib.EventImpl();
	var evImpl2 = new eventlib.EventImpl();
	
	evImpl.importEvent(ev);
	evImpl2.importEvent(ev);
	
	assert(evImpl.equals(evImpl2));
	assert(evImpl.attends(evImpl2));
	
	// Different IDs
	evImpl2 = new eventlib.EventImpl();
	evImpl2.importEvent(ev2);
	
	assert(!evImpl.equals(evImpl2));
	assert(!evImpl.attends(evImpl2));
	
	// evImpl is a subset of evImpl2
	evImpl2 = new eventlib.EventImpl();
	evImpl2.importEvent(ev);
	evImpl2.importEvent(ev3);
	
	assert(evImpl2.attends(evImpl));
	assert(!evImpl2.equals(evImpl));
}

testEventEquals();
testEventImpl();
