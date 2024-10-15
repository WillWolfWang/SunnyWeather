package com.will.sunnyweather.pagetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.will.sunnyweather.R

class WanPageListAdapter: PagedListAdapter<WanAndroidModel, RecyclerView.ViewHolder>(diff_callback) {

    object diff_callback : DiffUtil.ItemCallback<WanAndroidModel>() {
        override fun areItemsTheSame(oldItem: WanAndroidModel, newItem: WanAndroidModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: WanAndroidModel,
            newItem: WanAndroidModel
        ): Boolean {
             return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item =  getItem(position)
        (holder as WanViewHolder).tv.setText(item?.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_textview, parent, false)
        return WanViewHolder(view)
    }

    class WanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv = itemView.findViewById<TextView>(R.id.tv_item)
    }
}