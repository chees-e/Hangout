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
	const location = {"lat":0, "long":0};

	// Restore scheduler to known state
	scheduler.reset().then((code) => {
		assert(code === 0);
	});

	const evid = scheduler.getNextID();
	const ev = new eventlib.Event(evid, "Test", "TestDesc", start1, end1, location);

	// Default event: scheduler.getEvent() with no events must not error
	scheduler.getEvent().then((evnt) => {
		assert(evnt === null);
	});

	scheduler.addEvent("Test", evid, "TestDesc", start1, end1, location).then((code) => {
		assert(code === evid);
	});

	const newID = scheduler.getNextID();
	assert(evid !== newID);

	// getEvent must return the last added event
	scheduler.getEvent(evid).then((evnt) => {
		assert(evnt instanceof eventlib.Event);
		assert(evnt.equals(ev));
	});
	
	scheduler.addUser(newID);
	let events3 = scheduler.getAllEvents();
	assertArrEqual(events3, [ev]);  // Adding users must not add events
}

function testAddEventToUser(){

	const start1 = new Date(2020, 10, 24, 10, 45);
	const end1 = new Date(2020, 10, 24, 13, 50);
	const start2 = new Date(2020, 10, 24, 15, 20);
	const end2 = new Date(2020, 10, 24, 16, 30);
	const EID = scheduler.getNextID();
	const UID = "test UID";
	const location = {"lat":0, "long":0};

	// Restore scheduler to known state
	scheduler.reset().then((code) => {
		assert(code === 0);
	});

	var ev = new eventlib.Event(EID, null, null, start1, end1, location);
	const EID2 = scheduler.getNextID();
	var ev2 = new eventlib.Event(EID2, null, null, start2, end2, location);
	
	scheduler.addEvent(ev);
	scheduler.addEvent(ev2);
	scheduler.addUser(UID);
	
	scheduler.addEventToUser(UID, EID).then((code) => {
		assert(code === 0);
	});
	scheduler.addEventToUser(UID, EID).then((code) => {
		assert(code === -1);
	});
	scheduler.addEventToUser(UID, -1).then((code) => {
		assert(code === -2);
	});
	scheduler.addEventToUser(UID, EID2).then((code) => {
		assert(code === 0);
	});
	scheduler.getUser(UID).then((user) => {
		assert(user.events.length === 2);
		assert(user.events[0] === EID);
		assert(user.events[1] === EID2);
	});
}

testAddEvent();
testAddEventToUser();
process.exit(0); // Stop timer from running infinitely
