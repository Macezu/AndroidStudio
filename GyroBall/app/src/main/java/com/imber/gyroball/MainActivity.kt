package com.imber.gyroball

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() , SensorEventListener {

    // Init sensorManager
    private lateinit var sensorManager: SensorManager
    // Init found sensor
    private var mSensor: Sensor? = null
    private lateinit var valTw: TextView
    private lateinit var myView: MyView

    companion object{
        var x:Float = 1f
        var y: Float =1f
        var z: Float = 1f
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        valTw = findViewById(R.id.valtw)
        myView = findViewById(R.id.myViewBase)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager



        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            mSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

            println("HOMOJAAA $mSensor")
        } else {
            Toast.makeText(this, "Go buy a better phone!", Toast.LENGTH_SHORT).show()
        }

    }



    override fun onSensorChanged(event: SensorEvent) {
        valTw.text = "X:${event.values[0]}, Y:${event.values[1]},Z:${event.values[2]}"
        val orientation = resources.configuration.orientation
        if (Configuration.ORIENTATION_LANDSCAPE === orientation) {
            //Do SomeThing; // Landscape
            y += event.values[0]*25
            x += event.values[1]*15
        } else {
            //Do SomeThing;  // Portrait
            x += event.values[0]*18
            y += event.values[1]*7
        }
        myView.moveBall()






    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    override fun onResume() {
        super.onResume()
        mSensor?.also { GyroScope ->
            sensorManager.registerListener(this, GyroScope, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


}
