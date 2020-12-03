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
const user1 = {
    id: "TestUser1",
    name: "TestUser1",
    device: null,
    pfp: null
};
const user2 = {
    id: "TestUser2",
    name: "TestUser2",
    device: null,
    pfp: null
};
const user3 = {
    id: "TestUser3",
    name: "TestUser3",
    device: null,
    pfp: null
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
    const post1 = await request(app).post("/user/").send(user1);
    expect(post1.statusCode).toBe(201);

    // Test Case 2: Adding the same user should detect a conflict
    const post2 = await request(app).post("/user/").send(user1);
    expect(post2.statusCode).toBe(409);

    const get1 = await request(app).get("/user/TestUser1");
    expect(get1.statusCode).toBe(200);
    expect(get1.body.id).toBe("TestUser1");
    expect(get1.body.events).toEqual([]);
});

test("Testing Create Event", async () => {
    const expected = [
    {
        attendees: [],
        name: "TestEventName1",
        host: null,
        desc: "TestDesc1",
        start: start1.toISOString(),
        end: end1.toISOString(),
        location: {"lat":0, "long":0}
    },
    {
        attendees: [],
        name: "TestEventName2",
        host: null,
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
    expect(getResponse.body.events).toEqual(expected);
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

    const post1 = await request(app).post("/user/").send(user1);
    expect(post1.statusCode).toBe(201);

    // Test Case 1: Adding an event that does not exist should return 404
    const post2 = await request(app).post("/user/TestUser1/event/-1").send({});
    expect(post2.statusCode).toBe(404);
    
    // Test Case 2: Adding a valid event to an empty user should work
    const post3 = await request(app).post(`/user/TestUser1/event/${EV1Res.body.id}`);
    expect(post3.statusCode).toBe(200);
    
    // Test Case 3: ev3 and ev1 conflict, which should be indicated in the response
    const post4 = await request(app).post(`/user/TestUser1/event/${EV3Res.body.id}`);
    expect(post4.statusCode).toBe(409);
    
    // Test Case 4: ev1 and ev2 do not conflict, so ev2 should be added
    const post5 = await request(app).post(`/user/TestUser1/event/${EV2Res.body.id}`);
    expect(post5.statusCode).toBe(200);

    const get1 = await request(app).get("/user/TestUser1");
    expect(get1.statusCode).toBe(200);
    expect(get1.body.events).toEqual([EV1Res.body.id, EV2Res.body.id]);
});

test("Testing Remove Event From User", async () => {
    // Preparation: Add event and a test user
    const EV1Res = await request(app).post("/event/").send(ev1);
    expect(EV1Res.statusCode).toBe(201);

    const post1 = await request(app).post("/user/").send(user1);
    expect(post1.statusCode).toBe(201);

    // Test case 1: Deleting event from empty user should return 404
    const del1 = await request(app).delete(`/user/TestUser1/event/${EV1Res.body.id}`);
    expect(del1.statusCode).toBe(404);

    const post2 = await request(app).post(`/user/TestUser1/event/${EV1Res.body.id}`);
    expect(post2.statusCode).toBe(200);

    // Test case 2: Deleting event that the user attends should return 200
    const del2 = await request(app).delete(`/user/TestUser1/event/${EV1Res.body.id}`);
    expect(del2.statusCode).toBe(200);

    // Test case 3: Deleted event should be removed from the user
    const get1 = await request(app).get("/user/TestUser1");
    expect(get1.statusCode).toBe(200);
    expect(get1.body.events).toEqual([]);
    
    // Test case 4: Deleted event should no longer conflict
    const post3 = await request(app).post(`/user/TestUser1/event/${EV1Res.body.id}`);
    expect(post3.statusCode).toBe(200);
});

test("Testing Friend Requests", async () => {
    // Preparation: Add 3 users
    const post1 = await request(app).post("/user/").send(user1);
    expect(post1.statusCode).toBe(201);
    
    const post2 = await request(app).post("/user/").send(user2);
    expect(post2.statusCode).toBe(201);
    
    const post3 = await request(app).post("/user/").send(user3);
    expect(post3.statusCode).toBe(201);
    
    // Test case 1: Add a fake user as a friend, must fail
    const friend1 = await request(app).post("/user/TestUser1/friend/FakeUser");
    expect(friend1.statusCode).toBe(404);
    
    // Test case 2: Adding yourself as a friend should fail
    const friend2 = await request(app).post("/user/TestUser1/friend/TestUser1");
    expect(friend2.statusCode).toBe(409);
    
    // Test case 3: Adding a valid user as a friend should succeed
    const friend3 = await request(app).post("/user/TestUser1/friend/TestUser2");
    expect(friend3.statusCode).toBe(200);
    
    // Test case 4: Adding the same user twice as a friend should fail
    const friend4 = await request(app).post("/user/TestUser1/friend/TestUser2");
    expect(friend4.statusCode).toBe(409);
    
    // Test case 5: Adding another user as a friend should succeed
    const friend5 = await request(app).post("/user/TestUser1/friend/TestUser3");
    expect(friend5.statusCode).toBe(200);

    // Test case 6: Adding a friend should add the friend to your friends list
    const userData = await request(app).get("/user/TestUser1");
    expect(userData.statusCode).toBe(200);
    expect(userData.body.friends.includes("TestUser2"));
});

test("Testing Remove Friend", async () => {
    // Preparation: Add 3 users and add TestUser1 as friend to TestUser2
    const post1 = await request(app).post("/user/").send(user1);
    expect(post1.statusCode).toBe(201);
    
    const post2 = await request(app).post("/user/").send(user2);
    expect(post2.statusCode).toBe(201);
    
    const post3 = await request(app).post("/user/").send(user3);
    expect(post3.statusCode).toBe(201);
    
    const friend1 = await request(app).post("/user/TestUser1/friend/TestUser2");
    expect(friend1.statusCode).toBe(200);
    
    // Test case 1: Trying to remove invalid user should fail
    const delete1 = await request(app).delete("/user/TestUser1/friend/InvalidUser");
    expect(delete1.statusCode).toBe(404);
    
    // Test case 2: Trying to remove yourself should fail
    const delete2 = await request(app).delete("/user/TestUser1/friend/TestUser1");
    expect(delete2.statusCode).toBe(404);
    
    // Test case 3: Trying to remove a user you are not friends with should fail
    const delete3 = await request(app).delete("/user/TestUser1/friend/TestUser3");
    expect(delete3.statusCode).toBe(404);
    
    // Test case 4: Removing a user you are friends with should succeed
    const delete4 = await request(app).delete("/user/TestUser1/friend/TestUser2");
    expect(delete4.statusCode).toBe(200);
    
    // Test case 5: After you remove the other user, it should not be in either user's friend list
    const get1 = await request(app).get("/user/TestUser1");
    const get2 = await request(app).get("/user/TestUser2");
    expect(get1.statusCode).toBe(200);
    expect(get2.statusCode).toBe(200);
    expect(!get1.body.friends.includes("TestUser2"));
    expect(!get2.body.friends.includes("TestUser1"));
});
