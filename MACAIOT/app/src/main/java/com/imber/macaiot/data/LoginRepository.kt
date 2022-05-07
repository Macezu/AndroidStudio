package com.imber.macaiot.data

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.imber.macaiot.ui.login.LoginActivity

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

    fun login(email: String, password: String, auth: FirebaseAuth, loginActivity: LoginActivity): FirebaseUser? {
        // handle login
        var fbUser : FirebaseUser? = null
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        fbUser = auth.currentUser
                        setLoggedInUser(fbUser!!)
                        loginActivity.updateUiWithUser(fbUser!!.email!!)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(loginActivity,"Could not login, please check credentials", Toast.LENGTH_LONG).show()
                        fbUser = null
                    }

                }
        return fbUser
    }

    private fun setLoggedInUser(loggedInUser: FirebaseUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}