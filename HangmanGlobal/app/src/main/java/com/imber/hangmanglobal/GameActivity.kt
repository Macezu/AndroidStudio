package com.imber.hangmanglobal

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.imber.hangmanglobal.MainActivity.TheWord.hangmanWord

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val chosen : TextView = findViewById(R.id.chosenWTV)
        val btn : Button = findViewById(R.id.btntest)
        //setUnderLine()
        btn.setOnClickListener {
            chosen.text = hangmanWord
        }

        Toast.makeText(this,hangmanWord,Toast.LENGTH_SHORT).show()
    }

    private fun setUnderLine(chosen : TextView) {
        var str  = ""
        for (i in 1.. hangmanWord!!.length){
            str.plus("_")
        }
        print(hangmanWord!!.length)
        chosen.text = str
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
}