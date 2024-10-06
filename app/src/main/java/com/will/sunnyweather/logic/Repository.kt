package com.will.sunnyweather.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.will.sunnyweather.logic.dao.PlaceDao
import com.will.sunnyweather.logic.model.DailyResponse
import com.will.sunnyweather.logic.model.Place
import com.will.sunnyweather.logic.model.PlaceResponse
import com.will.sunnyweather.logic.model.RealtimeResponse
import com.will.sunnyweather.logic.model.Weather
import com.will.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * 仓库层
 */
object Repository {

    fun savePlace(place: Place) {
        PlaceDao.savePlace(place)
    }

    fun getPlace(): Place {
        return PlaceDao.getPlace()
    }

    fun isSavePlace(): Boolean {
        return PlaceDao.isPlaceSave()
    }


    fun searchPlace(city: String): LiveData<Result<List<Place>>> {
        // 使用 liveData 方法对网络请求结果封装为一个 liveData
//        val liveData = liveData<Result<List<Place>>>(Dispatchers.IO) {
//            val result: Result<List<Place>> = try {
//                val response = SunnyWeatherNetwork.searchPlace(city)
//                if (response.status == "ok") {
//                    val place = response.places
//                    // 用 Result 进行统一封装结果
//                    Result.success<List<Place>>(place)
//                } else {
//                    Result.failure<List<Place>>(Exception("failure statue is ${response.status}"))
//                }
//            } catch (e: Exception) {
//                Result.failure<List<Place>>(Exception(e))
//            }
//            // 将结果发射出去，类似 setValue
//            emit(result)
//        }

        val liveData = fire(Dispatchers.IO) {
            val response = SunnyWeatherNetwork.searchPlace(city)
            if (response.status == "ok") {
                val place = response.places
                // 用 Result 进行统一封装结果
                Result.success<List<Place>>(place)
            } else {
                Result.failure<List<Place>>(Exception("failure statue is ${response.status}"))
            }
        }

        return liveData
    }

    fun searchWeather(lat: String, lng: String): LiveData<Result<Weather>> {
        val liveData: LiveData<Result<Weather>> = liveData<Result<Weather>>(Dispatchers.IO) {
            val result: Result<Weather> = try {
                // 使用 coroutineScope 方法创建一个协程域
                // 这里可以创建一个协程作用域，然后使用异步 + 同步的方式，等两个请求结果都拿到数据
                // 后，进行数据合并
                coroutineScope {
                    // 异步请求两个网络
                    val deferredRealtime = async {
                        SunnyWeatherNetwork.searchRealtimeWeather(lat, lng)
                    }

                    val deferredDaily = async {
                        SunnyWeatherNetwork.searchDailyWeather(lat, lng)
                    }

                    // 等两个网络请求结果都完成，再进行数据合并
                    // 假如 dailyResponse 先执行完，但是还是要等待 realtimeResponse 执行完
                    // 才会走接下来的语句
                    val realtimeResponse: RealtimeResponse = deferredRealtime.await()
                    val dailyResponse: DailyResponse = deferredDaily.await()

                    if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                        val realtime = realtimeResponse.result.realtime
                        val daily = dailyResponse.result.daily
                        val weather = Weather(realtime, daily)
                        Result.success<Weather>(weather)
                    } else {
                        Result.failure(Exception("realtime: ${realtimeResponse.status}, daily: ${dailyResponse.status}"))
                    }

                }
            } catch (e: Exception) {
                Result.failure<Weather>(e)
            }

            emit(result)
        }
        return liveData
    }


    private fun <T> fire(
        context: CoroutineContext,
        block: suspend () -> Result<T>
    ): LiveData<Result<T>> {
        val liveData: LiveData<Result<T>> = liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
        return liveData
    }
}