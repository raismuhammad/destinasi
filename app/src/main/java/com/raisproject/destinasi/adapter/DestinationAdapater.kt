package com.raisproject.destinasi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raisproject.destinasi.R
import com.raisproject.destinasi.ui.detailpage.DetailPageActivity
import com.raisproject.destinasi.model.Destination

class DestinationAdapater(private val destinationArrayList: ArrayList<Destination>) : RecyclerView.Adapter<DestinationAdapater.DestinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.destination_item, parent, false)
        return DestinationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val currentItem = destinationArrayList[position]

        holder.destinationName.text = currentItem.destinationName

        Glide.with(holder.itemView.context)
            .load(currentItem.destinationImage)
            .into(holder.destinationImage)

        holder.itemView.setOnClickListener {
            val destinationId = currentItem.id
            val destinationName = currentItem.destinationName
            val intent = Intent(it.context, DetailPageActivity::class.java)
            intent.putExtra("destinationName", destinationName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return destinationArrayList.size
    }

    class DestinationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val destinationName: TextView = view.findViewById(R.id.tv_destination_name)
        val destinationImage: ImageView = view.findViewById(R.id.iv_destination_image)
    }
}