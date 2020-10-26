'use strict';

var eventlib = require("./eventlib.js");

class Scheduler{
	constructor(){
		this.events = {}; // Map from ids to events
		this.users = {};  // Map from ids to users
		this.impls = {};  // Map from ids to eventImpls
		this.nextID = 1;  // Next available ID
	}
	/* addEvent(event)
	 *  params: event, instance of eventlib.Event
	 *  returns: false if adding failed, true if adding was successful
	 * If the event already exists, the Scheduler is not modified
	 */
	addEvent(event){
		if ((event instanceof eventlib.Event) && !(this.events.hasOwnProperty(event.id))){
			this.events[event.id] = event;
			this.impls[event.id] = new eventlib.EventImpl(event.id);
			this.impls[event.id].importEvent(event);
			return true;
		} else {
			return false;
		}
	}
	/* addUser(user)
	 *  params: user id
	 *  returns: true if add was successful, false otherwise
	 * If the user already exists, the Scheduler is not modified
	*/
	addUser(id){
		if (!this.users.hasOwnProperty(id)){
			this.users[id] = new eventlib.User(id);
			this.impls[id] = new eventlib.EventImpl(id);
			return true;
		} else {
			return false;
		}
	}
	/* getNextID()
	*   returns: An ID which is guaranteed to be available.
	*/
	getNextID(){
		return this.nextID++;
	}
	/* getEvents()
	* 	returns: An array of the available events
	*/
	getEvents(){
		var events = new Array();
		for (const [key, value] of Object.entries(this.events)) {
			events.push(value);
		}
		return events;
	}
}

module.exports = { Scheduler };
