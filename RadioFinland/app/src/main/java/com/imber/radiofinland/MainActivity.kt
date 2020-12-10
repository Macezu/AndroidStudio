package com.imber.radiofinland

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    companion object{
        var currlat : Double? = null
        var curlong : Double? = null
        var maxdist : Double = 20.0
        var reload : Boolean = false
    }

    lateinit var radioList : MutableList<RecViewItem>
    lateinit var mQueue: RequestQueue
    lateinit var progcircle : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        //Load last know location from shareprefs
        loadlastLoc()

        //Get progress circle
        progcircle = findViewById(R.id.progcircle)

        if (currlat == null){
            Toast.makeText(this, "Hae lähellä olevia asemia, antamalla sijaintisi settings-valikosta", Toast.LENGTH_LONG).show()
        }
        radioList = mutableListOf<RecViewItem>()
        callRest()



    }



    private fun loadlastLoc() {
        val sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        if (sharedPref.getString("latKey", null) != null)
            currlat = sharedPref.getString("latKey", null)?.toDouble()
            curlong = sharedPref.getString("longKey", null)?.toDouble()
        println("${sharedPref.getString("latKey", null)} Do we have a shared pref")
    }

    override fun onResume() {
        super.onResume()
        if (reload)
            progcircle.visibility = View.VISIBLE
            radioList.clear()
            callRest()
            reload = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
            R.id.action_about -> {
                Toast.makeText(this, "ImberTech All rights reserved 2020", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun callRest(){
        mQueue = Volley.newRequestQueue(this)

        val url = "https://opendata.traficom.fi/api/v7/Radioasematiedot?\$select=Municipality%2CStationName%2CFrequency%2CLatitude%2CLongitude%2CLicenseOwner"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    try {
                        var jsonArray = response.getJSONArray("value")
                        for (i in 0 until jsonArray.length()) {
                            val station: JSONObject = jsonArray.getJSONObject(i)

                            // Get Name and Municipality
                            val sname = station.getString("StationName")
                            val munic = station.getString("Municipality")
                            // Position
                            val rLati = station.getString("Latitude")
                            val rLong = station.getString("Longitude")
                            //Get Frequency and trim it.
                            val freqfi = testFreguencylvl(station)
                            // Details
                            val details = "Location: ${rLati}.${rLong}, ${munic}\nLisenssi: ${station.getString("LicenseOwner")}"
                            val img: Drawable = getFoto(sname)

                            when (MainActivity.curlong) {
                                null -> {
                                    var obj = RecViewItem(img, sname, freqfi, details)
                                    if (radioList.size > 120) break
                                    radioList.add(obj)
                                }
                                else -> {
                                    if (maxdist > distance(DMStoDeciDeg(rLati), DMStoDeciDeg(rLong), MainActivity.currlat!!, MainActivity.curlong!!)) {
                                        println(distance(DMStoDeciDeg(rLati), DMStoDeciDeg(rLong), MainActivity.currlat!!, MainActivity.curlong!!))
                                        var obj = RecViewItem(img, sname, freqfi, details)
                                        radioList.add(obj)
                                    } else continue
                                }
                            }

                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    var recview: RecyclerView = findViewById(R.id.recView)
                    recview.adapter = RecAdapter(radioList)
                    recview.layoutManager = LinearLayoutManager(this)
                    recview.setHasFixedSize(true)
                    progcircle.visibility = View.INVISIBLE
                    if (radioList.isEmpty()) {
                        Toast.makeText(this, "Radiokanavia ei löytynyt", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    error.printStackTrace()
                })
        mQueue.add(jsonObjectRequest)


    }

    private fun testFreguencylvl(station: JSONObject) :String {
        val freqa: String
        val freqb: String


        if (station.getInt("Frequency") > 100000000) {
            freqa = station.getString("Frequency").substring(0, 3)
            freqb = station.getString("Frequency").substring(3, 4)
        } else {
            freqa = station.getString("Frequency").substring(0, 2)
            freqb = station.getString("Frequency").substring(2, 4)
        }

         return freqa.plus(".").plus(freqb).plus("MHz")
    }

    private fun getFoto(sname: String): Drawable {
        var sas = sname.replace("ä", "a", true)
        sas = sas.replace(" ", "_", true)
        val assetManager = applicationContext.assets
        val files = assetManager.list("")
            if (files != null) {
                for (elem in files) {
                    var str = ""
                    try {
                        str = elem.substring(0, elem.length - 4)
                    } catch (e : StringIndexOutOfBoundsException){
                        continue
                    }
                    if (sas.contains(str, true)) {
                        try {
                            val ims: InputStream = assetManager.open(elem)
                            return Drawable.createFromStream(ims, null)
                        } catch (e: FileNotFoundException){
                            e.printStackTrace()
                        }

                    }
                }
            }
            return resources.getDrawable(R.drawable.ic_baseline_radio_24)


    }

    private fun DMStoDeciDeg(dms: String) : Double{
        var d = dms.substring(0, 2).toDouble()
        var m = dms.substring(3, 5).toDouble()
        var s = dms.substring(5).toDouble()
        m += s/60
        d += m/60
        return  d
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}