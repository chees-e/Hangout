
const express = require("express");
const http = require("http");
const app = express()
const database = require("./database.js");

app.use(express.json())
 
app.get('/test', function(req, res) {
    res.send({text:"Hello world"})
})

app.get('/time', function(req, res) {
	let curr = new Date();
	res.send(curr.toString());
})

app.get('/addEvent/', function(req, res) {
	console.log(req.query.desc);
	
	if (!req.query.name || !req.query.id || !req.query.desc || !req.query.start || !req.query.end) {
		res.status(400).send({msg:"invalid request"});
	} else {
		let rv = database.addEvent(req.query.name, req.query.id, req.query.desc, req.query.start, req.query.end);
	
		res.send({msg:"success"});
	}
})

app.get('/getEvent/', function(req, res) {
	database.getEvent().then((evnt) => {
		console.log(evnt);
		res.send({  
			name: evnt.name,
			id: evnt.id,
			desc: evnt.desc,
			start: evnt.start,
			end: evnt.end
		})
	})
})

var server = app.listen(8081, function () {
   var host = server.address().address
   var port = server.address().port
   console.log("Example app listening at http://%s:%s", host, port)
})


