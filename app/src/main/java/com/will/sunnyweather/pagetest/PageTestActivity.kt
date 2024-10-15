package com.will.sunnyweather.pagetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.will.sunnyweather.R

class PageTestActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_test)

        val rv = findViewById<RecyclerView>(R.id.recycleView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        val adapter = WanPageListAdapter()
        val viewModel = ViewModelProvider(this).get(WanViewModel::class.java)
        viewModel.wanPageList.observe(this, Observer {
            adapter.submitList(it)
        })
        rv.adapter = adapter
    }
}