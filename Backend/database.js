"use strict";
const { MongoClient } = require("mongodb");
const uri = "mongodb://127.0.0.1:27017/";
const client = new MongoClient(uri, { useUnifiedTopology: true });
const dbname = "Hangout";
const eventlib = require("./eventlib.js");
var db = null;

module.exports.init = async () => {
    await client.connect();
    db = await client.db(dbname);
	console.log("Database initialized");
};

/* setData(path, obj)
 *  params:
 *   path - path: string containing array of keys, separated by /
 *   obj  - object to put into the database
 *  returns:
 *   a negative value on failure and 0 on success
*/
module.exports.setData = async (path, obj) => {
    let keys = path.split("/");
    let collection = null;
    let query = {};
    let update = {};
    if (obj instanceof eventlib.EventImpl) {
        obj = obj.serialize();
    }
    if (keys.length <= 0) {
        return -1;
    } else if (keys.length === 1) {
        collection = "loosedata";
        query = { name : keys[0] };
        update = { name : keys[0], value: obj };
    } else {
        collection = keys[0];
        query = { id : keys[1] };
        update = obj;
    }
    await db.collection(collection).replaceOne(query, update, { upsert : true });
    return 0;
};

/* getData(path)
 *  params:
 *   path - string containing array of keys, separated by /
 *  returns:
 *   the object on success, null on failure
*/
module.exports.getData = async (path) => {
    let keys = path.split("/");
    let collection = null;
    let query = {};
    let update = {};
    if (keys.length <= 0) {
        return -1;
    } else if (keys.length === 1) {
        collection = "loosedata";
        query = { name : keys[0] };
    } else {
        collection = keys[0];
        query = { id : keys[1] };
    }
    let results = await db.collection(collection).find(query).toArray();
    if (results.length <= 0) {
        return null;
    } else {
        return results[0];
    }
};

/* getKeys(collection)
 *  params:
 *   collection - string, collection in database.
 *  returns:
 *   Array of keys in the collection.
 *  Available collections are events, users, and impls
*/
module.exports.getKeys = async (collection) => {
    return await db.collection(collection).find().project({"id" : 1}).toArray();
};

/* hasKey(path)
 *  params:
 *   path - string containing array of keys, separated by /
 *  returns:
 *   true if the key exists, false otherwise
*/
module.exports.hasKey = async (path) => {
    let keys = path.split("/");
    if (keys.length < 1) {
        return false;
    } else if (keys.length === 1) {
        let dat = await module.exports.getData(keys[0]);
        return (dat !== null);
    } else {
        let dat = await module.exports.getKeys(keys[0]);
        return dat.some((_) => (_.id === keys[1]));
    }
};

/* deleteKey(path)
 *  params:
 *   path - string representing element to be deleted, of the form
 *          "collection/key", where collection is one of:
 *          "events", "users", and "impls"
 *  returns:
 *   0 on success, -1 if the key does not exist, -2 if the arguments are bad
*/
module.exports.deleteKey = async (path) => {
    let keys = path.split("/");
    if (keys.length !== 2) {
        return -2;
    } else if (await module.exports.hasKey(path)){
        return (await db.collection(keys[0]).deleteOne({ id : keys[1] })) === 1;
    } else {
        return -1;
    }
};
