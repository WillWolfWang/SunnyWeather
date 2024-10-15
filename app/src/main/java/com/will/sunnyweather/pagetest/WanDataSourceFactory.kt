package com.will.sunnyweather.pagetest

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

class WanDataSourceFactory: DataSource.Factory<Int, WanAndroidModel>() {

//    private val liveDataSource = MutableLiveData<WanDataSource>()

    override fun create(): DataSource<Int, WanAndroidModel> {
        val dataSource = WanDataSource()
//        liveDataSource.postValue(dataSource)
        return dataSource
    }
}