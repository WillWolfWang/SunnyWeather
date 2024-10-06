package com.will.sunnyweather.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.will.sunnyweather.logic.Repository
import com.will.sunnyweather.logic.model.Location
import com.will.sunnyweather.logic.model.Weather

class WeatherViewModel: ViewModel() {
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    // 定义一个查询天气的 liveData
    private val locationLiveData = MutableLiveData<Location>()
    val weatherLiveData: LiveData<Result<Weather>> = locationLiveData.switchMap { location->
        Repository.searchWeather(location.lat, location.lng)
    }
    fun searchWeather(lat: String, lng: String) {
        locationLiveData.value = Location(lat, lng)
    }


}