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
 * "Design 1" events are represented as a tuple (id, start, end), where id is
 * an integer, and start and end are Dates.
*/
class Event{
	constructor(id, start, end){
		this.id = id;
		this.start = start;
		this.end = end;
		if (new.target === Event) {
			Object.freeze(this);
		}
	}
	equals(other){
		if ((other === null) || !(other instanceof Event)){
			return false;
		} else if ((this.start === null) || (this.end === null)
				|| (other.start === null) || (other.end === null)) {
			return false; // Invalid events cannot be equal
		} else {
			return (this.id === other.id) 
				&& (this.start.getTime() === other.start.getTime())
				&& (this.end.getTime() === other.end.getTime());
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

/* getTimeslot: Get time slot number from date. Only takes into account
 * time of day, according to the local time zone.
 */
function getTimeslot(date){
	var minutes = date.getHours() * 60 + date.getMinutes();
	return Math.floor(minutes / TSLength);
}

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
		var startSlot = getTimeslot(event.start);
		var endSlot = getTimeslot(event.end);
		for (var i = startSlot; i < endSlot; i++){
			this.timeslots[i] = event.id;
		}
	}
	// Check if EventImpl other is a subset of this
	attends(other){
		if (!(other instanceof EventImpl)){
			return false;
		}
		for (var i = 0; i < dayLength; i++){
			if ((other.timeslots[i] !== null)
			&& (other.timeslots[i] !== this.timeslots[i])){
				return false;
			}
		}
		return true;
	}
	// Equality operator
	equals(other){
		if (!(other instanceof EventImpl)){
			return false;
		}
		for (var i = 0; i < dayLength; i++){
			if (this.timeslots[i] === null){
				if (other.timeslots[i] !== null){
					return false;
				}
			} else if (this.timeslots[i] !== other.timeslots[i]){
				return false;
			}
		}
		return true;
	}
}

module.exports = { Event, User, EventImpl };
