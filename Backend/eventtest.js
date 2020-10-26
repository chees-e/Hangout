'use strict';

var eventlib = require("./eventlib.js");
var assert = require("assert");

var Event = eventlib.Event;
var User = eventlib.User;
var EventImpl = eventlib.EventImpl;

function testEventEquals(){
	var ev = new eventlib.Event(null, null, null, null, null);
	var ev2 = new eventlib.Event(null, null, null, null, null);
	assert(!ev.equals(ev2)); // Invalid events

	ev = new eventlib.Event(1, null, null, new Date(), new Date());
	ev2 = new eventlib.Event(ev.id, ev.name, ev.desc, ev.start, ev.end);
	assert(ev.equals(ev2)); // Copying fields
	
	ev2 = new eventlib.Event(2, ev.name, ev.desc, ev.start, ev.end);
	assert(!ev.equals(ev2)); // Different IDs

	ev2 = new eventlib.Event(1, "test", ev.desc, ev.start, ev.end);
	assert(ev.equals(ev2)); // Different names should not influence equals
	
	ev2 = new eventlib.Event(1, ev.name, "test", ev.start, ev.end);
	assert(ev.equals(ev2)); // Different descriptions should not influence equals

	ev2 = new eventlib.Event(1, ev.name, ev.desc, new Date(0), ev.end);
	assert(!ev.equals(ev2)); // Different starts
	
	ev2 = new eventlib.Event(1, ev.name, ev.desc, ev.start, new Date(0));
	assert(!ev.equals(ev2)); // Different ends
	
}

function testEventImpl(){
	var start1 = new Date(2020, 10, 24, 10, 45);
	var end1 = new Date(2020, 10, 24, 13, 50);

	var start2 = new Date(2020, 10, 24, 15, 20);
	var end2 = new Date(2020, 10, 24, 16, 30);
	
	var ev = new eventlib.Event(1, null, null, start1, end1);
	var ev2 = new eventlib.Event(2, null, null, start1, end1);
	var ev3 = new eventlib.Event(3, null, null, start2, end2);
	
	// Identical events
	var evImpl = new eventlib.EventImpl(0);
	var evImpl2 = new eventlib.EventImpl(0);
	var evImpl3 = new eventlib.EventImpl(0);
	
	evImpl.importEvent(ev);
	evImpl2.importEvent(ev);
	
	assert(evImpl.equals(evImpl2));
	assert(evImpl.attends(evImpl2));

	// Non-conflicting events
	evImpl3.importEvent(ev3);
	assert(!evImpl.conflicts(evImpl3));
	
	// Different IDs
	evImpl2 = new eventlib.EventImpl(0);
	evImpl2.importEvent(ev2);
	
	assert(!evImpl.equals(evImpl2));
	assert(!evImpl.attends(evImpl2));
	
	// evImpl is a subset of evImpl2
	evImpl2 = new eventlib.EventImpl(0);
	evImpl2.importEvent(ev);
	evImpl2.importEvent(ev3);
	
	assert(evImpl2.attends(evImpl));
	assert(!evImpl2.equals(evImpl));
	assert(evImpl2.conflicts(evImpl3));
}

testEventEquals();
testEventImpl();