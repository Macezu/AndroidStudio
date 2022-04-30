package com.imber.macaiot.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.imber.macaiot.ui.login.LoginActivity
import com.imber.macaiot.ui.login.LoginViewModel

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    private var user: FirebaseUser? = null

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout(auth: FirebaseAuth) {
        user = null
        dataSource.logout(auth)
    }

    fun login(email: String, password: String, auth: FirebaseAuth, loginActvity: LoginActivity): FirebaseUser? {
        // handle login
        var fbUser : FirebaseUser?
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActvity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        fbUser = auth.currentUser
                        setLoggedInUser(fbUser)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        fbUser = null
                    }
                }

        return user
    }

    private fun setLoggedInUser(loggedInUser: FirebaseUser?) {
        println("TÄÄLLÄ")
        println(loggedInUser)
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}