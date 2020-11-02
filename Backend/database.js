'use strict';
const fs = require("fs");
const data = require("./data/scheduler.json");

const timeout = 60 * 1000; // Time between disk writes in milliseconds

/* updateData(filename, input)
 *  params:
 *    _filename - string, filename to save to without extension
 *    input     - object, object to save to JSON
 *  returns:
 *    -1 if the write fails
 *    0  if the write succeeds
 */
function updateData(filename, input) {
	fs.writeFile(`./data/${filename}.json`, JSON.stringify(input), (err) => {
		if (err) {
			console.log(err);
			return -1;
		} else return 0;
	});
	
	return 0;
}

const interval = setInterval(updateData, timeout, "scheduler", data);

/* setData(path, obj)
 *  params:
 *   path - path: string containing array of keys, separated by /
 *   obj  - object to put into the database
 *  returns:
 *   a negative value on failure and 0 on success
*/
module.exports.setData = (path, obj) => {
	let keys = path.split("/");
	if (keys.length <= 0) {
		return -1;
	} else {
		var node = data;
		for (var i = 0; i < (keys.length - 1); i++) {
			let key = keys[i];
			if (node.hasOwnProperty(keys[i])) {
				node = node[keys[i]];
			} else {
				return -1;
			}
		}
		
		let key = keys[keys.length - 1];
		if (key) {
			node[key] = obj;
			return 0;
		} else {
			return -1;
		}
	}
}

/* getData(path)
 *  params:
 *   path - string containing array of keys, separated by /
 *  returns:
 *   the object on success, null on failure
*/
module.exports.getData = (path) => {
	let keys = path.split("/");
	if (keys.length <= 0) {
		return null;
	} else {
		var node = data;
		for (var i = 0; i < (keys.length - 1); i++) {
			let key = keys[i];
			if (node.hasOwnProperty(keys[i])) {
				node = node[keys[i]];
			} else {
				return null;
			}
		}
		
		let key = keys[keys.length - 1];
		if (key) {
			return node[key];
		} else {
			return null;
		}
	}
}

/* hasKey(path)
 *  params:
 *   path - string containing array of keys, separated by /
 *  returns:
 *   true if the key exists, false otherwise
*/
module.exports.hasKey = (path) => {
	return (getData(path) !== null);
}
