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

/* reset()
 * 
 * Resets the scheduler's state.
 * 
 * After reset, no events or users exist.
 * 
 * Returns a negative value on failure, and 0 on success
 * 
*/
module.exports.reset = async () => {
	data.events = {};
	data.users = {};
	data.nextID = null;
	data.lastID = null;
	return await updateData("scheduler", data);
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
	if ((!_id) || (data.events.hasOwnProperty(_id) && !(data.events[_id].isValid()))){
		return -1;
	} else {
		let newEvent = new eventlib.Event(_id, _name, _desc, _start, _end);

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
	if (!data.nextID){
		return 1;
	} else {
		return data.nextID;
	}
}

/* getEvent(id)
 *  params: (optional) id - integer, id of event to fetch
 *  returns: event with id id. If id is not passed in,
 *   it returns the last event added
 */
module.exports.getEvent = async (id) => {
	if (!id) {
		if (!data.lastID) {
			return null;
		} else {
			return data.events[data.lastID];
		}
	} else if (!data.events.hasOwnProperty(id)){
		return null;
	} else {
		return data.events[id];
	}
}
