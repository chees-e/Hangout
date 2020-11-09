"use strict";

const express = require("express");
const http = require("http");
const app = express();
const db = require("./database.js");
const sched = require("./scheduler.js");
const eventlib = require("./eventlib.js");

app.use(express.json());
 
app.get("/test", function(req, res) {
    res.send({text:"Hello world"});
});

app.get("/time", function(req, res) {
    let curr = new Date();
    res.send(curr.toString());
});

app.post("/event/", function(req, res) {
    let rv = 0;
    let id = sched.getNextID();
    let evnt = new eventlib.Event(parseInt(req.query.id, 10), req.query.name, req.query.desc,
                                  new Date(req.query.start), new Date(req.query.end),
                                  req.query.location);
    if (!evnt.isValid()) {
        rv = sched.addEvent(null, id, null, new Date(0), new Date(0), null);
    } else {
        id = parseInt(req.query.id, 10);
        rv = sched.addEvent(evnt.name, evnt.id, evnt.desc, evnt.start, evnt.end, evnt.location);
    }
    rv.then((code) => {
        if ((!code) || (code <= 0)) {
            res.status(409).send({msg:"Event exists"});
        } else {
            res.status(201).send({"id":`${id}`});
        }
    });
});

app.get("/event/", function(req, res) {
    sched.getAllEvents().then( (eventlist) => {
        if (eventlist.length === 0){
            res.status(404).send({msg:"No events"});
        } else {
            res.send({
                length : eventlist.length,
                events : eventlist
            });
        }
    });
});

app.delete("/event/:id", function(req, res) {
    sched.deleteEvent(req.params.id).then((code) => {
        if (code < 0) {
            res.status(404).send({msg:"Event not found"});
        } else {
            res.send({msg:"Event deleted successfully"});
        }
    });
});

app.get("/event/:id", function(req, res) {
    sched.getEvent(req.params.id).then((evnt) => {
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

app.post("/user/:id", function(req, res) {
    sched.addUser(req.params.id).then((code) => {
        if (code < 0) {
            res.status(409).send({msg:"User already exists"});
        } else {
            res.status(201).send({id:req.params.id});
        }
    });
});

app.get("/user/:id", function(req, res) {
    sched.getUser(req.params.id).then((user) => {
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

app.post("/user/:uid/event/:eid", function(req, res) {
    sched.addEventToUser(req.params.uid, req.params.eid).then((code) => {
        if (code === -2) {
            res.status(404).send({msg:"Invalid IDs"});
        } else if (code === -1) {
            res.status(409).send({msg:"Conflict detected"});
        } else {
            res.send({msg:"Success"});
        }
    });
});

var server = app.listen(8081, function () {
   db.init().then();
});


