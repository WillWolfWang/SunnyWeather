package com.will.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 实时天气预报的 bean 类
 *  {
 *      "status": "ok",
 *      "result": {
 *          "realtime": {
 *              "temperature": 17.96,
 *              "skycon": "CLOUDY",
 *              "air_quality": {
 *                  "aqi": {
 *                      "chn": 83
 *                   }
 *               }
 *           }
 *       }
 *  }
 *
 */
data class RealtimeResponse(val status: String, val result: RealtimeResult) {
    // 将类放在 RealtimeResponse 模型内部，防止出现和其他接口数据模型类有同名的冲突情况
    data class RealtimeResult(val realtime: Realtime)

    data class Realtime(val temperature: Float, val skycon: String, @SerializedName("air_quality") val airQuality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)
}

