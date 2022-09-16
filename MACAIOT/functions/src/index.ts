/* eslint-disable max-len */
// The Cloud Functions for Firebase SDK create Cloud Functions set up triggers.
import * as functions from "firebase-functions";

import * as admin from "firebase-admin";
// import {MessagingDevicesResponse} from "firebase-admin/lib/messaging/messaging-api";

admin.initializeApp();

const currentTime: Date = new Date();
const year: string = currentTime.getFullYear().toString();
const month: string = (currentTime.getMonth() + 1).toString();
const day: string = currentTime.getDate().toString();

const hours: string = (currentTime.getHours() + 3).toString();


// eslint-disable-next-line @typescript-eslint/no-unused-vars
exports.pushNotification = functions.database.ref(`${year}/${month}/${day}`).onWrite((change, _context) => {
  //  Get the current value of what was written to the Realtime Database.
  const valueObject = change.after.val();


  if (!valueObject) {
    functions.logger.log("Value object is null");
    return null;
  }

  // Form string to find correct prop
  const objProp = hours + ":00";
  // functions.logger.log("hours + objProp: ", objProp);

  // Get Temperature and humidity
  const currentStrTemp: string = valueObject[objProp]["Temperature"];
  const currentStrHum: string = valueObject[objProp]["Humidity"];
  // functions.logger.log("current Temp: ", currentStrTemp);
  // functions.logger.log("current Humidity: ", currentStrHum);

  // extract floats
  const currentFloatTemp = parseFloat(currentStrTemp.substring(0, currentStrTemp.length - 2));
  const currentFloatHum = parseFloat(currentStrHum.substring(0, currentStrHum.length - 1));
  // functions.logger.log("current FloatTemp: ", currentFloatTemp);
  // functions.logger.log("current FloatHumidity: ", currentFloatHum);

  // Resolve message if needed
  let bodyvalue = "";
  if (currentFloatTemp >= 38 || currentFloatTemp <= 18) {
    bodyvalue = `Lämpötila: ${currentStrTemp}`;
  }
  if (currentFloatHum > 70 || currentFloatHum <= 25) {
    bodyvalue = bodyvalue + ` Kosteus: ${currentStrHum}`;
  }

  // If body has been filled send notification
  if (bodyvalue) {
    // Create a notification
    const payload = {
      notification: {
        title: "Raja-arvo rikottu",
        body: bodyvalue,
        sound: "default",
      },
    };

    // Create an options object that contains the time to live for the notification and the priority
    const options = {
      priority: "high",
      timeToLive: 60 * 60 * 24,
    };
    functions.logger.log("payload sent");
    return admin.messaging().sendToTopic("pushNotifications", payload, options);
  } else {
    return null;
  }
});

// #region "cemetary"
// exports.makeUppercase = functions.database.ref(`${year}/${month}/${day}`)
//     .onWrite((change, context) => {
//       // Only edit data when it is first created.
//       functions.logger.log(context);
//       if (change.before.exists()) {
//         return null;
//       }
//       // Exit when the data is deleted.
//       if (!change.after.exists()) {
//         return null;
//       }
//       // Grab the current value of what was written to the Realtime Database.
//       const original = change.after.val();
//       console.log("Uppercasing", context.params.pushId, original);
//       const uppercase = original.toUpperCase();
//       // You must return a Promise when performing asynchronous tasks inside a Functions such as
//       // writing to the Firebase Realtime Database.
//       // Setting an "uppercase" sibling in the Realtime Database returns a Promise.

//       if (change.after.ref.parent != null) {
//         return change.after.ref.parent.child("uppercase").set(uppercase);
//       } else {
//         return hours;
//       }
//     });


// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((_request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


// #endregion "cemetary"
