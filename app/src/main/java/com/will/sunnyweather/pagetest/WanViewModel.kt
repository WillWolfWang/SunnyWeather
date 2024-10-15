package com.will.sunnyweather.pagetest

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

class WanViewModel: ViewModel() {
    val  wanPageList: LiveData<PagedList<WanAndroidModel>>
    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(WanDataSource.PAGE_SIZE)
            .setPrefetchDistance(3)
            .setInitialLoadSizeHint(WanDataSource.PAGE_SIZE * 4)
            .setMaxSize(65535)
            .build()
        wanPageList = LivePagedListBuilder(WanDataSourceFactory(), config).build()
    }
}