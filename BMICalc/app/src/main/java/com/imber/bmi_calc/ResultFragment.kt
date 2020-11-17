package com.imber.bmi_calc


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ResultFragment : Fragment(R.layout.fragment_third) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Haetaan introteksti
        var introText: TextView = view.findViewById(R.id.twResultIntro)
        //Asetetaan nimi introtekstiin
        formatNameInTextView(introText)
        //Haetaan viimeisin BMI
        var latestbmi: TextView = view.findViewById(R.id.twCurrentBmi)
        //Calculate Bmi show it to user
        latestbmi.text = MainActivity.calculateBMI()
        // Instance of GraphView and lets make History!
        var historyview: GraphView = view.findViewById(R.id.graphView)
        historyview.addSeries(makeHistory())

        //Change x axis formatting
        historyview.getGridLabelRenderer().setLabelFormatter(DateAsXAxisLabelFormatter(getActivity()));
        historyview.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        // activate horizontal scrolling
        historyview.viewport.isScalable = true





        view.findViewById<Button>(R.id.buttonRetunHome).setOnClickListener {


            val firstFrag = FirstFragment()
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.flFragment, firstFrag)

                commit()
            }

        }

    }

    private fun makeHistory() :LineGraphSeries<DataPoint> {
        var series : LineGraphSeries<DataPoint> = LineGraphSeries()
        var x : Date?; var y :Double;


        if (MainActivity.arrayofBmis != null)
            for (value in MainActivity.arrayofBmis!!){
                x = value.date
                y = value.bmi.toString().toDouble()
                series.appendData(DataPoint(x, y), true, 100)
            }
        series.isDrawDataPoints = true
        return  series

    }

    private fun formatNameInTextView(_introText: TextView){
        //Ladataan tallennettu nimi, jotta voidaan asettaa se näkyviin
        val sharedPreferences : SharedPreferences? = activity?.getSharedPreferences(
                "sharedPrefs",
                Context.MODE_PRIVATE
        )
        val name : kotlin.String? = sharedPreferences!!.getString("nameKey", null)
        val res  = activity?.resources
        // Formatoidaan intro teksti uusiki laitetaan tallenettu nimi
        val _text:String = String.format(res!!.getString(R.string.resultHeader), name)
        //Päivitetään intro teksti
        _introText.text = _text
    }



}

