package com.imber.bmi_calc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))

        //Height loading and setting
        var currentHeightInp : TextView = findViewById(R.id.tvShowHeight)
        // Loads saved height
        val savedHeight = loadData()
        // Set textView to saved height
        if (savedHeight == "No Records")  currentHeightInp.text = savedHeight
        else currentHeightInp.text = savedHeight+"m"

        //Submit Btn
        var submitbtn: Button = findViewById(R.id.submitHButton)
        var heightInp : EditText = findViewById(R.id.editTextHeight)

        submitbtn.setOnClickListener {
            var saved: String = heightInp.text.toString()
            when {
                (saved.length.toInt() < 3 || saved.length.toInt() > 4) -> Toast.makeText(this,"Please give a valid height",Toast.LENGTH_SHORT).show()
                (saved.contains(",")||saved.contains(".") && saved.toDouble() < 2.9 && saved.toDouble() >= 1.0 ) ->{
                    Toast.makeText(this, "Height Updated", Toast.LENGTH_SHORT).show()
                    saveData(heightInp.text.toString())
                    currentHeightInp.text = saved+"m"
                    MainActivity.globalHeight = saved.toDouble()
                }
                (saved.toInt() in 80..250)->{
                    Toast.makeText(this, "Height Updated", Toast.LENGTH_SHORT).show()
                    val converted: Double = saved.toDouble()*1.0/100
                    saveData(converted.toString())
                    currentHeightInp.text = converted.toString()+"m"
                    MainActivity.globalHeight = converted
                }
                else -> Toast.makeText(this,"Please give a valid height",Toast.LENGTH_SHORT).show()
            }

        }

        //Funktion for deleting bmi records
        clearBmi()


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Contact Us at: lololeagueadmin@gmail.com", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    //Bool to confirm deleting sharedprefs
    var sure = false
    private fun clearBmi() {
        val sharedPreferences : SharedPreferences? = getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        val editor : SharedPreferences.Editor? = sharedPreferences!!.edit()

        var clearbtn :Button = findViewById(R.id.clearDButton)
        var cleartw : TextView = findViewById(R.id.clearRecrd)
        clearbtn.setOnClickListener {
            if (sure){
                editor?.remove("bmiKey")?.commit()
                //Restart for sharedprefs to take effect
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                finishAffinity()

            }
            else {
                cleartw.text = resources.getText(R.string.uSure)
                clearbtn.text = resources.getText(R.string.imSure)
                sure = true
            }

        }
    }

    fun saveData(_height: String){

        val sharedPreferences : SharedPreferences? = getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        val editor : SharedPreferences.Editor? = sharedPreferences!!.edit()
        editor?.apply{
            putString("heightKey", _height)
        }?.apply()
    }

    fun loadData():String{
        val sharedPreferences : SharedPreferences? = getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        val savedString : String? = sharedPreferences!!.getString("heightKey", null)
        if (savedString != null){
            return savedString
        }
        return "No Records"
    }


}