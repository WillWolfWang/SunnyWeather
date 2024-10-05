package com.will.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 使用 object 创建单例对象
 */
private const val BASE_URL = "https://api.caiyunapp.com/"
object ServiceCreator {
    // 初始化 retrofit 对象
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


//    val placeService = retrofit.create(PlaceService::class.java)

    // 创建 api service 类
    fun <T> createApiService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    // 使用 內联 关键字 （可以把代码直接移到具体方法处）
    // 以及泛型实化关键字 reified 可以直接对 T 泛型获取到实际的类型
    inline fun <reified T> create(): T {
        return retrofit.create(T::class.java)
    }
}