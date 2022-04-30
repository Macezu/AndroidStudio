package com.imber.macaiot.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.imber.macaiot.R
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendar = Calendar.getInstance()

        var month : Int = calendar.get(Calendar.DAY_OF_MONTH)
        var year : Int = calendar.get(Calendar.YEAR)

        print("KISSSAA")
        println(month)
        println(year)
        readData(month,year)





    }



    private fun readData(monthD : Int, year : Int){
        database = FirebaseDatabase.getInstance().getReference(year.toString()).child(monthD.toString())
    }


}