package com.imber.bmi_calc

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class FirstFragment : Fragment(R.layout.fragment_first) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Search namefield and introtextView and load a name if already found in records and change intro
        val namefield:EditText = view.findViewById(R.id.editText)
        val introtv:TextView = view.findViewById(R.id.tvFragment1)
        loadData(namefield,introtv)

        // Search for button tag
        val nextBtn: Button = view.findViewById(R.id.btnNextOne)

        nextBtn.setOnClickListener {
                // Check for given name, if not empty, save it and continue
                val _name = namefield.text

                when {
                    MainActivity.globalHeight < 1.0  -> Toast.makeText(activity,"Please enter your height from settings",Toast.LENGTH_SHORT).show()
                    _name.isNotEmpty() ->{
                        saveData(_name)

                        val secondFrag = SecondFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.apply {
                            setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            replace(R.id.flFragment,secondFrag,"SECOND")
                            addToBackStack("SECOND")
                            commit()
                        }
                    }
                    else -> Toast.makeText(activity,"Please enter your name!",Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun saveData(_name: Editable){

        val sharedPreferences : SharedPreferences? = activity?.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor? = sharedPreferences!!.edit()
        editor?.apply{
            putString("nameKey", _name.toString())
        }?.apply()
    }

    private fun loadData(namefield : EditText,introTV : TextView){
        val sharedPreferences : SharedPreferences? = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedstring : String? = sharedPreferences!!.getString("nameKey",null)
        if (savedstring != null){
            introTV.text = resources.getText(R.string.welcomeBack)
            namefield.setText(savedstring);
        }
    }





}