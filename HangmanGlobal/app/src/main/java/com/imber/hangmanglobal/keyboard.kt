package com.imber.hangmanglobal

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import java.lang.Exception


class keyboard : Fragment(R.layout.fragment_keyboard), View.OnClickListener   {

    var mCallback: OnLetterSelectedListener? = null
    private val RIGHTTAGS = "RIGHTTAGS"
    private val WRONGTAGS = "WRONGTAGS"
    //Save rights and wrongs to preserve color if orientation changed
    var arrofrights = arrayListOf<Int>()
    var arrofwrongs = arrayListOf<Int>()
    var ids : Array<Int> = arrayOf(R.id.a,R.id.b,R.id.c,R.id.d,R.id.e,R.id.f,R.id.g,R.id.h,R.id.i,R.id.j,R.id.k,R.id.l,R.id.m,R.id.n,R.id.o,R.id.p,R.id.q,R.id.r,R.id.s,R.id.t,R.id.u,R.id.v,R.id.w,R.id.x,R.id.y,R.id.z,R.id.å,R.id.ä,R.id.ö)


    // Container Activity must implement this interface
    interface OnLetterSelectedListener {
        fun onCorrectSelected(letter: String)
        fun onWrongSelected(letter: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            activity as OnLetterSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnLetterSelectedListener"
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        checksaveState(view,savedInstanceState)
        super.onActivityCreated(savedInstanceState)
    }

    private fun checksaveState(v: View?, savedInstanceState: Bundle?) {
            var right = savedInstanceState?.getIntegerArrayList(RIGHTTAGS)
            var wrong = savedInstanceState?.getIntegerArrayList(WRONGTAGS)
        try {
            if (wrong != null && wrong.size > 0) {
                arrofwrongs = wrong
            }
            if (right != null && right.size > 0) {
                arrofrights = right
            }
            for (elem in arrofrights) {
                var btn : Button = v!!.findViewById(elem)
                setRightBtnColor(btn)
            }
            for (i in arrofwrongs) {
                var btn : Button = v!!.findViewById(i)
                setWrongBtnColor(btn)
            }
        } catch (e : Exception){
            return
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        println("Täällä tallennettu tila")
        outState.putIntegerArrayList(RIGHTTAGS, arrofrights)
        outState.putIntegerArrayList(WRONGTAGS, arrofwrongs)
        super.onSaveInstanceState(outState)
    }



    override fun onClick(v: View?) {
        for (i in ids) {
            if (v!!.id == i) {
                if (MainActivity.hangmanWord!!.contains(v.tag.toString())) {
                    mCallback!!.onCorrectSelected(v.tag.toString())
                    setRightBtnColor(v)
                    //Add to list for saving
                    arrofrights.add(v.id)
                } else {
                    mCallback!!.onWrongSelected(v.tag.toString())
                    setWrongBtnColor(v)
                    //Add to list for saving
                    arrofwrongs.add(v.id)
                }
            }
        }

    }

    fun setRightBtnColor(v: View) {
        //Version Check set color to green
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) v.background.setTint(
            requireContext().getColor(
                R.color.rightgreen
            )
        ) else v.background.alpha = 10
    }

    fun setWrongBtnColor(v: View) {
        v.background.alpha = 10
    }

    fun initButtons(view: View) {
        val a: Button = view.findViewById(R.id.a)
        a.setOnClickListener(this) // calling onClick() method
        val b: Button = view.findViewById(R.id.b)
        b.setOnClickListener(this)
        val c: Button = view.findViewById(R.id.c)
        c.setOnClickListener(this)
        val d: Button = view.findViewById(R.id.d)
        d.setOnClickListener(this)
        val e: Button = view.findViewById(R.id.e)
        e.setOnClickListener(this)
        val f: Button = view.findViewById(R.id.f)
        f.setOnClickListener(this)
        val g: Button = view.findViewById(R.id.g)
        g.setOnClickListener(this)
        val h: Button = view.findViewById(R.id.h)
        h.setOnClickListener(this)
        val i: Button = view.findViewById(R.id.i)
        i.setOnClickListener(this)
        val j: Button = view.findViewById(R.id.j)
        j.setOnClickListener(this)
        val k: Button = view.findViewById(R.id.k)
        k.setOnClickListener(this)
        val l: Button = view.findViewById(R.id.l)
        l.setOnClickListener(this)
        val m: Button = view.findViewById(R.id.m)
        m.setOnClickListener(this)
        val n: Button = view.findViewById(R.id.n)
        n.setOnClickListener(this)
        val o: Button = view.findViewById(R.id.o)
        o.setOnClickListener(this)
        val p: Button = view.findViewById(R.id.p)
        p.setOnClickListener(this)
        val q: Button = view.findViewById(R.id.q)
        q.setOnClickListener(this)
        val r: Button = view.findViewById(R.id.r)
        r.setOnClickListener(this)
        val s: Button = view.findViewById(R.id.s)
        s.setOnClickListener(this)
        val t: Button = view.findViewById(R.id.t)
        t.setOnClickListener(this)
        val u: Button = view.findViewById(R.id.u)
        u.setOnClickListener(this)
        val v: Button = view.findViewById(R.id.v)
        v.setOnClickListener(this)
        val w: Button = view.findViewById(R.id.w)
        w.setOnClickListener(this)
        val x: Button = view.findViewById(R.id.x)
        x.setOnClickListener(this)
        val y: Button = view.findViewById(R.id.y)
        y.setOnClickListener(this)
        val z: Button = view.findViewById(R.id.z)
        z.setOnClickListener(this)
        val å: Button = view.findViewById(R.id.å)
        å.setOnClickListener(this)
        val ä: Button = view.findViewById(R.id.ä)
        ä.setOnClickListener(this)
        val ö: Button = view.findViewById(R.id.ö)
        ö.setOnClickListener(this)
    }

}