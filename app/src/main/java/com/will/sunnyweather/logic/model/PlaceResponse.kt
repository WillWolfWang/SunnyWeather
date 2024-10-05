package com.will.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 *{
 * "status": "ok",
 * "query": "北京",
 * "places": [
                {
                  "formatted_address": "中国 北京市 北京",
                  "id": "CY_CN_d9b61a",
                  "place_id": "CY_CN_d9b61a",
                  "name": "中国 北京市 北京",
                  "location": {
                      "lat": 39.904989,
                      "lng": 116.405285
                   }
                }
            ]
   }
 */
// 搜索城市数据接口返回的 JSON 数值
data class PlaceResponse(val status: String, val places: List<Place>) {
}

// @SerializedName 指定 Json 中要序列化的字段
data class Place(var name: String, val  location: Location, @SerializedName("formatted_address") val address: String) {

}

data class Location(val lat: String, val lng: String)