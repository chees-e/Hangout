"use strict";

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
        this.id = `${id}`;
        this.name = name;
        this.desc = desc;
        if (start < end) {
            this.start = start;
            this.end = end;
        } else {
            this.start = end;
            this.end = start;
        }
        this.location = location;
        this.attendees = [];
    }
    
    // Check if event is valid
    isValid() {
        return ((this.start instanceof Date) && (this.end instanceof Date)
             && (this.id >= 1) && (this.location));
    }
    
    // Two events are equal if their hashes are equal
    hash() {
        if (this.isValid()) {
            return `${this.id} ${this.start} ${this.end}`;
        } else {
            return null;
        }
    }
    
    equals(other) {
        if (other instanceof Event && this.isValid() && other.isValid()) {
            return this.hash() === other.hash();
        } else {
            return false; // Invalid events cannot be equal
        }
    }

    calculateScore(user) {
        if (this.attendees.includes(user.id)) {
            //User already attends this event, no need for recommendation
            return -1;
        }
        //Right now it is just calculating the score based on the attendees
        let score = 0;

        for (let otherUser of this.attendees) {
            if (user.isFriend(otherUser)) {
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
        if (this.events.includes(event.id)) {
            return false;
        } else {
            this.events.push(event.id);
            return true;
        }
    }

    /* addFriend(id);
     *
     * Params: id - User id of friend to add
     * Returns: true if the friend was successfully added, false otherwise
     * 
     */
    addFriend(id){
        if (this.friends.includes(id)) {
            return false;
        } else {
            this.friends.push(id);
            return true;
        }
    }
    /* getEvents();
     * 
     * Returns: the events the user currently attends
    */
    getEvents(){
        return this.events.slice();
    }
    /* getFriends();
     * 
     * Returns: a list of (ids of) friends the user have
    */
    getFriends() {
        return this.friends.slice();
    }
    /* isFriend();
     * 
     * Returns: whether the user is friend with the given user
     * used when determining the score of an event
    */
    isFriend(id) {
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
const dayLength = 24 * 60 / TSLength;

function getDayEncoding(date) {
    const minuteMS = 60 * 1000;
    const dayMS = 24 * 60 * minuteMS;
    const zero = new Date(0);
    const diff = date - zero + (date.getTimezoneOffset() * minuteMS);
    return Math.floor(diff / dayMS);
}

/* getTimeslot: Get time slot number from date, using the local time zone
 */
function getTimeslot(date) {
    const minutes = date.getHours() * 60 + date.getMinutes();
    const timeslot = {
        slot: Math.floor(minutes / TSLength),
        day: getDayEncoding(date)
    };
    return timeslot;
}

/* Compares 2 time slots of the form { start: int, length: int, id: int }
 *  returns: 
 *   - A negative value if they are not valid time slots
 *   - 0 if they do not intersect at all
 *   - 1 if they intersect and have the same id
 *   - 2 if they intersect and have different ids
*/
function compare(slot1, slot2) {
    let s1 = slot1;
    let s2 = slot2;
    if (s1.start > s2.start) {
        s1 = slot2;
        s2 = slot1;
    }
    if ((s1.start + s1.length) > (s2.start)) {
        if (s1.id === s2.id) {
            return 1;
        } else {
            return 2;
        }
    } else {
        return 0;
    }
}

// If slot1 contains slot2
function contains(slot1, slot2) {
    return (slot2.start >= slot1.start)
        && ((slot2.start + slot2.length) <= (slot1.start + slot1.length))
        && (slot1.id === slot2.id);
}

// Advance initial according to iter, loopcond, and exitcond
function advance(initial, iter, loopcond, exitcond) {
    while (loopcond(initial)) {
        initial = iter.next();
        if (exitcond(initial)) {
            return initial;
        }
    }
    return initial;
}

// If slot2 is a subset of slot1
// slot1 and slot2 must be sorted by start
function subset(slot1, slot2) {
    const sl1iter = slot1.values();
    let sl1 = sl1iter.next();
    for (let sl2 of slot2) {
        sl1 = advance(sl1, sl1iter, (initial) => (((sl2.start + sl2.length) < initial.value.start)),
                                    (initial) => (initial.done));
        if (sl1.done) {
// There exists at least one event in slot2 which occurs after the end of all events in slot1
            return false;
        }
        if (!contains(sl1.value, sl2)) {
            return false;
        }
    }
    return true;
}

// If slot1 conflicts with slot2
function conflicts(slot1, slot2) {
    const sl2iter = slot2.values();
    let sl2 = sl2iter.next();
    for (let sl1 of slot1) {
        sl2 = advance(sl2, sl2iter, (initial) => (((initial.value.start + initial.value.length) < sl1.start)),
                                    (initial) => (initial.done));
        if (sl2.done) {
            return false;
        }
        
        if (compare(sl1, sl2.value) !== 0) {
            return true;
        }
    }
    return false;
}

function mapEquals(map1, map2, func) {
    for (let [key, val] of map1) {
        let val2 = map2.get(key);
        if (!(val2 && func(val, val2))) {
            return false;
        }
    }
    return true;
}

class EventImpl{
    constructor(id){
        this.id = id;
        this.timeslots = new Map();
    }
    // Import single event into time slot table
    importEvent(event){
        var startSlot = getTimeslot(event.start);
        var endSlot = getTimeslot(event.end);
        // At least one day where this event takes up the whole day
        if ((startSlot.day + 1) < (endSlot.day - 1)) {
            for (var i = startSlot.day + 1; i < (endSlot.day - 1); i++) {
                this.addTS(i, {
                    start: 0,
                    length: dayLength,
                    id: event.id
                });
            }
        }
        if (startSlot.day < endSlot.day) {
            /* This event lasts from the start time to the end of the first day,
            * and from the start of the last day to the end time
            */
            this.addTS(startSlot.day, {
                start: startSlot.slot,
                length: dayLength - startSlot.slot,
                id: event.id
            });
            this.addTS(endSlot.day, {
                start: 0,
                length: endSlot.slot,
                id: event.id
            });
        } else {
            // This event is contained in a single day
            this.addTS(startSlot.day, {
                start: startSlot.slot,
                length: endSlot.slot - startSlot.slot,
                id: event.id
            });
        }
    }
    // Add time slot of the form { start: int, length: int, id: int } to array id
    addTS(id, timeslot) {
        if (timeslot.length > 0) {
            if (!this.timeslots.has(id)) {
                this.timeslots.set(id, []);
            }
            var arr = this.timeslots.get(id);
            arr.push(timeslot);
            arr = arr.sort((a, b) => (
                (a.start > b.start) ? 1 : -1
            ));
            this.timeslots.set(id, arr);
        }
    }
    /* Helper for attends, conflicts
     * Applies func to every timeslot of this and other in order.
     * behav can either be "all" or "any"
     * If behav is "any", apply returns true if at any time func returns true
     * If behav is "all", apply returns false if at any time func returns false
    */
    apply(func, other, behav) {
        if (!(other instanceof EventImpl)) {
            return false;
        }
        
        const thiskeys = Array.from(this.timeslots.keys());
        const sharedkeys = thiskeys.filter( (key) => other.timeslots.has(key) );
        
        if (behav === "any") {
            return sharedkeys.some( (key) => {
                return func(this.timeslots.get(key), other.timeslots.get(key));
            });
        } else if (behav === "all") {
            return sharedkeys.every( (key) => {
                return func(this.timeslots.get(key), other.timeslots.get(key));
            });
        }
    }
    
    // Check if EventImpl other is a subset of this
    attends(other){
        return this.apply((thisSlot, otherSlot) => subset(thisSlot, otherSlot), other , "all");
    }
    // Equality operator
    equals(other){
        if (!(other instanceof EventImpl)) {
            return false;
        }
        return mapEquals(this.timeslots, other.timeslots, (val1, val2) => {
            return val1.every((elem, idx) => {
                let other = val2.slice(idx, idx+1)[0];
                return other && ((elem.start === other.start)
                    && (elem.length === other.length)
                    && (elem.id === other.id));
            });
        });
        
    }
    // Check if other can be added to this
    conflicts(other){
        return this.apply((thisSlot, otherSlot) => conflicts(thisSlot, otherSlot), other, "any");
    }
    // Serialization for MongoDB
    serialize(){
        return {
            id: this.id,
            timeslots: Array.from(this.timeslots.entries())
        };
    }
}

module.exports = { Event, User, EventImpl };
