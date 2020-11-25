"use strict";

const express = require("express");
const app = express();
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

app.post("/event/", async function(req, res) {
    let rv = 0;
    let id = await sched.getNextID();
    //Summary = name
    //to be added: host, attendees
    //
    let evnt = new eventlib.Event(id, req.body.name, req.body.description,
                                  new Date(req.body.start), new Date(req.body.end),
                                  req.body.location);
    if (!evnt.isValid()) {
        rv = sched.addEvent(null, id, null, new Date(0), new Date(0), null);
    } else {
        rv = sched.addEvent(evnt.name, evnt.id, evnt.desc, evnt.start, evnt.end, evnt.location);
    }
    rv.then((code) => {
        if ((!code) || (code <= 0)) {
            res.status(409).send({msg:"Event exists"});
        } else {
            res.status(201).send({"id":`${code}`});
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

//To be removed
app.get("/deleteallevents/confirm", function(req, res) {
    sched.getAllEvents().then( (eventlist) => {
        for(let i = 0; i < eventlist.length; i++) {
            let id = eventlist[0]["id"];
            sched.deleteEvent(id).then((code) => {
                if (code < 0) {
                        res.status(404).send({msg:"Event not found"});
                }
            });
        }
        res.send({msg:"Event deleted successfully"});
    });
});


module.exports = app;
