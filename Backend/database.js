"use strict";
const { MongoClient } = require("mongodb");
const uri = "mongodb://127.0.0.1:27017/";
const client = new MongoClient(uri);
const dbname = "Hangout";
const eventlib = require("./eventlib.js");
var db = null;

module.exports.init = async () => {
    await client.connect();
    db = await client.db(dbname);
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
	let dat = await module.exports.getData(path);
    return (dat !== null);
};
