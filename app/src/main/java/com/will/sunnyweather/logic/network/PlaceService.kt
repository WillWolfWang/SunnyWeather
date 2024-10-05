package com.will.sunnyweather.logic.network

import com.will.sunnyweather.SunnyWeatherApplication
import com.will.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 搜索城市的 api 接口
interface PlaceService {

    // v2/place?query=北京&toekn=EeM8Hh2LYbZswoxN&lang=zh_CN
    // @Query 注解可以直接拼接到后面
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlace(@Query("query") city: String): Call<PlaceResponse>
}