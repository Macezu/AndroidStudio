package com.imber.senorlight



import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.imber.senorlight.LoginActivity.Companion.maxValues
import com.imber.senorlight.LoginActivity.Companion.medianValues
import com.imber.senorlight.LoginActivity.Companion.timestampValues
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import java.text.Format
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var latest : TextView
    lateinit var graph: GraphView
    lateinit var myRef : DatabaseReference
    lateinit var sendbtn : Button


    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        graph = findViewById<View>(R.id.graph) as GraphView
        latest = findViewById(R.id.latestTV)
        sendbtn = findViewById(R.id.turnbtn)

        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    val formatter: Format = SimpleDateFormat("HH:mm")
                    return formatter.format(value)
                }
                return super.formatLabel(value, isValueX)
            }
        }



        // Read from the database
        val database = FirebaseDatabase.getInstance()
        myRef = database.reference

        sendbtn.setOnClickListener {
            val SDK_INT = Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                        .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                val cls2 = ClientSend()
                cls2.run()
            }
            sendbtn.isClickable = false
            sendbtn.visibility = View.INVISIBLE
            Toast.makeText(this, "Starting sensor",Toast.LENGTH_SHORT).show()

        }


        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Init
                var data: HashMap<String, ArrayList<Long>>
                //Check if data from today
                if (dataSnapshot.child(getDate("dd_M")).value != null) {
                    data = dataSnapshot.child(getDate("dd_M")).value as HashMap<String, ArrayList<Long>>
                    println("value Data ${data}")

                } else {
                    // Get last data from yesterday
                    data = dataSnapshot.child("27_2").value as HashMap<String, ArrayList<Long>>
                }

                // All Values Listed
                maxValues = ArrayList(data["max_value"])
                medianValues = ArrayList(data["median_value"])
                timestampValues = ArrayList(data["ts"])

                // Latest Values
                var latestmax = maxValues!!.elementAt(maxValues!!.size - 1).toString()
                var latestmed = medianValues!!.elementAt(medianValues!!.size - 1).toString()
                // Timestamp
                var ts = timeStamp()
                Toast.makeText(this@MainActivity,"New Value Added",Toast.LENGTH_SHORT).show()
                // Latest Text update
                println("values ${latestmax}")
                latest.text = getString(R.string.lorem, ts, latestmax, latestmed)


                fillGraph(graph)


            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                println("FAILED TO READ values")
            }
        })




    }

    fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

    }

    private fun fillGraph(graph: GraphView) {

        graph.title = "MAX"
        graph.titleColor = Color.BLUE
        graph.titleTextSize = 70f
        //ViewPort Y
        graph.viewport.isYAxisBoundsManual = true;
        graph.viewport.setMinY(0.0);
        graph.viewport.setMaxY(1000.0)

        //ViewPort X
        graph.viewport.isXAxisBoundsManual = true;
        graph.viewport.setMinX(timestampValues!![0].toDouble());
        var maxX = timestampValues!![timestampValues!!.size - 1] +(1*60*60*1000) //x-axis +1 hour
        graph.viewport.setMaxX(timestampValues!![timestampValues!!.size / 2].toDouble());

        // enable scrolling and zooming
        graph.viewport.isScalable = true;
        graph.viewport.isScrollable = true; // enables horizontal scrolling



        //#region style
        graph.gridLabelRenderer.isHighlightZeroLines = true
        graph.gridLabelRenderer.gridColor = Color.LTGRAY


        //Vertical Style
        //graph.gridLabelRenderer.verticalAxisTitle = "VALUE"
        //graph.gridLabelRenderer.verticalAxisTitleTextSize = 70f
        //graph.gridLabelRenderer.verticalAxisTitleColor = Color.GREEN
        graph.gridLabelRenderer.verticalLabelsColor = Color.RED


        // Horizontal Style
        graph.gridLabelRenderer.horizontalAxisTitle = "MEDIAN"
        graph.gridLabelRenderer.horizontalAxisTitleTextSize = 70f
        graph.gridLabelRenderer.horizontalAxisTitleColor = Color.GREEN
        graph.gridLabelRenderer.padding = 5
        graph.gridLabelRenderer.labelsSpace = 5
        graph.gridLabelRenderer.horizontalLabelsColor = Color.RED

        //Show Legend
        //graph.legendRenderer.isVisible = true
        //graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP;

        //Add two Series


        graph.addSeries(makeLineSeries());
        graph.addSeries(makePointSeries());


        // endregion




    }

    fun createGraph(graph: GraphView){

    }

    private fun makePointSeries(): PointsGraphSeries<DataPoint>{
        var series : PointsGraphSeries<DataPoint> = PointsGraphSeries()
        var x : Date?; var y :Double;

        if (medianValues != null){
            for (i in 0 until medianValues!!.size) {
                x = convertLongToTime(timestampValues!![i])
                y = medianValues!![i].toDouble()
                series.appendData(DataPoint(x, y), true, 30)
            }

        }
        //series.title = "Median"

        series.shape = PointsGraphSeries.Shape.TRIANGLE;
        series.color = Color.GREEN
        return  series

    }


    private fun makeLineSeries():LineGraphSeries<DataPoint> {
        var series : LineGraphSeries<DataPoint> = LineGraphSeries()
        var x : Date?; var y :Double;

        if (maxValues != null){
            for (i in 0 until maxValues!!.size) {
                x = convertLongToTime(timestampValues!![i])

                //println("values time ${convertLongToTime(timestampValues!![i])}")
                y = maxValues!![i].toDouble()
                series.appendData(DataPoint(x, y), true, 30)
            }
        }

        //series.title = "Max"
        series.isDrawDataPoints = true
        series.thickness = 7
        series.dataPointsRadius = 15f
        //series.isDrawBackground = true


        return  series
    }

    fun getDate(format: String): String {

        var current = LocalDateTime.now()
        var zonedUTC: ZonedDateTime = current.atZone(ZoneId.of("Europe/Helsinki"))
        zonedUTC.plusHours(2)
        val formatter = DateTimeFormatter.ofPattern(format)
        val formatted = zonedUTC.format(formatter)
        return formatted.toString()
    }

    fun timeStamp(): String{
        DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        return DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withZone(ZoneOffset.of("+02:00"))
                .format(Instant.now())

    }

    fun convertLongToTime(time: Long): Date {
        return Date(time)
    }




}