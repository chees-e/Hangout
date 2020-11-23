/* Integration tests
 *   Login / Add user
 *   Create Event
 *   Add Event to User
 *   Remove Event from User
 *   Delete Event
 *   Add Friend Request
 *   Remove Friend
*/
"use strict";
const assert = require("assert");
const request = require("supertest");
const app = require("./app.js");
const db = require("./database.js");
const uri = "mongodb://127.0.0.1:27017/";
const dbname = "IntegrationTest";

test("Testing Login", async () => {
    await db.close();
    await db.init(uri, dbname);
    await db.clear();
    const response1 = await request(app).post("/user/TestUser").send({});
    expect(response1.statusCode).toBe(201);
    const response2 = await request(app).get("/user/TestUser");
    expect(response2.statusCode).toBe(200);
    expect(response2.body).toEqual({
        id : "TestUser",
        events: []
    });
    await db.close();
});

test("Testing Create Event", async () => {
    assert(false);
});

test("Testing Add Event To User", async () => {
    assert(false);
});

test("Testing Remove Event From User", async () => {
    assert(false);
});

test("Testing Friend Requests", async () => {
    assert(false);
});

test("Testing Remove Friend", async () => {
    assert(false);
});
