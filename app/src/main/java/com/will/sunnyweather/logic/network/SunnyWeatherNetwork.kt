package com.will.sunnyweather.logic.network

import android.util.Log
import com.will.sunnyweather.logic.model.DailyResponse
import com.will.sunnyweather.logic.model.PlaceResponse
import com.will.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 网络接口统一入口处
 */
object SunnyWeatherNetwork {

    // 这里执行 retrofit 的请求方法调用
    // 获取 PlaceService
    private val placeService = ServiceCreator.createApiService(PlaceService::class.java)

    // 获取 WeatherService
    private val weatherService = ServiceCreator.create<WeatherService>()

    // 通过使用扩展函数，可以直接得到结果
    suspend fun searchPlace(city: String): PlaceResponse{
       return placeService.searchPlace(city).await<PlaceResponse>()
    }

    suspend fun searchRealtimeWeather(lat: String, lng: String): RealtimeResponse {
        return weatherService.searchRealtimeWeather(lat, lng).await<RealtimeResponse>()
    }

    suspend fun searchDailyWeather(lat: String, lng: String): DailyResponse {
        return weatherService.searchDailyWeather(lat, lng).await<DailyResponse>()
    }

    // 给 call 定义一个扩展函数，并且使用 SuspendCoroutine 将回调进行包装
    private suspend fun <T> Call<T>.await(): T {

        return suspendCoroutine<T> {continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(Throwable("body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, e: Throwable) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }
}