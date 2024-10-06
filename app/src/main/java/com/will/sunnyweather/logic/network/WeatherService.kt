package com.will.sunnyweather.logic.network

import com.will.sunnyweather.SunnyWeatherApplication
import com.will.sunnyweather.logic.model.DailyResponse
import com.will.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun searchDailyWeather(@Path("lat") lat: String, @Path("lng") lng: String):Call<DailyResponse>

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun searchRealtimeWeather(@Path("lat") lat: String, @Path("lng") lng: String):Call<RealtimeResponse>
}