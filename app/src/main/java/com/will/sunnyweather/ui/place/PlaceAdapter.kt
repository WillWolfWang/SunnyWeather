package com.will.sunnyweather.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.will.sunnyweather.R
import com.will.sunnyweather.logic.model.Place

// 城市适配器
class PlaceAdapter(val places: List<Place>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val place = places.get(position)
        (holder as PlaceViewHolder).bindView(place)
    }

    inner class PlaceViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.placeName)
        val tvAddress = view.findViewById<TextView>(R.id.placeAddress)
        fun bindView(place: Place) {
            tvName.text = place.name
            tvAddress.text = place.address
        }
    }
}