/* eslint-disable max-len */
// The Cloud Functions for Firebase SDK create Cloud Functions set up triggers.
import * as functions from "firebase-functions";

import * as admin from "firebase-admin";
import {MessagingDevicesResponse} from "firebase-admin/lib/messaging/messaging-api";

admin.initializeApp();

const currentTime: Date = new Date();
const year: string = currentTime.getFullYear().toString();
const month: string = (currentTime.getMonth() + 1).toString();
const day: string = currentTime.getDate().toString();
const hours : string = currentTime.getHours().toString();


// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((_request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


// Sends a notifications to all users when a new message is posted.
exports.sendNotifications = functions.firestore.document(`${year}/${month}/${day}`).onCreate(
    async (snapshot) => {
      // Notification details.
      functions.logger.log(hours+" atm");
      const ssData = snapshot.data();
      functions.logger.log(`${ssData.hours}:00`);
      const payload = {
        notification: {
          title: "New Metric",
          body: ssData ? `${ssData.hours}:00` : "ssData empty",
          // click_action: `https://${process.env.GCLOUD_PROJECT}.firebaseapp.com`,
        },
      };

      // Get the list of device tokens.
      const allTokens = await admin.firestore().collection("fcmTokens").get();
      const tokens: string | string[] = [];
      allTokens.forEach((tokenDoc) => {
        tokens.push(tokenDoc.id);
      });

      if (tokens.length > 0) {
        // Send notifications to all tokens.
        const response = await admin.messaging().sendToDevice(tokens, payload);
        await cleanupTokens(response, tokens);
        functions.logger.log("Notifications have been sent and tokens cleaned up.");
      }
    });

// Cleans up the tokens that are no longer valid.
// eslint-disable-next-line require-jsdoc
function cleanupTokens(response: MessagingDevicesResponse, tokens: string[]) {
  // For each notification we check if there was an error.
  const tokensDelete: unknown[] = [];
  response.results.forEach((result, index) => {
    const error = result.error;
    if (error) {
      functions.logger.error("Failure sending notification to", tokens[index], error);
      // Cleanup the tokens that are not registered anymore.
      if (error.code === "messaging/invalid-registration-token" ||
          error.code === "messaging/registration-token-not-registered") {
        const deleteTask = admin.firestore().collection("fcmTokens").doc(tokens[index]).delete();
        tokensDelete.push(deleteTask);
      }
    }
  });
  return Promise.all(tokensDelete);
}
