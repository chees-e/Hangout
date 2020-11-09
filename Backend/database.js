"use strict";
const fs = require("fs");
const timeout = 60 * 1000; // Time between disk writes in milliseconds

/* stringifyWithMap
 * replacer for JSON.stringify, but it properly handles Maps
 */
function stringifyWithMap(key, value) {
    const obj = this[new String(key)];
    if (obj instanceof Map) {
        const temp = {
            "_type" : "MAP",
            "_values" : obj.values()
        };
        return temp;
    } else {
        return value;
    }
}

/* parseWithMap
 * reviver for JSON.parse, but it properly handles Maps
*/
function parseWithMap(key, value) {
    if (typeof value === "object" && value !== null) {
        if (value._type === "MAP") {
            return new Map(value._values);
        }
    }
    return value;
}

/* updateData(filename, input)
 *  params:
 *    input     - object, object to save to JSON
 *  returns:
 *    -1 if the write fails
 *    0  if the write succeeds
 */
function updateData(input) {
    fs.writeFile("./data/scheduler.json", JSON.stringify(input, stringifyWithMap), (err) => {
        if (err) {
            return -1;
        } else {
            return 0;
        }
    });
    
    return 0;
}

const data = JSON.parse(fs.readFileSync("./data/scheduler.json", "utf-8"), parseWithMap);

/* traverse(json, keys)
 *  params:
 *   json - Object representing JSON structure
 *   keys - Array of keys to traverse
 *  returns:
 *   the node corresponding to json[key1][key2][key3][etc] for 
 *   all key1...keyn in keys, or null if it does not exist
 */
function traverse(json, keys) {
    let node = json;
    for (let key of keys) {
        if ((node instanceof Map) && node.has(key)) {
            node = node.get(key);
        } else {
            return null;
        }
    }
    return node;
}

const interval = setInterval(updateData, timeout, data);

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
        let lastKey = keys.pop();
        let node = traverse(data, keys);

        if (node != null) {
            node.set(lastKey, obj);
            return 0;
        } else {
            return -1;
        }
    }
};

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
        let lastKey = keys.pop();
        let node = traverse(data, keys);

        if (node !== null) {
            return node.get(lastKey);
        } else {
            return null;
        }
    }
};

/* hasKey(path)
 *  params:
 *   path - string containing array of keys, separated by /
 *  returns:
 *   true if the key exists, false otherwise
*/
module.exports.hasKey = (path) => {
    return (module.exports.getData(path) !== null);
};
