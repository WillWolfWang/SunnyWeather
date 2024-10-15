package com.will.sunnyweather.pagetest

data class WanAndroidModel(val id: Int, val title: String) {
}

data class WanAndroidModels(val curPage: Int, val offset: Int, val total: Int, val size: Int, val datas: List<WanAndroidModel>)

data class WanResult(val errorCode: Int, val errorMsg: String, val data: WanAndroidModels)