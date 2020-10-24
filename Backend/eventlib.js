'use strict';

/* Eventlib: The backend's scheduling code, used to generate user profiles.
 * Right now, it is events are suggested and conflicts are resolved as if
 * all events occur on the same day.
 * 
 * "Design 1" Users are represented by arrays of Events
 * 
 * "Design 2" Users and Events (collectively EventImpls) are represented
 * by arrays of time slots
 */

/* Immutable class Event: Represents the "Design 1" format of an event, as used in the
 * scheduler.
 * 
 * Event name and description can be handled elsewhere, as the scheduler does not
 * yet directly interface with a database.
 * 
 * "Design 1" events are represented as a tuple (id, start, length), where id is
 * an integer, and start and length are Dates.
*/
class Event{
	constructor(id, start, length){
		this.id = id;
		this.start = start;
		this.length = length;
		if (new.target === Event) {
			Object.freeze(this);
		}
	}
}

/* Class User: Represents the "Design 1" format of a person, as used in the scheduler.
 * 
 * This is an output of the scheduling subsystem, which is passed to the profile builder.
 * 
 * A User in the "Design 1" format has a numeric id and a sorted array of Design 1 Events.
 * User details such as name and information is stored elsewhere in the backend.
*/
class User{
	constructor(id){
		this.id = id;
		this.events = [];
	}
	/* addEvent(event);
	 *
	 * Params: event - type Event, event to add to the User's schedule
	 * Returns: true if the event was successfully added, false if there was a conflict
	 * 
	 */
	addEvent(event){
		this.events.push(event);
		return true;
	}
	/* getProfile();
	 * 
	 * Params: none
	 * Returns: A profile string to pass to the frontend.
	 * 
	 * Currently, it just calls JSON.stringify();
	 */
	getProfile(){
		return JSON.stringify(this);
	}
}

/* Class EventImpl: Represents the "Design 2" format of an event or a user.
 * 
 * This is the internal format that the scheduling subsystem uses to represent
 * both events and users. Every EventImpl contains a sorted array of time slots,
 * each of which have a standard length.
 */
 
// 10 minutes, the length of a single time slot
const TSLength = 10; 

/* Number of time slots in a day
 * The current implementation processes event conflicts per day, or time
 * period where events can conflict
 */
const dayLength = 24 * 60 * 60 / TSLength;

class EventImpl{
	constructor(){
		this.id = 0;
		this.timeslots = Array(dayLength).fill(null);
	}
	// Import User with table of events 
	importUser(user){
		for (var i = 0; i < user.events.length; i++){
			this.importEvent(user.events[i]);
		}
	}
	// Import single event into time slot table
	importEvent(event){
	}
}
