package com.imber.hangmanglobal


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import kotlin.random.Random


lateinit var sp1: Spinner;
lateinit var mAdView : AdView
lateinit var selectedL : String
lateinit var jsonToLoad : JSONArray

class MainActivity : AppCompatActivity() {

    companion object TheWord{
        var fileName: String? = null
        var hangmanWord:String? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lottie: LottieAnimationView = findViewById(R.id.logoimg)
        lottie.imageAssetsFolder = "lottie"
        val btn : Button = findViewById(R.id.startGamebutton)

        //Init ads
        initAds()
        //Load Tokens Text
        loadTokens()

        // Create Arr of images and find Spinner from view
        var imagesarr = intArrayOf(R.drawable.usa, R.drawable.fin, R.drawable.ita, R.drawable.swe)
        sp1 = findViewById(R.id.spinner)
        //Use the collected data to create spinner
        createSpinner(sp1, imagesarr)

        btn.setOnClickListener {
            searchDictionary(selectedL)
        }



    }

    private fun createSpinner(sp1: Spinner, imagesarr: IntArray) {
        // Get Arr of Names
        val namesarr = resources.getStringArray(R.array.languages_array)
        var cA = customAdapted(this, namesarr, imagesarr)
        sp1.adapter = cA

        //Set onitemclicked
        sp1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                Toast.makeText(applicationContext, "${namesarr[position]} language selected", Toast.LENGTH_SHORT).show()
                selectedL = namesarr[position]
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

    private fun loadTokens() {
        var hintTv : TextView = findViewById(R.id.hintsTV)
        val sharedprefs = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val tokens = sharedprefs.getString("tokensKey", "0")
        val formatted = String.format(getString(R.string.hint_tokens), tokens)
        hintTv.text = formatted

    }

    private fun searchDictionary(_lang: String){
        if (_lang.isNullOrBlank()){
            Toast.makeText(this,"Please Select a language",Toast.LENGTH_SHORT).show()
        }

        var assetlist = assets.list("")
        if (assetlist != null) {
            for (elem in assetlist){
                if (elem == "$_lang.json"){
                    fileName = elem
                    loadWord(fileName!!)
                }
            }
            if (fileName.isNullOrBlank()) Toast.makeText(this,"Encountered an error when searching dictionaries",Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadWord(_fileName:String) {
        try {
            // Create an Json array
            val jSonArr =  JSONArray(assets.open(_fileName).bufferedReader().use {
                it.readText()
            })
            val r = (0 until jSonArr.length()-1).random()
            hangmanWord = jSonArr.get(r).toString()
            println(hangmanWord)
            val intent : Intent = Intent(this,GameActivity::class.java)
            startActivity(intent)


        } catch (e: IOException){
            Toast.makeText(this,"Could not load words",Toast.LENGTH_SHORT).show()

        }

    }

}