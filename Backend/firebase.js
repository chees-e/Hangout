
const admin = require("firebase-admin");
let serviceAccount = require("./firebase.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://m6frontend-1603068531105.firebaseio.com/"
});

module.exports.sendNotif = (msg, token) => {
    let options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    let message = {
        notification: {
            title: "Hangout",
            body: msg
        }
    };
    admin.messaging().sendToDevice(token, message, options).then((response) => {
        return 0;    
    }).catch((error) => {
        return -1;
    });
};
