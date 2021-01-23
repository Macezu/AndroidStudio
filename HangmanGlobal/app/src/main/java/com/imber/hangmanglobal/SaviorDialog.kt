package com.imber.hangmanglobal

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException


class SaviorDialog : AppCompatDialogFragment() {

    var giveResult : GiveResultlistener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it,R.style.MyDialogTheme)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view : View = inflater.inflate(R.layout.fragment_savior_dialog,null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { dialog, id ->
                       giveResult!!.yesSelected(true)

                    })
                .setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { dialog, id ->
                        giveResult!!.yesSelected(false)
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        giveResult = try {
            activity as GiveResultlistener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnLetterSelectedListener"
            )
        }
    }

    interface GiveResultlistener{
        fun yesSelected(r : Boolean)
    }




}