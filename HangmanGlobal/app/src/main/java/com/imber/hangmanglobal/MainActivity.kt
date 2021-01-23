package com.imber.hangmanglobal


import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.games.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import org.json.JSONArray
import java.io.IOException


private var mFirebaseAnalytics: FirebaseAnalytics? = null
lateinit var sp1: Spinner
lateinit var diffspinn : Spinner
lateinit var settingsBtn : ImageButton
lateinit var strbtn : Button
lateinit var mAdView : AdView


class MainActivity : AppCompatActivity() {

    //google init
    private val RC_SIGN_IN = 123
    private val REQUEST_CODE_RESOLUTION = 123



    companion object TheWord{
        // 2ez, 0norm, -1 hard, -3ultimate
        var gameDiff: Int = 0
        var fileName: String? = null
        var hangmanWord:String? = null
        //Language name used to searh for correct json and tallying language specific points
        lateinit var selectedL : String
        //saviours hangmnpts, recordstreak and total gamesplayed
        var saviourTkn  = 0
        var hgpoints = 0
        var recordstreak = 0
        var gamespld = 0
        //For jack of all trades
        var languageswon = arrayOf(false, false, false, false)
        // Set last used diff and lang automatically
        var lastDif: Int = 1
        var lastLang: Int = 0
        // Get achiev. and leaderboard
        var achievmentclient : AchievementsClient? = null
        var leaderboardsclient : LeaderboardsClient? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        signInSilently()
        //Animated logo
        val lottie: LottieAnimationView = findViewById(R.id.logoimg)
        lottie.imageAssetsFolder = "lottie"
        //Buttons
        strbtn = findViewById(R.id.startGamebutton)
        settingsBtn = findViewById(R.id.settingsbtn)

        //Init ads
        initAds()
        //Load Tokens
        loadData()

        // Create Arr of images and find Spinner from view
        var imagesarr = intArrayOf(R.drawable.usa, R.drawable.fin, R.drawable.ita, R.drawable.swe)
        sp1 = findViewById(R.id.spinner)
        //Use the collected data to create spinner
        createSpinners(sp1, imagesarr)

        strbtn.setOnClickListener {
            searchDictionary(selectedL)
        }

        settingsBtn.setOnClickListener {
            val intentti = Intent(this, SettingsActivity::class.java)
            startActivity(intentti)

        }




    }


    private fun createSpinners(sp1: Spinner, imagesarr: IntArray) {
        // Flags search names, set customadapter
        val namesarr = resources.getStringArray(R.array.languages_array)
        var cA = customAdapted(this, namesarr, imagesarr)
        sp1.adapter = cA
        sp1.setSelection(lastLang)

        creatediffSpin()

        //Set onitemclicked
        sp1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedL = namesarr[position]
                lastLang = position
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

    }

    private fun creatediffSpin() {
        diffspinn = findViewById(R.id.diffspinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.diff_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            diffspinn.adapter = adapter
            diffspinn.setSelection(lastDif)
        }
        diffspinn.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> gameDiff = 2
                    1 -> gameDiff = 0
                    2 -> gameDiff = -1
                    3 -> gameDiff = -3
                }
                lastDif = position
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

    }

    private fun initAds() {
        MobileAds.initialize(this){}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }


    //region Google signin, leaderboards and achievments

    private fun signInSilently() {
        val signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        println("result account $account")
        if (GoogleSignIn.hasPermissions(account)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            achievmentclient = Games.getAchievementsClient(this, account)
            leaderboardsclient = Games.getLeaderboardsClient(this, account)
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient: GoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    this,
                    OnCompleteListener<GoogleSignInAccount?> { task ->
                        if (task.isSuccessful) {
                            // The signed in account is stored in the task's result.
                            val signedInAccount = task.result
                            achievmentclient = Games.getAchievementsClient(this, signedInAccount)
                            leaderboardsclient = Games.getLeaderboardsClient(this, signedInAccount)
                        } else {
                            println("Täällä ollaan result! ${task.exception}")
                            startSignInIntent()
                        }
                    })
        }
    }

    private fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount
                println("Täällä Ollaan 2  result")
                val playerClient = Games.getPlayersClient(this, signedInAccount)
                val playerTask = playerClient.currentPlayer
                achievmentclient = Games.getAchievementsClient(this, signedInAccount)
                leaderboardsclient = Games.getLeaderboardsClient(this, signedInAccount)
            } else {
                println("result: $result")
                println("result success: ${result.isSuccess}")
                println("result status: ${result.status}")
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = "FAILED TO SIGN IN"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                onConnectionFailed(result)
            }
        }


    }

    fun onConnectionFailed(result: GoogleSignInResult) {
        // Called whenever the API client fails to connect.
        println("GoogleApiClient connection failed: $result")
        if (!result.status.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.status.statusCode, 0).show()
            return
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.status.startResolutionForResult(this, REQUEST_CODE_RESOLUTION)
        } catch (e: SendIntentException) {
            println("Exception while starting resolution activity $e")
        }
    }




    //endregion

    private fun loadData() {
        var hintTv : TextView = findViewById(R.id.hintsTV)
        val sharedprefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        saviourTkn = sharedprefs.getInt("tokensKey", 0)
        recordstreak = sharedprefs.getInt("streakKey", 0)
        hgpoints = sharedprefs.getInt("hgpointsKey", 0)
        gamespld = sharedprefs.getInt("gamespKey", 0)
        hintTv.text = String.format(getString(R.string.hint_tokens), saviourTkn)

    }

    private fun searchDictionary(_lang: String){
        if (_lang.isNullOrBlank()){
            Toast.makeText(this, "Please Select a language", Toast.LENGTH_SHORT).show()
        }

        var assetlist = assets.list("")
        if (assetlist != null) {
            for (elem in assetlist){
                if (elem == "$_lang.json"){
                    fileName = elem
                    loadWord(fileName!!)
                }
            }
            if (fileName.isNullOrBlank()) Toast.makeText(
                this,
                "Encountered an error when searching dictionaries",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun loadWord(_fileName: String) {
        try {
            // Create an Json array
            val jSonArr =  JSONArray(assets.open(_fileName).bufferedReader().use {
                it.readText()
            })
            val r = (0 until jSonArr.length()-1).random()
            hangmanWord = jSonArr.get(r).toString().toLowerCase()
            println("THE CHOSEN WORD: $hangmanWord")
            val intent : Intent = Intent(this, GameActivity::class.java)
            startActivity(intent)


        } catch (e: IOException){
            Toast.makeText(this, "Could not load words", Toast.LENGTH_SHORT).show()

        }

    }






    override fun onResume() {
        super.onResume()
        signInSilently()
    }


}