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
const ev3 = {
    name: "TestEventName3",
    description: "TestDesc3",
    start: start1,
    end: end2,
    location: {"lat":0, "long":0}
};

beforeAll(() => {
    return db.init(uri, dbname);
});

beforeEach(() => {
    return db.clear();
});

afterAll(() => {
    return db.close();
});

test("Testing Login", async () => {    
    // Test Case 1: Add user, should work
    const post1 = await request(app).post("/user/TestUser").send({});
    expect(post1.statusCode).toBe(201);

    // Test Case 2: Adding the same user should detect a conflict
    const post2 = await request(app).post("/user/TestUser").send({});
    expect(post2.statusCode).toBe(409);

    const get1 = await request(app).get("/user/TestUser");
    expect(get1.statusCode).toBe(200);
    expect(get1.body).toEqual({
        id : "TestUser",
        events: []
    });
});

test("Testing Create Event", async () => {
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
});

test("Testing Delete Event", async () => {
    const addResponse = await request(app).post("/event/").send(ev1);
    expect(addResponse.statusCode).toBe(201);

    const delResponse = await request(app).delete(`/event/${addResponse.body.id}`);
    expect(delResponse.statusCode).toBe(200);

    const delResponse2 = await request(app).delete(`/event/${addResponse.body.id}`);
    expect(delResponse2.statusCode).toBe(404);
});

test("Testing Add Event To User", async () => {
    // Preparation: Add 3 events and a test user
    const EV1Res = await request(app).post("/event/").send(ev1);
    expect(EV1Res.statusCode).toBe(201);
    
    const EV2Res = await request(app).post("/event/").send(ev2);
    expect(EV2Res.statusCode).toBe(201);
    
    const EV3Res = await request(app).post("/event/").send(ev3);
    expect(EV3Res.statusCode).toBe(201);

    const post1 = await request(app).post("/user/TestUser").send({});
    expect(post1.statusCode).toBe(201);

    // Test Case 1: Adding an event that does not exist should return 404
    const post2 = await request(app).post("/user/TestUser/event/-1").send({});
    expect(post2.statusCode).toBe(404);
    
    // Test Case 2: Adding a valid event to an empty user should work
    const post3 = await request(app).post(`/user/TestUser/event/${EV1Res.body.id}`);
    expect(post3.statusCode).toBe(200);
    
    // Test Case 3: ev3 and ev1 conflict, which should be indicated in the response
    const post4 = await request(app).post(`/user/TestUser/event/${EV3Res.body.id}`);
    expect(post4.statusCode).toBe(409);
    
    // Test Case 4: ev1 and ev2 do not conflict, so ev2 should be added
    const post5 = await request(app).post(`/user/TestUser/event/${EV2Res.body.id}`);
    expect(post5.statusCode).toBe(200);

    const get1 = await request(app).get("/user/TestUser");
    expect(get1.statusCode).toBe(200);
    expect(get1.body).toEqual({
        id : "TestUser",
        events: [EV1Res.body.id, EV2Res.body.id]
    });
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
