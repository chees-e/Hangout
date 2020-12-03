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
    //
    let attendeearray = [];

    if (req.body.attendees) {
        attendeearray = req.body.attendees.split("+");
    }
    let evnt = new eventlib.Event(id, req.body.host, req.body.name, req.body.description,
                                  new Date(req.body.start), new Date(req.body.end),
                                  req.body.location, attendeearray);
    if (!evnt.isValid()) {
        rv = sched.addEvent(null, null, id, null, new Date(0), new Date(0), null, []);
    } else {
        rv = sched.addEvent(evnt.name, evnt.host, evnt.id, evnt.desc, evnt.start, evnt.end, evnt.location, evnt.attendees);
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
            let toSend = {  
                name: evnt.name,
                host: evnt.host,
                id: evnt.id,
                desc: evnt.desc,
                start: evnt.start,
                end: evnt.end,
                location: evnt.location,
                attendees: []
            };
            for (let attendee of toSend.attendees) {
                sched.getUser(attendee).then((user) => {
                    /*attendees.push({
                        id: user.id,
                        name: user.name,
                        pfp: user.pfp
                    });*/
                    //only adding name for now because
                    //thats what we need for the frontend
                    toSend.attendees.push(user.name);
                });
            }
            res.send(toSend);
        }
    });
});

app.post("/user/", function(req, res) {
//  console.log(req)
    sched.addUser(req.body.id, req.body.name, req.body.device, req.body.pfp).then((code) => {
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
            let friendids = user.getFriends();
            let friendlist = [];
            for (let friend of friendids) {
                friendlist.push({
                    id: friend.id,
                    name: friend.name,
                    pfp: friend.pfp
                });
            }
            res.send({
                id: user.id,
                name: user.name,
                events: user.events,
                friends: friendlist,
                requestin: user.requestin,
                requestout: user.requestout,
                pfp: user.pfp
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

app.delete("/user/:uid/event/:eid", function(req, res) {
    sched.removeEventFromUser(req.params.uid, req.params.eid).then((code) => {
        if (code < 0) {
            res.status(404).send({msg:"Invalid IDs"});
        } else {
            res.send({msg:"Success"});
        }
    });
});

//TODO
app.get("/user/:uid/event/", function(req, res) {
    sched.getHostEvents(req.params.uid).then((hevents) => {
        sched.getAttendeeEvents(req.params.uid).then((aevents) => {
//          console.log(hevents);
            res.send({
                host: hevents,
                attendee: aevents
            }); 
        });
    });
});
//Detect invalid requests
app.get("/user/:uid/findevent/", function(req, res) {
    sched.searchEvents(req.params.uid).then((events) => {
        res.send({
            length: events.length,
            events
        }); 
    });
});

app.get("/user/:uid/findfriends", function(req, res) {
//    console.log(req)
    sched.searchFriends(req.params.uid).then( (userlist) => {
        if (userlist.length === 0){
            res.status(404).send({msg:"No users"});
        } else {
            let rv = [];
            for (let user of userlist) {
                rv.push({
                    id: user.id,
                    name: user.name,
                    pfp: user.pfp
                });
            }
//          console.log(rv)
            res.send({
                length : rv.length,
                users: rv
            });
        }
    });
    
});

app.get("/user/", function(req, res) {
    sched.getAllUsers().then( (userlist) => {
        if (userlist.length === 0){
            res.status(404).send({msg:"No users"});
        } else {
            res.send({
                length : userlist.length,
                users : userlist
            });
        }
    });
    
});

app.post("/user/:uid/friend/:fid", function(req, res) {
    sched.addFriend(req.params.uid, req.params.fid).then((code) => {
        if (code === -2) {
            res.status(409).send({msg:"A conflict was detected"});
        } else if (code === -1) {
            res.status(404).send({msg:"User/Friend not found"});
        } else {
            sched.deleteRequest(req.params.uid, req.params.fid);
            res.send({msg: "Friend added successfully"});
        }

    });
});

app.delete("/user/:uid/friend/:fid", function(req, res) {
    sched.deleteFriend(req.params.uid, req.params.fid).then((code) => {
        if (code === -2) {
            res.status(409).send({msg:"A conflict was detected"});
        } else if (code < 0) {
            res.status(404).send({msg:"User/Friend not found"});
        } else {
            res.send({msg: "Friend deleted successfully"});
        }

    });
});

//from uid to fid
app.post("/user/:uid/request/:fid", function(req, res) {
    sched.addRequest(req.params.uid, req.params.fid).then( (code) => {
        if (code < 0) {
            res.status(404).send({msg:"User/Friend not found"});
        } else {
            res.send({msg:"Request added successfully"});
        }
    });
});

app.delete("/user/:uid/request/:fid", function(req, res) {
    sched.deleteRequest(req.params.uid, req.params.fid).then( (code) => {
        if (code < 0) {
            res.status(404).send({msg:"User/Friend not found"});
        } else {
            res.send({msg:"Request deleted successfully"});
        }
    });
});

//To be removed
app.get("/reset/confirm", function(req, res) {
    sched.reset().then((code) => {
        if (code >= 0) { res.send({msg:"Everythin deleted successfully"}); }
    });
});


module.exports = app;
