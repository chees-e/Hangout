'use strict';

const eventlib = require("./eventlib.js");
const fs = require("fs");

const data = require("./data/scheduler.json");

/* updateData(filename, input)
 *  params:
 *    _filename - string, filename to save to without extension
 *    input     - object, object to save to JSON
 *  returns:
 *    -1 if the write fails
 *    0  if the write succeeds
 */
async function updateData(filename, input) {
	fs.writeFile(`./data/${filename}.json`, JSON.stringify(input), (err) => {
		if (err) {
			console.log(err);
			return -1;
		} else return 0;
	});
	
	return 0;
}

/* addEvent(_name, _id, _desc, _start, _end)
 *  params: 
 *   _name  - string, name of event
 *   _id    - int, unique id of event
 *   _desc  - string, event description
 *   _start - Date, event start date and time
 *   _end   - Date, event end date and time
 *  returns: A negative value if the operation failed, the id if the
 *    operation was successful
 *
 * If an event with the same id already exists, the scheduler is not modified
 */
module.exports.addEvent = async (_name, _id, _desc, _start, _end) => {
	if (data.events.hasOwnProperty(_id)){
		return -1;
	} else {
		let newEvent = new eventlib.Event(_id, _name, _desc, new Date(_start), new Date(_end));

		if ((newEvent.start === NaN) || (newEvent.end === NaN)) {
			return -1;
		}

		data.events[_id] = newEvent;

		let oldNextID = data.nextID;
		let oldLastID = data.lastID;
		
		data.lastID = _id;
		data.nextID = Math.max(data.nextID - 1, _id) + 1;

		let rv = await updateData("scheduler", data);
		
		if (rv === 0) {
			return data.lastID;
		} else {
			data.lastID = oldLastID;
			data.nextID = oldNextID;
			delete data.events[_id];
			return rv;
		}
	}
}


/* getNextID()
*   returns: An ID which is guaranteed to be available.
*/
module.exports.getNextID = () => {
	return data.nextID;
}

/* getEvent
 *  returns: most recently added event
 */
module.exports.getEvent = async () => {
	if (data.lastID === -1){
		return new eventlib.Event(-1, "", "", null, null);
	} else {
		return data.events[data.lastID];
	}
}
