"use strict";

const app = require("./app.js");
const db = require("./database.js");
const uri = "mongodb://127.0.0.1:27017/";
const dbname = "Hangout";

const server = app.listen(8081, function () {
   db.init(uri, dbname).then();
});
