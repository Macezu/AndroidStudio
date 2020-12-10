package com.imber.radiofinland

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.size
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.*


class Settings : AppCompatActivity() {

    // Declare
    lateinit var fusedloc: FusedLocationProviderClient
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Init ads
        initAds()

        //Init views
        val etaisyyslider : SeekBar = findViewById(R.id.seekBar)
        val etaisyystv : TextView = findViewById(R.id.etaisyyTV)
        etaisyystv.text = getString(R.string.etaisyysohje,MainActivity.maxdist.toString())
        val btn:Button = findViewById(R.id.gpsbtn)
        var locView: TextView = findViewById(R.id.locationTV)

        //init fusedlocProviderClient
        fusedloc = LocationServices.getFusedLocationProviderClient(this)


        etaisyyslider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MainActivity.maxdist = progressChangedValue.toDouble()
                etaisyystv.text = getString(R.string.etaisyysohje,progressChangedValue.toString())
                MainActivity.reload = true
            }
        })


        btn.setOnClickListener {

            //CheckPermissions
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ){
                //Permissions granted get location
                println("JOKO")
                controlPerms()
            } else {
                println("VAI")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 44
                )
            }
            locView.text = resources.getString(
                R.string.locations,
                MainActivity.currlat.toString(),
                MainActivity.curlong.toString()
            )
            if (MainActivity.curlong != null) {
                locView.visibility = View.VISIBLE
            }
        }

    }

    private fun initAds() {
        MobileAds.initialize(this){}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }



    private fun controlPerms() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        getLastLocation()
    }



    @SuppressLint("MissingPermission")
    fun getLastLocation(){
        println("SAIN?")
        if(isLocationEnabled()) {
            println("ENTÄS TÄÄLLLÄ?")
            fusedloc.lastLocation.addOnCompleteListener { task ->
                var location: Location? = task.result
                println("TÄÄLLÄ $location")
                if (location == null){
                    println("$location NULLLL")
                    NewLocationData()
                } else{
                    println("$location HOMOJAAAAAA")
                    MainActivity.curlong = location.longitude
                    MainActivity.currlat = location.latitude
                    saveLoc(location)
                    MainActivity.reload = true
                }

            }
        }
        else{
            println("PASKEMPI HOMMA")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 44
            )
        }
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("MissingPermission")
    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedloc = LocationServices.getFusedLocationProviderClient(this)
        fusedloc!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            getLastLocation()
        }
    }

    private fun saveLoc(location: Location){
        val sharedPreferences : SharedPreferences? = getSharedPreferences(
            "sharedPrefs",
            Context.MODE_PRIVATE
        )
        val editor : SharedPreferences.Editor? = sharedPreferences!!.edit()
        editor?.apply{
            putString("latKey", location.latitude.toString())
            putString("longKey", location.longitude.toString())
        }?.apply()

    }

    private fun changeToDark() {

        /*
        val switch: Switch = findViewById(R.id.dayNswitch)

        var currentMode =  resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentMode){
            Configuration.UI_MODE_NIGHT_YES -> switch.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> switch.isChecked = false
            else -> switch.isChecked = false
        }

        switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked){
                true ->
                    false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Toast.makeText(this,"Mode Changed",Toast.LENGTH_SHORT).show()
        })*/
    }
}