package com.will.sunnyweather.logic.network

import com.will.sunnyweather.logic.model.PlaceResponse
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
    private var placeService = ServiceCreator.createApiService(PlaceService::class.java)

    // 通过使用扩展函数，可以直接得到结果
    suspend fun searchPlace(city: String): PlaceResponse{
       return placeService.searchPlace(city).await<PlaceResponse>()
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