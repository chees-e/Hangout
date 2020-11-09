"use strict";

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
    let initialState = new Map([
        ["events", new Map()],
        ["users", new Map()],
        ["impls", new Map()],
        ["nextID", 1],
        ["lastID", null]
    ]);
    for (let [key, val] of initialState) {
        if (data.setData(key, val) !== 0) {
            return -1;
        }
    }
    return 0;
};

/* getEventImpl(_id)
 *  params:
 *   _id - string or null / undefined
 *  returns:
 *   A null event if event does not exist, the event otherwise
 */
async function getEventImpl(_id) {
    const eventData = await data.getData(`events/${_id}`);
    if ((!_id) || (!eventData)) {
        return new eventlib.Event(null, null, null, null, null, null);
    } else {
        return new eventlib.Event(parseInt(eventData.id, 10), eventData.name,
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
async function getUserImpl(_id) {
    let userData = await data.getData(`users/${_id}`);
    if ((!_id) || (!userData)) {
        return null;
    } else {
        let user = new eventlib.User(userData.id);
        user.events = userData.events;
        user.friends = userData.friends;
        return user;
    }
}

/* getImpl(_id)
 *  params:
 *   _id - string or null / undefined
 *  returns:
 *   null if the impl does not exist, the impl otherwise
*/
async function getImpl(_id) {
    const implData = await data.getData(`impls/${_id}`);
    if ((!_id) || (!implData)) {
        return null;
    } else {
        let impl = new eventlib.EventImpl(implData.id);
        impl.timeslots = implData.timeslots;
        return impl;
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
    const evnt = await getEventImpl(_id);
    if (evnt.isValid()) {
        return -1;
    } else {
        let id = _id;
        if (!_id) {
            id = module.exports.getNextID();
        }
        
        let newEvent = new eventlib.Event(id, _name, _desc, _start, _end, _location);
        let newImpl = new eventlib.EventImpl(id);
        newImpl.importEvent(newEvent);

        if (await data.setData(`events/${id}`, newEvent) === 0) {
            await data.setData("lastID", id);
            let tmp = Math.max(data.getData("nextID") - 1, id) + 1;
            await data.setData("nextID", tmp);
//            await data.setData(`impls/${id}`, newImpl);
            return id;
        } else {
            return -1;
        }
    }
};

/* deleteEvent(id)
 *  params: id - ID of event to remove
 *  returns: a negative value on failure and 0 on success
 */
module.exports.deleteEvent = async (_id) => {
    const eventmap = data.getData("events");
    if (eventmap.has(_id)) {
        eventmap.delete(_id);
        return data.setData("events", eventmap);
    } else {
        return -1;
    }
};

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
};

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
};

/* getAllEvents()
 *  params: none
 *  returns: array containing all valid events
 *
 */
module.exports.getAllEvents = async () => {
    var evts = new Array();
    const eventmap = await data.getKeys("events");
    for (const _ of eventmap) {
        const value = await getEventImpl(_.id);
        if (value.isValid()) {
            evts.push(value);
        }
    }
    return evts;
};

/* addUser(_id)
 *  params: _id - user id, must not collide with event ids
 *  returns: negative value on failure and _id on success
*/
module.exports.addUser = async (_id) => {
    if (data.hasKey(`users/${_id}`)) {
        return -1;
    } else {
        data.setData(`users/${_id}`, new eventlib.User(_id));
        data.setData(`impls/${_id}`, new eventlib.EventImpl(_id));
        
        return _id;
    }
};

/* addEventToUser(uid, eid)
 *  params:
 *   uid: user id
 *   eid: event id
 *  returns:
 *   negative value on failure and 0 on success
*/
module.exports.addEventToUser = async (_uid, _eid) => {
    const user = getUserImpl(_uid);
    const evnt = getEventImpl(_eid);
    const uimpl = getImpl(_uid);
    const eimpl = getImpl(_eid);
    if (!(user && evnt.isValid())){
        return -2;
    } else if (uimpl.conflicts(eimpl)){
        return -1;
    } else {
        user.addEvent(evnt);
        uimpl.importEvent(evnt);
        
        await data.setData(`users/${_uid}`, user);
        await data.setData(`impls/${_uid}`, uimpl);
        return 0;
    }
};

/* getUser(id)
 *  params:
 *   id: user id
 *  returns:
 *   null if the user does not exist or the user's data if the user exists
*/
module.exports.getUser = async (_uid) => {
    let dat = await getUserImpl(_uid);
    return dat;
};
