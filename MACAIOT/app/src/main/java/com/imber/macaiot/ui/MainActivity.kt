package com.imber.macaiot.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.imber.macaiot.R
import com.imber.macaiot.ui.login.LoggedInUserView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import kotlin.reflect.typeOf

class MetricsData {
    companion object  {
        var SnapshotList = mutableListOf<DataSnapshot>()
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        saveData()

    }

    override fun onResume() {
        super.onResume()


        saveData()
    }


    private fun saveData(){
        val latestMetric = findViewById<TextView>(R.id.latestMetric)
        val calendar = Calendar.getInstance()
        var day : Int = calendar.get(Calendar.DAY_OF_MONTH)
        var monthD : Int = calendar.get(Calendar.MONTH) +1 //The Months are numbered from 0 (January) to 11 (December).
        var year : Int = calendar.get(Calendar.YEAR)


        database =  FirebaseDatabase.getInstance().getReference(year.toString()).child(monthD.toString()).child(day.toString())

        database.get().addOnSuccessListener {
            if (it.exists()){
                println(it.childrenCount)
                it.children.forEach { x ->  MetricsData.SnapshotList.add(x) }
            }else{
                Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            println("CANT REAAAD")
            Toast.makeText(this,"Could Not read Data",Toast.LENGTH_SHORT).show()
        }


        if(MetricsData.SnapshotList.size > 0) {
            MetricsData.SnapshotList.sortByDescending { it.key }
            latestMetric.text = getString(R.string.latestMetric,MetricsData.SnapshotList[0].key,MetricsData.SnapshotList[0].value)
        }



    }


}