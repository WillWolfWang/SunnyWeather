package com.will.sunnyweather.logic.dao

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.will.sunnyweather.SunnyWeatherApplication
import com.will.sunnyweather.logic.model.Place

// 使用 sp 保存城市
object PlaceDao {

    private fun getSharePreferences(): SharedPreferences {
        return SunnyWeatherApplication.context.
        getSharedPreferences("sun_weather", Context.MODE_PRIVATE)
    }

    fun getPlace(): Place {
        val json = getSharePreferences().getString("place", "")
        return Gson().fromJson(json, Place::class.java)
    }

    fun savePlace(place: Place) {
        getSharePreferences().edit().putString("place", Gson().toJson(place)).apply()
    }

    fun isPlaceSave(): Boolean {
        return getSharePreferences().contains("place")
    }
}