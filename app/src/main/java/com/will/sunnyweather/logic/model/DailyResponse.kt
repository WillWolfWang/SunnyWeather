package com.will.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName


/**
 * 未来几天天气信息接口
 *  {
 *      "status": "ok",
 *      "result": {
 *          "daily": {
 *              "temperature": [
 *                  {
 *                      "date": "2024-10-05T00:00+08:00",
 *                      "max": 22.14,
 *                      "min": 9.16,
 *                      "avg": 15.95
 *                  }
 *              ],
 *              "skycon": [
 *                  {
 *                      "date": "2024-10-05T00:00+08:00",
 *                      "value": "CLOUDY"
 *                  }
 *              ],
 *              "life_index": {
 *                  "coldRisk": [
 *                      {
 *                          "date": "2024-10-05T00:00+08:00",
 *                          "index": "4",
 *                          "desc": "极易发"
 *                      }
 *                  ],
 *
 *                  "carWashing": [
 *                      {
 *                          "date": "2024-10-05T00:00+08:00",
 *                          "index": "3",
 *                          "desc": "较不适宜"
 *                      }
 *                  ],
 *
 *                  "ultraviolet": [
 *                      {
 *                          "date": "2024-10-05T00:00+08:00",
 *                          "index": "2",
 *                          "desc": "弱"
 *                      }
 *                  ],
 *
 *                  "dressing": [
 *                      {
 *                          "date": "2024-10-05T00:00+08:00",
 *                          "index": "4",
 *                          "desc": "温暖"
 *                      }
 *                  ]
 *              }
 *          }
 *      }
 */
data class DailyResponse(val status: String, val result: Result) {

    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>, @SerializedName("life_index") val lifeIndex: LifeIndex)
    data class Temperature(val min: Float, val max: Float)
    data class Skycon(val value: String, val date: String)
    data class LifeIndex(val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>, val ultraviolet: List<LifeDescription>, val dressing: List<LifeDescription>)
    // 因为生活指数的几个分类都是同样的字段，所以可以统一，不用每个分别定义
    data class LifeDescription(val desc: String)

}