package com.imber.hangmanglobal


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.games.Games


lateinit var leaderbBtn : ImageButton
lateinit var AchivBtn : ImageButton
lateinit var rateBtn : ImageButton
lateinit var shopBtn : ImageButton
lateinit var homeBtn : Button
lateinit var signInBtn : SignInButton


class SettingsActivity : AppCompatActivity() {

    private val RC_LEADERBOARD_UI = 9004
    private val RC_ACHIEVEMENT_UI = 9003


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        leaderbBtn = findViewById(R.id.leaderboardbtn)
        AchivBtn = findViewById(R.id.achievbtn)
        rateBtn = findViewById(R.id.rateusbtn)
        shopBtn = findViewById(R.id.shopbtn)
        homeBtn = findViewById(R.id.backhomebtn)
        signInBtn = findViewById(R.id.signInButton)




        leaderbBtn.setOnClickListener {
            showTopPlayers()
        }

        AchivBtn.setOnClickListener {
            showAchievements()
        }

        shopBtn.setOnClickListener {
            Toast.makeText(this, "Available in future update!", Toast.LENGTH_SHORT).show()
        }

        rateBtn.setOnClickListener {
            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://play.google.com/store/apps/details?id=com.imber.hangmanglobal")
            )
            startActivity(viewIntent)
        }

        homeBtn.setOnClickListener {
            val int = Intent(this, MainActivity::class.java)
            startActivity(int)
        }
    }



    private fun showTopPlayers(){

            try {
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .allLeaderboardsIntent
                    .addOnSuccessListener { intent -> startActivityForResult(intent, RC_LEADERBOARD_UI) }

            } catch (e: RemoteException){
                println(e.localizedMessage)
                Toast.makeText(this, "Not signed in to Google Play Services", Toast.LENGTH_SHORT).show()
            }


    }
    fun showAchievements(){
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
            .achievementsIntent
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI) }

    }

}