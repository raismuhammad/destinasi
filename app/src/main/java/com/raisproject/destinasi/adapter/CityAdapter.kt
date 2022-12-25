package com.raisproject.destinasi.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raisproject.destinasi.R
import com.raisproject.destinasi.model.City
import com.raisproject.destinasi.ui.destination.DestinationListActivity
import com.raisproject.destinasi.ui.loginuser.LoginActivity

class CityAdapter(private val cityArrayList: ArrayList<City>) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_city, parent, false)
        return CityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val currentItem = cityArrayList[position]

        holder.cityName.text = currentItem.cityName
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .into(holder.cityImage)

        holder.itemView.setOnClickListener {
//            val idCity = currentItem.id
            val cityName = currentItem.cityName

            val intent = Intent(it.context, DestinationListActivity::class.java)
            intent.putExtra("cityName", cityName)
            it.context.startActivity(intent)

        }
    }
    override fun getItemCount(): Int {
        return cityArrayList.size

    }

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cityName: TextView = view.findViewById(R.id.tv_city)
        val cityImage: ImageView = view.findViewById(R.id.iv_city)
        val cvItem: CardView = view.findViewById(R.id.cv_city)
//        val urlLoc: TextView = view.findViewById(R.id.tv_url)
    }

}