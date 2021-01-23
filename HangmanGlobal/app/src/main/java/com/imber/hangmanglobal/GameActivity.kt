package com.imber.hangmanglobal


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.imber.hangmanglobal.MainActivity.TheWord.hangmanWord
import com.plattysoft.leonids.ParticleSystem
import com.plattysoft.leonids.modifiers.AlphaModifier
import com.plattysoft.leonids.modifiers.ScaleModifier
import org.jetbrains.annotations.NotNull


class GameActivity : AppCompatActivity(), keyboard.OnLetterSelectedListener, RewardedVideoAdListener, SaviorDialog.GiveResultlistener {

    lateinit var lives : TextView
    lateinit var hmpointsTv : TextView
    var adfailed = false
    lateinit var handler: Handler
    lateinit var heartimg : ImageView
    private lateinit var  underlstr : String
    private lateinit var chosen : TextView
    lateinit var mAd: RewardedVideoAd

    private val KEY_TEXT_UNDER = "UNDERLINEKEY"
    private val KEY_TEXT_LIVES = "LIVEKEY"

    companion object{
        var playerwon = false
        var remainlives = 7
        var currstreak = 0
        var roundpts = 0
    }

    //TODO ORIENTATION CHANGE?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //Init
        hmpointsTv = findViewById(R.id.hmpointsTv)
        hmpointsTv.text = MainActivity.hgpoints.toString()
        heartimg = findViewById(R.id.heartIV)
        chosen = findViewById(R.id.chosenWTV)
        lives = findViewById(R.id.livesrTV)
        handler = Handler()


        //Ads
        setandLoadAds()

        //Set default lives
        remainlives += MainActivity.gameDiff
        lives.text = remainlives.toString()

