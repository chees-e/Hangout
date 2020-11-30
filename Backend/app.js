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

app.post("/user/", function(req, res) {
	console.log(req)
    sched.addUser(req.body.id, req.body.name, req.body.device).then((code) => {
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
			for (let i = 0; i < friendids.length; i++) {
				frindlist.push({
					id: friendids[i].id,
					name: friendids[i].name
				});
			}
            res.send({
                id: user.id,
				name: user.name,
                events: user.events,
				friends: friendlist,
				requestin: user.requestin,
				requestout: user.requestout
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

});
//Detect invalid requests
app.get("/user/:uid/findevent/", function(req, res) {

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
	sched.addFriend(req.param.uid, req.param.fid).then((code) => {
		if (code < 0) {
			res.status(404).send({msg:"User/Friend not found"});
		} else {
			sched.deleteRequest(req.param.uid, req.param.fid);
        	res.send({msg: "Friend added successfully"});
		}

	});
});

app.delete("/user/:uid/friend/:fid", function(req, res) {
	sched.deleteFriend(req.param.uid, req.param.fid).then((code) => {
		if (code < 0) {
			res.status(404).send({msg:"User/Friend not found"});
		} else {
        	res.send({msg: "Friend deleted successfully"});
		}

	});
});

//from uid to fid
app.post("/user/:uid/request/:fid", function(req, res) {
	sched.addRequest(req.param.uid, req.param.fid).then( (code) => {
		if (code < 0) {
			res.status(404).send({msg:"User/Friend not found"});
		} else {
        	res.send({msg:"Request added successfully"});
		}
	});
});

app.delete("/user/:uid/request/:fid", function(req, res) {
	sched.deleteRequest(req.param.uid, req.param.fid).then( (code) => {
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
