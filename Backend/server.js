'use strict';

const express = require("express");
const http = require("http");
const app = express()
const database = require("./scheduler.js");

app.use(express.json());
 
app.get('/test', function(req, res) {
    res.send({text:"Hello world"})
});

app.get('/time', function(req, res) {
	let curr = new Date();
	res.send(curr.toString());
});

app.post('/event/', function(req, res) {
	let rv = 0;
	let id = database.getNextID();
	if (!req.query.name || !req.query.id || !req.query.desc || !req.query.start || !req.query.end
	    || (parseInt(req.query.id) === NaN) || !req.query.location) {
		rv = database.addEvent(null, id, null, null, null, null);
	} else {
		id = parseInt(req.query.id);
		rv = database.addEvent(req.query.name, id, req.query.desc, new Date(req.query.start), new Date(req.query.end), req.query.location);
	}
	rv.then((code) => {
		if ((!code) || (code <= 0)) {
			res.status(409).send({msg:"Event exists"});
		} else {
			res.status(201).send({"id":`${id}`});
		}
	});
});

app.get('/event/', function(req, res) {
	let events = database.getAllEvents();
	if (events.length === 0){
		res.status(404).send({msg:"No events"});
	} else {
		res.send({
			"length" : events.length,
			"events" : events
		});
	}
});

app.delete('/event/:id', function(req, res) {
	database.deleteEvent(req.params.id).then((code) => {
		if (code < 0) {
			res.status(404).send({msg:"Event not found"});
		} else {
			res.send({msg:"Event deleted successfully"});
		}
	});
});

app.get('/event/:id', function(req, res) {
	database.getEvent(req.params.id).then((evnt) => {
		if (!evnt) {
			res.status(404).send({msg:"Event not found"});
		} else if (!evnt.isValid()) {
			res.status(404).send({msg:"Event is invalid"});
		} else {
			res.send({  
				name: evnt.name,
				id: evnt.id,
				desc: evnt.desc,
				start: evnt.start,
				end: evnt.end,
				location: evnt.location
			});
		}
	});
});

app.post('/user/:id', function(req, res) {
	database.addUser(req.params.id).then((code) => {
		if (code < 0) {
			res.status(409).send({msg:"User already exists"});
		} else {
			res.status(201).send({id:req.params.id});
		}
	});
});

app.get('/user/:id', function(req, res) {
	database.getUser(req.params.id).then((user) => {
		if (!user) {
			res.status(404).send({msg:"User not found"});
		} else {
			res.send({
				id: user.id,
				events: user.events
			});
		}
	});
});

var server = app.listen(8081, function () {
   var host = server.address().address
   var port = server.address().port
   console.log("Example app listening at http://%s:%s", host, port)
})