        setUnderLine(chosen)



    }

    //Save state
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence(KEY_TEXT_UNDER, underlstr)
        outState.putInt(KEY_TEXT_LIVES, remainlives)
        super.onSaveInstanceState(outState)
    }

    //CHECK IF Saved states, if so, load saved underlinetxt and remaining lives
    override fun onRestoreInstanceState(@NotNull savedInstanceState: Bundle) {
        val savedText = savedInstanceState.getCharSequence(KEY_TEXT_UNDER)
        underlstr = savedText.toString()
        chosen.text = underlstr
        remainlives = savedInstanceState.getInt(KEY_TEXT_LIVES)
        lives.text = remainlives.toString()
        super.onRestoreInstanceState(savedInstanceState)
    }



    private fun setandLoadAds() {
        MobileAds.initialize(this, "ca-app-pub-7300619757531522~4760225710")
        mAd = MobileAds.getRewardedVideoAdInstance(this)
        mAd.rewardedVideoAdListener = this
        mAd.loadAd("ca-app-pub-7300619757531522/8928481103", AdRequest.Builder().build())
    }

    private fun setUnderLine(chosen: TextView) {
        val regex = "\\p{L}".toRegex()
         underlstr  = hangmanWord!!.replace(regex, "_")
        chosen.text = underlstr
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
        //Set lives + text

    }

    override fun onCorrectSelected(letter: String) {

        //Get all letters that match
        var arr = ArrayList<Int>()
        var index: Int = hangmanWord!!.indexOf(letter)
        while (index >= 0) {
            arr.add(index)
            index = hangmanWord!!.indexOf(letter, index + 1)
        }
        //Change underscores to letters
        val chars = underlstr.toCharArray()
        for (elem in arr){
            chars[elem] = letter.single()
        }
        underlstr = String(chars)
        chosen.text = underlstr


        //Check if player won
        hasplayerWon()

    }

    private fun giveFinalPts() {
        roundpts = 100 * MainActivity.lastDif
        var start = MainActivity.hgpoints;
        MainActivity.hgpoints += 100*MainActivity.lastDif; // the total number

        Thread {
            while (start < MainActivity.hgpoints) {
                try {
                    Thread.sleep(8)
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                hmpointsTv.post(Runnable { hmpointsTv.text = start.toString() })
                start++
            }
        }.start()


    }

    private fun hasplayerWon() {
        //Check if no underlines
        if (!(underlstr.contains("_"))) {
            fireworks()
            giveFinalPts()
            //JackofAllTrades achieve.
            MainActivity.languageswon[MainActivity.lastLang] = true
            playerwon = true
            handler.postDelayed({
                val intent = Intent(this, EndScreen::class.java)
                startActivity(intent)
                if (MainActivity.gameDiff != 2) currstreak++
                remainlives = 7
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                finish()
            }, 3300)
        }
    }

    private fun fireworks() {

        ParticleSystem(this, 100, R.drawable.star, 5000)
            .setSpeedRange(0.1f, 0.25f)
            .setRotationSpeedRange(90f, 180f)
            .setInitialRotationRange(0, 360)
            .oneShot(findViewById(R.id.chosenWTV), 100)
    }

    override fun onWrongSelected(letter: String) {
        // Check if gameover
        if (remainlives - 1 <= 0){
            playerwon = false
            remainlives = 0
            lives.text = remainlives.toString()
            //Use Savior?

            if (MainActivity.saviourTkn > 0){
                createAlert(this)
            } else {
                openSaviorDialog()
            }


        } else {
            // Deduct a live and update heart and text
            smokeEffect()
            controlHeart(-7)
            remainlives -= 1
            lives.text = remainlives.toString()
        }

    }

    private fun smokeEffect() {
        ParticleSystem(this, 6, R.drawable.blood, 1500)
            .setSpeedByComponentsRange(-0.025f, 0.025f, -0.06f, -0.08f)
            .setAcceleration(0.00002f, 30)
            .setInitialRotationRange(0, 360)
            .addModifier(AlphaModifier(255, 0, 1000, 3000))
            .addModifier(ScaleModifier(0.5f, 2f, 0, 1000))
            .oneShot(findViewById<View>(R.id.heartIV), 10)
    }

    private fun openSaviorDialog() {
        val fm = this@GameActivity.supportFragmentManager
        val sD = SaviorDialog()
        sD.show(fm, "Saviour")
        sD.isCancelable = false

    }

    fun gameOver(){
        if (MainActivity.hgpoints >= 400) MainActivity.hgpoints -= 50 * MainActivity.lastDif
        roundpts = -50 * MainActivity.lastDif
        val intent = Intent(this, EndScreen::class.java)
        startActivity(intent)
        remainlives = 7
        currstreak = 0
    }

    fun createAlert(context: Context){
        AlertDialog.Builder(context, R.style.MyDialogTheme)
            .setTitle(resources.getString(R.string.usesaviour))
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(resources.getString(R.string.yes),
                DialogInterface.OnClickListener { _, _ ->
                    // Give 2 more lives deduct savior token
                    remainlives += 2
                    lives.text = remainlives.toString()
                    controlHeart(14)
                    MainActivity.saviourTkn--
                    saveData()

                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(
                resources.getString(R.string.no),
                DialogInterface.OnClickListener { _, _ ->
                    if (!adfailed) {
                        openSaviorDialog()
                    } else {
                        gameOver()
                    }
                })
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    fun controlHeart(amount: Int){
        heartimg.layoutParams.height += amount;
        heartimg.layoutParams.width += amount;
        heartimg.requestLayout()
    }

    fun saveData(){
        val sharedprefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedprefs.edit()
        editor.putInt("tokensKey", MainActivity.saviourTkn)
        editor.commit()

    }

    override fun onPause() {
        super.onPause()
        mAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        mAd.resume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAd.destroy(this)
    }



    //region ADS

    override fun onRewardedVideoAdLoaded() {
        println("Ad has loaded")
    }

    override fun onRewardedVideoAdOpened() {
        println("Ad has opened")

    }

    override fun onRewardedVideoStarted() {
        println("Ad has started")
    }

    override fun onRewardedVideoAdClosed() {
        // Ad closed early or not?
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 1.5sek
            if (remainlives == 0) {
                gameOver()
            }
        }, 1500)
    }

    override fun onRewarded(reward: RewardItem) {
        // Reward the user.
        println("Player Awarded")
        remainlives += 2
        lives.text = remainlives.toString()

    }

    override fun onRewardedVideoAdLeftApplication() {
        println("I was left")
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        adfailed = true
    }

    override fun onRewardedVideoCompleted() {
        println("Ad was completed")

    }

    override fun yesSelected(r: Boolean) {
        //  Interface from  saviordialog If player wish to see ad
        if (r){
            if (mAd.isLoaded) {
                mAd.show()
            } else {
                gameOver()
            }
        }else {
            gameOver()
        }
    }

    //endregion

}