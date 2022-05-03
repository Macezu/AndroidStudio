package com.imber.macaiot.ui.login

import com.google.firebase.auth.FirebaseUser

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
        val user: FirebaseUser
        //... other data fields that may be accessible to the UI

)

