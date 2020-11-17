package com.imber.bmi_calc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

//Tallennetaan Lista kyseistä luokkaa
class OneBmi{
    var bmi: Double? = null
    var date: Date? = null
}


class MainActivity : AppCompatActivity() {




    companion object {
        var name: String? = null
        var globalWeight = 0.0
        var globalHeight = 0.0
        var arrayofBmis: MutableList<OneBmi>? = null



        fun calculateBMI(): String {
            var deci = BigDecimal(globalWeight / (globalHeight * globalHeight)).setScale(2, RoundingMode.HALF_EVEN)
            return deci.toString()
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        loadData()


        val firstFrag = FirstFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, firstFrag, "FIRST")

            commit()
        }



    }

    var doubleBackToExitPressedOnce = false
    //Second and third BackPress overwrite
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.fragments.last()


        when {
            (doubleBackToExitPressedOnce) -> {
                finish()
            }
            (currentFragment.tag == "SECOND") -> {
                Toast.makeText(this, resources.getText(R.string.cantEscape), Toast.LENGTH_LONG).show()

            }
            (currentFragment.tag == "THIRD") ->{
                //if on resultFrag asked if really want to exit
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, resources.getText(R.string.sureExit), Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    run {

                        doubleBackToExitPressedOnce = false
                    }
                }, 3000)

            }
            else -> super.onBackPressed()

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
            R.id.action_credits -> {
                Toast.makeText(this, "Made by ImberTechⓇ 2020", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    fun loadData(){

        val sharedPreferences : SharedPreferences? = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        // getHeight from sharedPrefs
        globalHeight  = sharedPreferences!!.getString("heightKey", "-1.0")!!.toDouble()
        // Init gson and  return a the saved array as json string
        val gson = Gson()
        val json: String? = sharedPreferences.getString("bmiKey",null)
        // create a set of oneBmi objects
        val set : Type = object : TypeToken<ArrayList<OneBmi>>(){}.type
        // convert the json string to array of BMIs
        arrayofBmis = gson.fromJson(json,set)


        if (arrayofBmis == null) {
            arrayofBmis = ArrayList()
        }

    }

}



