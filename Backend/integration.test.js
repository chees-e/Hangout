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
    const post1 = await request(app).post("/user/TestUser").send({});
    expect(post1.statusCode).toBe(201);

    const post2 = await request(app).post("/user/TestUser").send({});
    expect(post2.statusCode).toBe(409);

    const get1 = await request(app).get("/user/TestUser");
    expect(get1.statusCode).toBe(200);
    expect(get1.body).toEqual({
        id : "TestUser",
        events: []
    });
    
    await db.close();
});

test("Testing Create Event", async () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const start2 = new Date(2020, 10, 25, 10, 45);
    const end2 = new Date(2020, 10, 25, 13, 50);
    const ev1 = {
        name: "TestEventName1",
        description: "TestDesc1",
        start: start1,
        end: end1,
        location: {"lat":0, "long":0}
    };
    const ev2 = {
        name: "TestEventName2",
        description: "TestDesc2",
        start: start2,
        end: end2,
        location: {"lat":0, "long":0}
    };
    const expected = [
    {
        attendees: [],
        name: "TestEventName1",
        desc: "TestDesc1",
        start: start1.toISOString(),
        end: end1.toISOString(),
        location: {"lat":0, "long":0}
    },
    {
        attendees: [],
        name: "TestEventName2",
        desc: "TestDesc2",
        start: start2.toISOString(),
        end: end2.toISOString(),
        location: {"lat":0, "long":0}
    }];

    
    await db.close();
    await db.init(uri, dbname);
    await db.clear();
    
    const addResponse1 = await request(app).post("/event/").send({});
    expect(addResponse1.statusCode).toBe(201);
        
    const addResponse2 = await request(app).post("/event/").send(ev1);
    expect(addResponse2.statusCode).toBe(201);
    expected[0].id = `${addResponse2.body.id}`;
    
    const addResponse3 = await request(app).post("/event/").send(ev2);
    expect(addResponse3.statusCode).toBe(201);
    expected[1].id = `${addResponse3.body.id}`;
    
    const getResponse = await request(app).get("/event/");
    expect(getResponse.body).toEqual({
        length : 2,
        events : expected
    });
    await db.close();
});

test("Testing Delete Event", async () => {
    const start1 = new Date(2020, 10, 24, 10, 45);
    const end1 = new Date(2020, 10, 24, 13, 50);
    const ev1 = {
        name: "TestEventName1",
        description: "TestDesc1",
        start: start1,
        end: end1,
        location: {"lat":0, "long":0}
    };

    await db.close();
    await db.init(uri, dbname);
    await db.clear();

    const addResponse = await request(app).post("/event/").send(ev1);
    expect(addResponse.statusCode).toBe(201);

    const delResponse = await request(app).delete(`/event/${addResponse.body.id}`);
    expect(delResponse.statusCode).toBe(200);

    const delResponse2 = await request(app).delete(`/event/${addResponse.body.id}`);
    expect(delResponse2.statusCode).toBe(404);

    await db.close();
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
