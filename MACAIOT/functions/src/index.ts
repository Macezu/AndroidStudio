/* eslint-disable max-len */
// The Cloud Functions for Firebase SDK create Cloud Functions set up triggers.
import * as functions from "firebase-functions";
import {CloudFunction} from "firebase-functions";
import {QueryDocumentSnapshot} from "firebase-functions/v1/firestore";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
export const helloWorld = functions.https.onRequest((_request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});


export const logLatest = (): CloudFunction<QueryDocumentSnapshot> => {
  const currentTime: Date = new Date();
  const year: string = currentTime.getFullYear().toString();
  const month: string = (currentTime.getMonth() + 1).toString();
  const day: string = currentTime.getDate().toString();
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const getLatestMetric = functions.firestore.document(`"/${year}/${month}/${day}"`)
      .onCreate((snap, context) => {
      // Grab the current value of what was written to Firestore.
        const original = snap.data().original;

        // Access the parameter `{documentId}` with `context.params`
        functions.logger.log("Uppercasing", context.params.documentId, original);


        // You must return a Promise when performing asynchronous tasks inside a Functions such as
        // writing to Firestore.
        // Setting an 'uppercase' field in Firestore document returns a Promise.
        return snap.ref.set({original}, {merge: true});
      });
  return getLatestMetric;
};
