package com.will.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.will.sunnyweather.logic.Repository
import com.will.sunnyweather.logic.model.Place

// 城市请求的 viewModel
class PlaceViewModel: ViewModel() {
    // 城市集合缓存，缓存 ui 界面上的数据
    val placeList = ArrayList<Place>()


    // 定义一个查询城市的 liveData
    private val searchLiveData = MutableLiveData<String>()

    // 通过 switchMap 将网络请求返回来的每次都是 新 的 liveData 转换成一个
    // 可观察的 liveData
    val placeLiveData = searchLiveData.switchMap {city->
        Repository.searchPlace(city)
    }

    fun searchPlace(city: String) {
        searchLiveData.value = city
    }
}