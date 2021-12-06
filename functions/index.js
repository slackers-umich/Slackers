const functions = require("firebase-functions");


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp();

const payload = {
	token: FCMToken,
    notification: {
        title: 'cloud function demo',
        body: message
    },
    data: {
        body: message,
    }
};

admin.messaging().send(payload).then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
    return {success: true};
}).catch((error) => {
    return {error: error.code};
});

exports.checkNearby = functions.database.ref('/users/{pushId}/nearbyUsers')
.onCreate((snapshot, context) => {
    // Grab the current value of what was written to the Realtime Database.
    const original = snapshot.val();
    functions.logger.log('Uppercasing', context.params.pushId, original);
    const uppercase = original.toUpperCase();

    const FCMToken = admin.database().ref('${pushId}/FCMToken').once('value')
    const payload = {
          token: FCMToken,
          notification:{
            title: 'UmichConnect',
            body: 'There are new users in the area!',
          },
          data: {
            body: 'There are new users in the area!',
          }
    };

    admin.messaging().send(payload).then((response) => {
      console.log('Success', response)
      return {success: true};
    }).catch((error) => {
        return {error: error.code}
    });




    // You must return a Promise when performing asynchronous tasks inside a Functions such as
    // writing to the Firebase Realtime Database.
    // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
    return snapshot.ref.parent.child('uppercase').set(uppercase);
  });

exports.addMessage = functions.https.onRequest(async (req, res) => {
    // Grab the text parameter.
    const original = req.query.text;
    // Push the new message into Firestore using the Firebase Admin SDK.
    const writeResult = await admin.firestore().collection('messages').add({original: original});
    // Send back a message that we've successfully written the message
    res.json({result: `Message with ID: ${writeResult.id} added.`});
  });

// Listens for new messages added to /messages/:documentId/original and creates an
// uppercase version of the message to /messages/:documentId/uppercase
exports.makeUppercase = functions.firestore.document('/messages/{documentId}')
.onCreate((snap, context) => {
  // Grab the current value of what was written to Firestore.
  const original = snap.data().original;

  // Access the parameter `{documentId}` with `context.params`
  functions.logger.log('Uppercasing', context.params.documentId, original);
  
  const uppercase = original.toUpperCase();
  
  // You must return a Promise when performing asynchronous tasks inside a Functions such as
  // writing to Firestore.
  // Setting an 'uppercase' field in Firestore document returns a Promise.
  return snap.ref.set({uppercase}, {merge: true});
});