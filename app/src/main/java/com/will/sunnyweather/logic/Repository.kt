package com.will.sunnyweather.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.will.sunnyweather.logic.model.Place
import com.will.sunnyweather.logic.model.PlaceResponse
import com.will.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

/**
 * 仓库层
 */
object Repository {
    fun searchPlace(city: String):LiveData<Result<List<Place>>> {
        // 使用 liveData 方法对网络请求结果封装为一个 liveData
        val liveData = liveData<Result<List<Place>>>(Dispatchers.IO) {

            val result:Result<List<Place>> = try {
                val response = SunnyWeatherNetwork.searchPlace(city)
                if (response.status == "ok") {
                    val place = response.places
                    // 用 Result 进行统一封装结果
                    Result.success<List<Place>>(place)
                } else {
                    Result.failure<List<Place>>(Exception("failure statue is ${response.status}"))
                }
            } catch (e: Exception) {
                Result.failure<List<Place>>(Exception(e))
            }
            // 将结果发射出去，类似 setValue
            emit(result)
        }
        return liveData
    }


}