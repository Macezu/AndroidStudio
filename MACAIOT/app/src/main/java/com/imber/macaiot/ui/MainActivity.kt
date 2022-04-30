package com.imber.macaiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.imber.macaiot.R
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendar = Calendar.getInstance()

        var day : Int = calendar.get(Calendar.DAY_OF_MONTH)
        var month : Int = calendar.get(Calendar.MONTH) +1 //The Months are numbered from 0 (January) to 11 (December).
        var year : Int = calendar.get(Calendar.YEAR)


        readData(day,month,year)




    }



    private fun readData(day : Int,monthD : Int, year : Int){
        database = FirebaseDatabase.getInstance().getReference(year.toString()).child(monthD.toString()).child(day.toString())
        database.get().addOnSuccessListener {
            if (it.exists()){
                println(it.childrenCount)
                it.children.forEach { x -> println(x.value) }
            }else{
                println("why man")
                Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            println("CANT REAAAD")
            Toast.makeText(this,"Could Not read Data",Toast.LENGTH_SHORT).show()
        }




    }


}