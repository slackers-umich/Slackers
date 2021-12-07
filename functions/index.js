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

exports.findNearby = functions.database.ref('/users/{pushId}/latitude')
.onWrite((snapshot, context) => {
  var startLat = null, endLat = null, startLong = null, endLong = null
  var currLong = null, currLat = null
  var keysLat = null, keysLong = null
  const pushId = context.params.pushId
  console.log(pushId)
  if (snapshot.after.val() != null)
  { 
    currLat = snapshot.after.val()
    admin.database().ref(`users/${pushId}/longitude`).once('value').then((snapshot) => {
      currLong = snapshot.val()
    })
    setTimeout(function(){
      startLat = currLat - 0.001
      endLat = currLat + 0.001
      startLong = currLong - 0.001
      endLong = currLong + 0.001
    }, 50)
    setTimeout(function(){
      admin.database().ref(`users`).orderByChild('latitude').startAt(startLat).endAt(endLat).once('value')
      .then((snapshot) => {
        keysLong = Object.keys(snapshot.val())
      })
      admin.database().ref(`users`).orderByChild('longitude').startAt(startLong).endAt(endLong).once('value')
      .then((snapshot) => {
        keysLat = Object.keys(snapshot.val())
      })
    }, 75)
    setTimeout(function(){
      var keysIntersected = keysLong.filter(value => keysLat.includes(value))
      console.log(keysIntersected)

      const index = keysIntersected.indexOf(pushId)
      if (index > -1){
        keysIntersected.splice(index, 1)
      }
      console.log(keysIntersected)
      admin.database().ref(`users/${pushId}/nearbyUsers`).set(keysIntersected)
    }, 110)
  }
  return null
})
exports.checkNearby = functions.database.ref('/users/{pushId}/nearbyUsers')
.onWrite((snapshot, context) => {
    const pushId = context.params.pushId
    
    if (snapshot.before.val() == null)
    { 
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
          data: {
            message : "my_custom_value",
            body:"test"
         },
          android:{
            priority:"high"
          }
        };
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
    }
    else if (snapshot.after.val() != null){
      const beforeData = snapshot.before.val().filter(function (e) {return e != null;}); // data before the write
      const afterData = snapshot.after.val().filter(function (e) {return e != null;});// data after the write
      if (!compareOldNewList(beforeData, afterData))
      {
        console.log(`/FCMTokens/${pushId}`)
        var FCMToken = null
        admin.database().ref(`/FCMTokens/${pushId}`).once('value').then((snapshot) =>{
          FCMToken = snapshot.val()
          const payload = {
            token: FCMToken,
            notification:{
              title: 'UmichConnect',
              body: 'There are new users in the area!',
            },
            android:{
              priority:"high"
            }
          };
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
        }
      }
      return null
  });

  exports.checkConnections = functions.database.ref('/users/{pushId}/pending')
  .onWrite((snapshot, context) => {
      const pushId = context.params.pushId
      var FCMToken = null
      if (snapshot.before.val() == null)
      { 
        admin.database().ref(`/FCMTokens/${pushId}`).once('value').then((snapshot) =>{
          FCMToken = snapshot.val()
          console.log(FCMToken)
          const payload = {
            token: FCMToken,
            notification:{
              title: 'UmichConnect',
              body: 'You have new a connection request!',
            },
          };
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
      }
      else if (snapshot.after.val() != null){
        const beforeData = snapshot.before.val().filter(function (e) {return e != null;}); // data before the write
        const afterData = snapshot.after.val().filter(function (e) {return e != null;});// data after the write
        if (beforeData.length < afterData.length){
          admin.database().ref(`/FCMTokens/${pushId}`).once('value').then((snapshot) =>{
            FCMToken = snapshot.val()
            console.log(FCMToken)
            const payload = {
              token: FCMToken,
              notification:{
                title: 'UmichConnect',
                body: 'You have new a connection request!',
              },
            };
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
        }
      }
      return null
    });
//return true if the all the data in newData appears in oldData
function compareOldNewList(oldData, newData){
  if (oldData === undefined)
  {
    return false
  }
  let checker = (arr, target) => target.every(v => arr.includes(v));

  return (checker(oldData, newData))
}