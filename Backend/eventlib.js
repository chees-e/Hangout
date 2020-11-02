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

/* Class Event: Represents the "Design 1" format of an event, as used in the
 * scheduler.
 * 
 * Event name and description can be handled elsewhere, as the scheduler does not
 * yet directly interface with a database.
 * 
 * "Design 1" events are represented as a tuple (id, name, desc, start, end, location),
 * where id is an integer, name and desc are strings, start and end are Dates,
 * and location is an object with format {"lat" : <latitude>, "long" : <longitude>}.
 * 
 * name and desc should be entirely determined by id, so they are not compared
 * in equals
*/

const ATTENDEE_WEIGHT = 1;
const FRIEND_WEIGHT = 20;

class Event{
	constructor(id, name, desc, start, end, location){
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.start = start;
		this.end = end;
		this.location = location;
		this.attendees = [];
	}
	isValid() {
		return ((this.start instanceof Date) && (this.end instanceof Date)
			 && (this.id >= 1) && (this.name) && (this.desc) && (this.location));
	}
	copy() {
		return new Event(this.id, this.name, this.desc, this.start, this.end, this.location);
	}
	equals(other) {
		if ((other === null) || !(other instanceof Event)){
			return false;
		} else if (!((this.start instanceof Date) && (this.end instanceof Date)
				 && (other.start instanceof Date) && (other.end instanceof Date))) {
			return false; // Invalid events cannot be equal
		} else {
			return (this.id === other.id) 
				&& (this.start.getTime() === other.start.getTime())
				&& (this.end.getTime() === other.end.getTime());
		}
	}

	calculateScore(user) {
		if (this.attendees.includes(user.id)) {
			//User already attends this event, no need for recommendation
			return -1;
		}
		//Right now it is just calculating the score based on the attendees
		let score = 0;

		for (let i = 0; i < this.attendees.length; i++) {
			if (user.isFriend(this.attendees[i])) {
				score += FRIEND_WEIGHT;
			} else {
				score += ATTENDEE_WEIGHT;
			}
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
		this.friends = [];
	}
	/* addEvent(event);
	 *
	 * Params: event - type Event, event to add to the User's schedule
	 * Returns: true if the event was successfully added, false if there was a conflict
	 * 
	 */
	addEvent(event){
		this.events.push(event.id);
		return true;
	}

	/* addEvent(event);
	 *
	 * Params: event - type Event, event to add to the User's schedule
	 * Returns: true if the event was successfully added, false if there was a conflict
	 * 
	 */
	addFriend(id){
		this.friends.push(id);
		return true;
	}
	/* getEvents();
	 * 
	 * Returns: the events the user currently attends
	*/
	getEvents(){
		var arrs = [];
		for (var i = 0; i < this.events.length; i++){
			arrs.push(this.events[i]);
		}
		return arrs;
	}
	/* getFriends();
	 * 
	 * Returns: a list of (ids of) friends the user have
	*/
	getFriends() {
		var arrs = [];
		for (var i = 0; i < this.friends.length; i++){
			arrs.push(this.friends[i]);
		}
		return arrs;
	}
	/* isFriend();
	 * 
	 * Returns: whether the user is friend with the given user
	 * used when determining the score of an event
	*/
	ifFriend(id) {
		return this.friends.includes(id);
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
	constructor(id){
		this.id = id;
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
			if (this.timeslots[i] !== other.timeslots[i]){
				return false;
			}
		}
		return true;
	}
	// Check if other can be added to this
	conflicts(other){
		if (!(other instanceof EventImpl)){
			return true;
		}
		for (var i = 0; i < dayLength; i++){
			if ((other.timeslots[i] !== null) && (this.timeslots[i] !== null)){
				return true;
			}
		}
		return false;
	}
}

module.exports = { Event, User, EventImpl };
