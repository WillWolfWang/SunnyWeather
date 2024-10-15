package com.will.sunnyweather.pagetest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WanAndroidApi {

    @GET("article/list/{page}/json")
    fun getWanAndroidData(@Path("page") page: Int, @Query("page_size") pageSize: Int): Call<WanResult>
}