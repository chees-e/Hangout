"use strict";
require("dotenv").config();
const request = require("request");
const geourl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
const token = process.env.TOKEN;
module.exports.calculateLongLat = (location) => {
    let url = geourl + location.split(" ").join("+") + "key=" + token;
    request({url:geourl, qs:{address: location.split(" ").join("+"), key: token}}, function (error, response, body) {
        if (error) {
            return null; 
        } else {
            return body["results"]["geometry"]["location"];
        }
    });
};
