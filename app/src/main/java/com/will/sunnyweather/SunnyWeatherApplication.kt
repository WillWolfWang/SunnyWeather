package com.will.sunnyweather

import android.app.Application
import android.content.Context

// 全局 application
class SunnyWeatherApplication: Application() {
    // 静态获取应用 context 方式
    companion object{
        // 彩云 api 需要的 token
        const val TOKEN = "EeM8Hh2LYbZswoxN"

        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}