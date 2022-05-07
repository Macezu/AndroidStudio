package com.imber.macaiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.imber.macaiot.R
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*


class MetricsData {
    companion object  {
        var SnapshotList = mutableListOf<DataSnapshot>()
        var DataPoints  = emptyArray<DataPoint>()

    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fetchDataB = findViewById<Button>(R.id.fetchData)
        val graph = findViewById<View>(R.id.graph) as GraphView

        getDataFromFireBase()

        fetchDataB.setOnClickListener {
            populateView()
        }

        // custom label formatter to show currency "EUR"
        // custom label formatter to show currency "EUR"
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    // show normal x values
                    super.formatLabel(value, isValueX) + " :00"
                } else {
                    // show currency for y values
                    super.formatLabel(value, isValueX) + " °C"
                }
            }
        }

        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMaxY(35.0)
        graph.viewport.setMinY(10.0)

/*        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMaxX(23.0)
        graph.viewport.setMinX(0.0)*/

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


        database =  FirebaseDatabase.getInstance().getReference(year.toString()).child(monthD.toString()).child(
            day.toString()
        )

        database.get().addOnSuccessListener {
            if (it.exists()){
                println(it.childrenCount)
                it.children.forEach { x ->  MetricsData.SnapshotList.add(x) }
                populateView()
            }else{
                Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            println("CANT REAAAD")
            Toast.makeText(this, "Could Not read Data", Toast.LENGTH_SHORT).show()
        }


    }

    private fun populateView(){
        val latestTv = findViewById<TextView>(R.id.latestMetric)
        val graph = findViewById<View>(R.id.graph) as GraphView
        var highestMetric : Int = -1
        var tmpIndex  = 0
        var listOfPairs = mutableListOf<Pair<Double, Double>>()

        if(MetricsData.SnapshotList.size > 0) {
            // Get latest time this could be just refactored as a comparator
            MetricsData.SnapshotList.forEachIndexed { index, element ->
                var hourInKey : String = element.key?.get(0).toString()
                if (element.key?.get(1).toString() != ":"){
                    hourInKey = hourInKey.plus(element.key?.get(1).toString())
                }
                if (highestMetric < hourInKey.toInt()) {
                    highestMetric = hourInKey.toInt()
                    tmpIndex = index
                }

                var elementTemperature = element.value.toString().substringBefore("°C")
                elementTemperature = elementTemperature.substring(elementTemperature.length - 5)

                var elementHumidity = element.value.toString().substringAfter("y=")
                elementHumidity = elementHumidity.substring(0, elementHumidity.length - 2)

                var pair  = Pair(hourInKey.toDouble(), elementTemperature.toDouble())
                listOfPairs.add(index, pair)

            }

            listOfPairs.sortBy { it.first }
            listOfPairs.forEach { it -> println(it) }
            var i = 0


            val dPoints = arrayOfNulls<DataPoint>(listOfPairs.size)
            listOfPairs.forEach { it ->
                var dataPoint = DataPoint(it.first, it.second)
                if (!dPoints.contains(dataPoint)){
                    dPoints[i] = dataPoint
                    println(i)
                    println(dataPoint)
                    i++
                }
            }


            if (dPoints.isNotEmpty()) {
                val series = LineGraphSeries(dPoints)
                graph.addSeries(series)

            }

            latestTv.text = getString(
                R.string.latestMetric,
                MetricsData.SnapshotList[tmpIndex].key,
                MetricsData.SnapshotList[tmpIndex].value
            )
        }else {
            Toast.makeText(this, "Snapshot Size = 0", Toast.LENGTH_SHORT).show()
        }
    }






}