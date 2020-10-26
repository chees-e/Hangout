const fs = require("fs");
const events = require("./data/events.json");

async function updateData(filename, input) {
	fs.writeFile(`./data/${filename}.json`, JSON.stringify(input), (err) => {
		if (err) {
			console.log(err);
			return -1;
		} else return 0;
	});
}

module.exports.addEvent = async (_name, _id, _desc, _start, _end) => {
	let newEvent = {
		name: _name,
		id: _id,
		desc: _desc,
		start: _start,
		end: _end,
	}

	console.log(newEvent);

	events.unshift(newEvent);
	let rv = await updateData("events", events);

	if (rv == 0) return newEvent.id;
	else return rv;
}

//Temporary skeleto code to get the first event
module.exports.getEvent = async () => {
	console.log(events);
	return events[0];

}
