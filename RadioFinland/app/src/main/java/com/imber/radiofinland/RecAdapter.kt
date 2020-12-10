package com.imber.radiofinland

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class RecAdapter(private val radioList: List<RecViewItem> ) : RecyclerView.Adapter<RecAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recview_main,
        parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = radioList[position]

        holder.imageView.setImageDrawable(currentItem.imageResource)
        holder.headerView.text = currentItem.headLine
        holder.fregView.text = currentItem.frequency
        holder.detailView.text = currentItem.details
    }

    override fun getItemCount() = radioList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById(R.id.logoiView)
        val headerView : TextView = itemView.findViewById(R.id.titletv)
        val fregView : TextView = itemView.findViewById(R.id.freqtv)
        val detailView : TextView = itemView.findViewById(R.id.detailstv)

    }
}