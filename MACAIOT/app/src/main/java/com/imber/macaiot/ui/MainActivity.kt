package com.imber.macaiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.snapshot.Index
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
        val fetchDataB = findViewById<Button>(R.id.fetchData)


        getDataFromFireBase()

        fetchDataB.setOnClickListener {
            fetchLoadedMetrics()
        }

    }

    override fun onResume() {
        super.onResume()

        getDataFromFireBase()
    }


    private fun getDataFromFireBase(){
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




    }

    private fun fetchLoadedMetrics(){
        val latestTv = findViewById<TextView>(R.id.latestMetric)
        var highestMetric : Int = -1
        var tmpIndex : Int = 0

        if(MetricsData.SnapshotList.size > 0) {

            // Get latest time this could be just refactored as a comparator
            MetricsData.SnapshotList.forEachIndexed { index,element ->
                var value : String = element.key?.get(0).toString()
                if (element.key?.get(1).toString() != ":"){
                    value = value.plus(element.key?.get(1).toString())
                }
                if (highestMetric < value.toInt()) {
                    highestMetric = value.toInt()
                    tmpIndex = index
                }
            }
            latestTv.text = getString(R.string.latestMetric,MetricsData.SnapshotList[tmpIndex].key,MetricsData.SnapshotList[tmpIndex].value)
        }else {
            Toast.makeText(this,"Snapshot Size = 0",Toast.LENGTH_SHORT).show()
        }
    }








}