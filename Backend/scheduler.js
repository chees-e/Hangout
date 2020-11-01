'use strict';

const eventlib = require("./eventlib.js");
const data = require("./database.js");

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
	if (data.setData("events", {}) !== 0) {
		return -1;
	} else if (data.setData("users", {}) !== 0) {
		return -1;
	} else if (data.setData("impls", {}) !== 0) {
		return -1;
	} else if (data.setData("nextID", 1) !== 0) {
		return -1;
	} else if (data.setData("lastID", null) !== 0) {
		return -1;
	}
	return 0;
}

/* getEventImpl(_id)
 *  params:
 *   _id - string or null / undefined
 *  returns:
 *   null if event does not exist, the event otherwise
 */
function getEventImpl(_id) {
	const eventData = data.getData(`events/${_id}`);
	if ((!_id) || (!eventData)) {
		return null;
	} else {
		return new eventlib.Event(parseInt(eventData.id), eventData.name,
								  eventData.desc, new Date(eventData.start),
								  new Date(eventData.end), eventData.location);
	}
}

/* getUserImpl(_id)
 *  params:
 *   _id - string or null / undefined
 *  returns:
 *   null if the impl does not exist, the user otherwise
*/
function getUserImpl(_id) {
	const userData = data.getData(`users/${_id}`);
	if ((!_id) || (!userData)) {
		return null;
	} else {
		let user = new eventlib.User(userData.id);
		user.events = userData.events;
		return user;
	}
}

/* getImpl(_id)
 *  params:
 *   _id - string or null / undefined
 *  returns:
 *   null if the impl does not exist, the impl otherwise
*/
function getImpl(_id) {
	const implData = data.getData(`impls/${_id}`);
	if ((!_id) || (!implData)) {
		return null;
	} else {
		let impl = new eventlib.EventImpl(implData.id);
		impl.timeslots = implData.timeslots;
	}
}

/* addEvent(_name, _id, _desc, _start, _end, _location)
 *  params: 
 *   _name  - string, name of event
 *   _id    - int, unique id of event
 *   _desc  - string, event description
 *   _start - Date, event start date and time
 *   _end   - Date, event end date and time
 *   _location - Object, location of event
 *  returns: A negative value if the operation failed, the id if the
 *    operation was successful
 *
 * If a valid event with the same id already exists, the scheduler is not modified
 */
module.exports.addEvent = async (_name, _id, _desc, _start, _end, _location) => {
	const oldEvent = getEventImpl(_id);
	if ((oldEvent) && (oldEvent.isValid())) {
		return -1;
	} else {
		let id = _id;
		if (!_id) {
			id = getNextID();
		}
		
		let newEvent = new eventlib.Event(id, _name, _desc, _start, _end, _location);
		let newImpl = new eventlib.EventImpl(id);
		newImpl.importEvent(newEvent);

		if (data.setData(`events/${id}`, newEvent) === 0) {
			data.setData("lastID", id);
			data.setData("nextID", Math.max(data.getData("nextID") - 1, id) + 1);
			data.setData(`impls/${id}`, newImpl);
			return id;
		} else {
			return -1;
		}
	}
}

/* deleteEvent(id)
 *  params: id - ID of event to remove
 *  returns: a negative value on failure and 0 on success
 */
module.exports.deleteEvent = async (_id) => {
	const eventmap = data.getData("events");
	if (eventmap.hasOwnProperty(_id)) {
		delete eventmap[_id];
		return data.setData("events", eventmap);
	} else {
		return -1;
	}
}

/* getNextID()
*   returns: An ID which is guaranteed to be available.
*/
module.exports.getNextID = () => {
	const id = data.getData("nextID");
	if (!id){
		return 1;
	} else {
		return id;
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
			return getEventImpl(data.lastID);
		}
	} else {
		return getEventImpl(id);
	}
}

/* getAllEvents()
 *  params: none
 *  returns: array containing all valid events
 *
 */
module.exports.getAllEvents = () => {
	var evts = new Array();
	const eventmap = data.getData("events");
	for (const [key, _] of Object.entries(eventmap)) {
		const value = getEventImpl(key);
		if (value && value.isValid()) {
			evts.push(value);
		}
	}
	return evts;
}

/* addUser(_id)
 *  params: _id - user id, must not collide with event ids
 *  returns: negative value on failure and _id on success
*/
module.exports.addUser = async (_id) => {
	let users = data.getData("users");
	if (users.hasOwnProperty(_id)) {
		return -1;
	} else {
		users[_id] = new eventlib.User(_id);
		data.setData("users", users);

		let impls = data.getData("impls");
		impls[_id] = new eventlib.EventImpl(_id);
		data.setData("impls", impls);
		
		return _id;
	}
}

/* getUser(id)
 *  params:
 *   id: user id
 *  returns:
 *   null if the user does not exist or the user's data if the user exists
*/
module.exports.getUser = async (_uid) => {
	return getUserImpl(_uid);
}
