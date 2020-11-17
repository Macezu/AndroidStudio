package com.imber.bmi_calc


import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(R.layout.fragment_second) {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidThreeTen.init(activity);


        // Get the introtext to set name, EditText for weight collection and
        val introText: TextView = view.findViewById(R.id.twFrag2withName)
        val weightInput: EditText = view.findViewById(R.id.editTextNumberDecimal)


        // Reformat intro string to show name
        formatIntro(introText)


        //Button click checks if wight has been entered and saves it to globalWeight.
        view.findViewById<Button>(R.id.buttonNextSecond).setOnClickListener {
            if (weightInput.text.length in 2..3){
                //Save weight
                MainActivity.globalWeight = weightInput.text.toString().toDouble()

                //Calculate Bmi and set it as OneBmi parameter
                val one = OneBmi()
                one.bmi= MainActivity.calculateBMI().toDouble()
                //DateTime
                var cal = Calendar.getInstance()
                one.date = cal.time
                // Add the object to list of Bmis
                MainActivity.arrayofBmis?.add(one)
                //Saves arrayofBmis as json
                saveData()


                // Open third fragment
                val resultFrag = ResultFragment()
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    replace(R.id.flFragment, resultFrag, "THIRD")

                    commit()
                }

            } else Toast.makeText(activity, "Please enter a valid weight", Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveData(){

        val sharedPreferences : SharedPreferences? = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        // init gson
        val gson = Gson()
        //create a json from the array called arrayofBmis
        var json = gson.toJson(MainActivity.arrayofBmis)


        val editor : SharedPreferences.Editor? = sharedPreferences!!.edit()
        editor?.apply{
            //save bmi
            putString("bmiKey", json)
        }?.apply()
    }

    private fun formatIntro(_introText: TextView){
        val sharedPreferences : SharedPreferences? = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val name : kotlin.String? = sharedPreferences!!.getString("nameKey", null)
        val res  = activity?.resources
        val _text:String = String.format(res!!.getString(R.string.areUreadyVar), name)
        _introText.text = _text+"?"
    }



}