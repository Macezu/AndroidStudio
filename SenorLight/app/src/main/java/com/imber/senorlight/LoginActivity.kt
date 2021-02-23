package com.imber.senorlight

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity() {

    companion object{
        var maxValues : ArrayList<Long>? = null
        var medianValues : ArrayList<Long>? = null
        var timestampValues : ArrayList<Long>? = null
    }

    private var mAuth: FirebaseAuth? = null
    lateinit var emailf: EditText
    lateinit var passwordf: EditText
    lateinit var progressbar: ProgressBar
    lateinit var loginB : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressbar = findViewById(R.id.loading)
        mAuth = FirebaseAuth.getInstance();
        emailf = findViewById(R.id.username)
        passwordf = findViewById(R.id.password)
        loginB = findViewById(R.id.login)

        loginB.setOnClickListener {
            LoginFireBase()
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.child(getDate("dd_M")).value != null){
                    var value = dataSnapshot.child(getDate("dd_M")).value as HashMap<String, ArrayList<Long>>
                    maxValues = ArrayList(value["max_value"])
                    medianValues = ArrayList(value["median_value"])
                    timestampValues = ArrayList(value["ts"])
                } else {
                    var value = dataSnapshot.child("18_2").value as HashMap<String, ArrayList<Long>>
                    maxValues = ArrayList(value["max_value"])
                    medianValues = ArrayList(value["median_value"])
                    timestampValues = ArrayList(value["ts"])
                }
                
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

    }



    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        if(currentUser != null){
            progressbar.visibility = View.VISIBLE
            Handler().postDelayed({
                if (maxValues != null) {
                    val intent: Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    progressbar.visibility = View.INVISIBLE
                }
            }, 2000)
            progressbar.visibility = View.INVISIBLE
        }

    }

    fun getDate(format: String): String {

        var current = LocalDateTime.now()
        var zonedUTC: ZonedDateTime = current.atZone(ZoneId.of("Europe/Helsinki"))
        zonedUTC.plusHours(2)
        val formatter = DateTimeFormatter.ofPattern(format)
        val formatted = zonedUTC.format(formatter)
        return formatted.toString()
    }





    fun LoginFireBase() {

        if (emailf.text.toString().trim().isEmpty()) {
            emailf.error = "Email Required"
            emailf.requestFocus()
            return
        }
        if (passwordf.text.toString().trim().isEmpty()) {
            passwordf.error = "Password Required"
            passwordf.requestFocus()
            return
        }

        progressbar.visibility = View.VISIBLE

        mAuth!!.signInWithEmailAndPassword(
                emailf.text.toString().trim(),
                passwordf.text.toString().trim()
        )
            .addOnCompleteListener(
                    this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    val intent : Intent = Intent(this, MainActivity::class.java)
                    progressbar.visibility = View.INVISIBLE
                    startActivity(intent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                    ).show()
                    progressbar.visibility = View.INVISIBLE
                }
            }
    }


}