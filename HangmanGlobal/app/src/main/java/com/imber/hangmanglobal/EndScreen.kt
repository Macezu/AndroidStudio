package com.imber.hangmanglobal




import android.R.id
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.plattysoft.leonids.ParticleSystem
import java.util.*


class EndScreen : AppCompatActivity() {

    //init
    lateinit var title : TextView
    lateinit var hmpoints : TextView
    lateinit var hangmanw : TextView
    lateinit var beststreak : TextView
    lateinit var currentsrcount : TextView
    lateinit var earnedsav : TextView
    lateinit var thumbdown : ImageButton
    lateinit var homebtn : Button
    lateinit var definit : Button
    lateinit var sharedprefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen)

        sharedprefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        editor = sharedprefs.edit()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // find
        title  = findViewById(R.id.titleTv)
        hmpoints = findViewById(R.id.hmpointsTv)
        hmpoints.text = MainActivity.hgpoints.toString()
        beststreak = findViewById(R.id.bestsrteakTv)
        hangmanw = findViewById(R.id.hangmanwordTV)
        currentsrcount = findViewById(R.id.streakTv)
        homebtn = findViewById(R.id.backBtn)
        definit = findViewById(R.id.definitionBt)
        earnedsav = findViewById(R.id.earnedsaviTv)
        thumbdown = findViewById(R.id.thumbdownIB)

        // Set view for popups catch error save to analytics
        try {
            Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .setGravityForPopups(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
            val gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this))
            gamesClient.setViewForPopups(window.decorView.findViewById(id.content))
        } catch (e: Exception){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, e.toString())
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }



        //Save total points and language pts gotten and games played
        pointsNGames()
        saveRecords(MainActivity.selectedL)

        //Set Title, Streak and Points texts Accordingly
        setTitleText()
        setStreakNPointsText()

        //Set hangmanword
        hangmanw.text = MainActivity.hangmanWord

        //Show and give saviour token if earned.
        tenInARow()

        definit.setOnClickListener {
            //Show definition to user
            loadWiki()
        }


        homebtn.setOnClickListener {
            // Return Home
            val intention : Intent = Intent(this, MainActivity::class.java)
            earnedsav.visibility = View.INVISIBLE
            startActivity(intention)
        }

        thumbdown.setOnClickListener{

            //Send badwords to firebase
            FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
            val database  = FirebaseDatabase.getInstance("https://hangman-global-98421790-default-rtdb.europe-west1.firebasedatabase.app")
            val mFeedbackRef = database.reference.child("asd").push()
            mFeedbackRef.setValue(MainActivity.hangmanWord)
            Toast.makeText(this,R.string.feedbackok,Toast.LENGTH_SHORT).show()
            thumbdown.isClickable = false
            thumbdown.alpha = 0.1f
        }
    }




    private fun setStreakNPointsText() {
        //if currstreak over personal, save it as personal best
        if (GameActivity.currstreak >= MainActivity.recordstreak) saveRecordStreak()
        beststreak.text = String.format(
            getString(R.string.yourbeststreak),
            MainActivity.recordstreak
        )
        //current
        currentsrcount.text = String.format(getString(R.string.currentstr), GameActivity.currstreak)
    }

    private fun saveRecordStreak() {
        //Saves record streak an submits it to leaderboard
        MainActivity.recordstreak = GameActivity.currstreak
        editor.putInt("streakKey", MainActivity.recordstreak).commit()
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
            .submitScore(
                getString(R.string.leaderboard_longest_streak),
                MainActivity.recordstreak.toLong()
            )

    }

    private fun pointsNGames(){
        MainActivity.gamespld ++
        editor.apply{
            putInt("gamespKey", MainActivity.gamespld)
            putInt("hgpointsKey", MainActivity.hgpoints)
            }.commit()
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
            .submitScore(
                getString(R.string.leaderboard_mvp),
                MainActivity.hgpoints.toLong()
            )

        if (MainActivity.achievmentclient == null) return
        // Check and Unclock achievements
        if (GameActivity.playerwon){
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .increment(getString(R.string.masterofwords), 1)
        }

        if (GameActivity.playerwon){
            if (MainActivity.hangmanWord!!.length > 14){
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.how))
            } else if (MainActivity.hangmanWord!!.length > 9){
                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.intellectual))
            }
        }
        // Jackofaall
        if (MainActivity.languageswon.all { true }){
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unlock(getString(R.string.jackofalltrades))
        }
        when (MainActivity.gamespld){
            1 -> Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unlock(getString(R.string.firsttimer))
            50 -> Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unlock(getString(R.string.adolescent))
            100 -> Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unlock(getString(R.string.experienced))
            200 -> Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unlock(getString(R.string.oldtimer))
        }
    }

    private fun tenInARow() {
        //If current streak is ok then show earned saviour text and commence saving
        if (GameActivity.currstreak == 10 || GameActivity.currstreak == 20) {
            earnedsav.visibility = View.VISIBLE
            MainActivity.saviourTkn ++
            saveTokens()
        }
    }

    private fun loadWiki() {
        //From spinner position determine language
        var lang = when (MainActivity.lastLang){
            (0) -> "en"
            (1) -> "fi"
            (2) -> "it"
            (3) -> "sv"
            else -> "en"
        }
        // Format wikipedia search based on lang and hmword
        val wiki = String.format(getString(R.string.wikipage), lang, MainActivity.hangmanWord)
        val viewIntent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(wiki)
        )
        startActivity(viewIntent)
        blood()
    }



    private fun setTitleText() {
        //Load random quote from resources and set it
        if (GameActivity.playerwon){
            fireworks()
            val goodarr = resources.getStringArray(R.array.goofendings);
            val randomIndex: Int = Random().nextInt(goodarr.size)
            title.text = goodarr[randomIndex]
            title.setTextColor(ContextCompat.getColor(this, R.color.rightgreen))
        } else {
            val badarr = resources.getStringArray(R.array.baadendigs);
            val rIdx: Int = Random().nextInt(badarr.size)
            title.text = badarr[rIdx]
            title.setTextColor(ContextCompat.getColor(this, R.color.redlight))
        }
    }

    private fun fireworks() {

        Handler(Looper.getMainLooper()).postDelayed({
            val ps = ParticleSystem(this, 100, R.drawable.firew_one, 800)
            ps.setScaleRange(0.7f, 1.3f)
            ps.setSpeedRange(0.1f, 0.25f)
            ps.setRotationSpeedRange(90f, 180f)
            ps.setFadeOut(500, AccelerateInterpolator())
            ps.oneShot(findViewById(R.id.rightupemit), 70)

        }, 400)

        Handler(Looper.getMainLooper()).postDelayed({
            val ps3 = ParticleSystem(this, 100, R.drawable.firew_two, 800)
            ps3.setScaleRange(0.7f, 1.3f)
            ps3.setSpeedRange(0.1f, 0.25f)
            ps3.setRotationSpeedRange(90f, 180f)
            ps3.setFadeOut(500, AccelerateInterpolator())
            ps3.oneShot(findViewById(R.id.leftemit), 70)

        }, 1300)

        Handler(Looper.getMainLooper()).postDelayed({
            val ps2 = ParticleSystem(this, 100, R.drawable.firew_three, 800)
            ps2.setScaleRange(0.7f, 1.3f)
            ps2.setSpeedRange(0.1f, 0.25f)
            ps2.setFadeOut(800, AccelerateInterpolator())
            ps2.oneShot(findViewById(R.id.rightemit), 70)

        }, 2500)
    }

    fun blood(){
        ParticleSystem(this, 8, R.drawable.blood, 3000)
            .setAcceleration(0.00001f, 90)
            .setSpeedByComponentsRange(0f, 0f, 0.02f, 0.09f)
            .setFadeOut(1900, AccelerateInterpolator())
            .emitWithGravity(hangmanw, Gravity.BOTTOM, 2)
    }

    fun saveTokens(){
        //If new token received
        editor.putInt("tokensKey", MainActivity.saviourTkn)
        editor.commit()

    }

    fun saveRecords(language: String){
        //Get languagepoints
        var lpoints = sharedprefs.getInt("${language}Key", 0)
        //add rounds points andreturn them to zero
        lpoints += GameActivity.roundpts
        //Check for negative
        if (lpoints< 0) lpoints = 0
        GameActivity.roundpts = 0
        //Save langPts
        editor.apply{
            putInt("${language}Key", lpoints)
        }
        if (lpoints > 0){
            when (language){
                ("English") -> Games.getLeaderboardsClient(
                    this, GoogleSignIn.getLastSignedInAccount(
                        this
                    )!!
                )
                    .submitScore(
                        getString(R.string.leaderboard_english),
                        lpoints.toLong()
                    )
                ("Finnish") -> Games.getLeaderboardsClient(
                    this, GoogleSignIn.getLastSignedInAccount(
                        this
                    )!!
                )
                    .submitScore(
                        getString(R.string.leaderboard_finnish),
                        lpoints.toLong()
                    )
                ("Swedish") -> Games.getLeaderboardsClient(
                    this, GoogleSignIn.getLastSignedInAccount(
                        this
                    )!!
                )
                    .submitScore(
                        getString(R.string.leaderboard_swedish),
                        lpoints.toLong()
                    )
                ("Italian") -> Games.getLeaderboardsClient(
                    this, GoogleSignIn.getLastSignedInAccount(
                        this
                    )!!
                )
                    .submitScore(
                        getString(R.string.leaderboard_italian),
                        lpoints.toLong()
                    )
            }
        }

    }

    override fun onResume() {
        super.onResume()

    }
}