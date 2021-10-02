package com.example.dmhourtracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dmhourtracker.databinding.FragmentFirstBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var currentDate: String
    private lateinit var mainBtn: Button
    private lateinit var mainTxt: TextView
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainBtn = getView()!!.findViewById(R.id.strButton)
        mainTxt = getView()!!.findViewById(R.id.homeTV)
        val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss")
        currentDate = sdf.format(Date()) // mut tää on str?


        if (arguments != null) {
            val currentDate = arguments!!.getString("userId")
        }

        setTexts(mainTxt, mainBtn, currentDate )
        binding.strButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            //Toast.makeText(activity, "Time: $currentDate", Toast.LENGTH_SHORT).show()
            //TODO Onko Sharedprefsistä löytyvä aloitusaika tallennettu? jos ei tallenna tämä aika, else kirjaa lopetus ja laske kulunut aika
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTexts(mainTxt: TextView, mainBtn: Button, currentDate: String){
        //TODO Start or stop depeding if sharedprefs contains start Time
        mainTxt.text = String.format(resources.getString(R.string.main_tv), currentDate,"Start")
        mainBtn.text = String.format(resources.getString(R.string.main_btn), "Start")
    }

}