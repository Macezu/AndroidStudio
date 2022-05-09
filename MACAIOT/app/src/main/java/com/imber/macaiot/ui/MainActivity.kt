package com.imber.macaiot.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.imber.macaiot.R
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import java.text.NumberFormat
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

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        fetchDataB.setOnClickListener {
            populateView()
        }


        // custom label formatter to show
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    // show normal x values
                    super.formatLabel(value, isValueX) + " :00"
                } else {
                    // show currency for y values
                        if (value >= 35.0) {
                            super.formatLabel(value, isValueX) + " %"
                        } else {
                            super.formatLabel(value, isValueX) + " °C"
                        }

                }
            }
        }

/*        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(10.0)
        graph.viewport.setMaxY(80.0)*/

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(23.0)


/*        // enable scaling and scrolling
        graph.viewport.isScalable = true; // enables horizontal zooming and scrolling
        graph.viewport.setScalableY(true); // enables vertical zooming and scrolling*/
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
        var listOfTempPairs = mutableListOf<Pair<Double, Double>>()
        var listOfHumPairs = mutableListOf<Pair<Double, Double>>()


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

                //Temp.
                var elementTemperature = element.value.toString().substringBefore("°C")
                elementTemperature = elementTemperature.substring(elementTemperature.length - 5)

                var tempPair  = Pair(hourInKey.toDouble(), elementTemperature.toDouble())
                listOfTempPairs.add(index, tempPair)

                //humidity
                var elementHumidity = element.value.toString().substringAfter("y=")
                elementHumidity = elementHumidity.substring(0, elementHumidity.length - 2)

                var humPair  = Pair(hourInKey.toDouble(), elementHumidity.toDouble())
                listOfHumPairs.add(index, humPair)

            }

            listOfTempPairs.sortBy { it.first }
            listOfTempPairs.forEach { it -> println(it) }
            var i = 0

            val tempdataPoints = arrayOfNulls<DataPoint>(listOfTempPairs.size)
            listOfTempPairs.forEach { it ->
                var dataPoint = DataPoint(it.first, it.second)
                if (!tempdataPoints.contains(dataPoint)){
                    tempdataPoints[i] = dataPoint
                    i++
                }
            }

            if (tempdataPoints.isNotEmpty()) {
                val series = LineGraphSeries(tempdataPoints)
                series.title = "Temperature"
                series.color = (Color.RED)
                graph.addSeries(series)

            }

            //humidity
            listOfHumPairs.sortBy { it.first }
            listOfHumPairs.forEach { it -> println(it) }
            i = 0

            val humDataPoints = arrayOfNulls<DataPoint>(listOfHumPairs.size)
            listOfHumPairs.forEach { it ->
                var dataPoint = DataPoint(it.first, it.second)
                if (!humDataPoints.contains(dataPoint)){
                    humDataPoints[i] = dataPoint
                    i++
                }
            }

            if (humDataPoints.isNotEmpty()) {
                val series2 = PointsGraphSeries(humDataPoints)
                series2.title = "Humidity"
                series2.color = (Color.BLUE)
                graph.addSeries(series2)

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