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

exports.checkNearby = functions.database.ref('/users/{pushId}/nearbyUsers')
.onWrite((snapshot, context) => {
    const pushId = context.params.pushId
    console.log(`/FCMTokens/${pushId}`)
    console.log("HELLO1")
    var FCMToken = null
    admin.database().ref(`/FCMTokens/${pushId}`).once('value').then((snapshot) =>{
      FCMToken = snapshot.val()
      console.log(FCMToken)

      const payload = {
        token: FCMToken,
        notification:{
          title: 'UmichConnect',
          body: 'There are new users in the area!',
        },
      };
      
      console.log("HELLO2")
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return {success: true};
      }).catch((error) => {
        return {error: error.code};
      });
    })
    .catch((error) => {
      console.log(error.code)
    })

    
    console.log("HELLO3")
  });

  exports.checkConnections = functions.database.ref('/users/{pushId}/pending')
.onWrite((snapshot, context) => {
    const pushId = context.params.pushId
    console.log(`/FCMTokens/${pushId}`)
    console.log("HELLO1")
    var FCMToken = null
    admin.database().ref(`/FCMTokens/${pushId}`).once('value').then((snapshot) =>{
      FCMToken = snapshot.val()
      console.log(FCMToken)

      const payload = {
        token: FCMToken,
        notification:{
          title: 'UmichConnect',
          body: 'You have new connections!',
        },
      };
      
      console.log("HELLO2")
      admin.messaging().send(payload).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
        return {success: true};
      }).catch((error) => {
        return {error: error.code};
      });
    })
    .catch((error) => {
      console.log(error.code)
    })

    
    console.log("HELLO3")
  });
