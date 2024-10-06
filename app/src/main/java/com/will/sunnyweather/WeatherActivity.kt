package com.will.sunnyweather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.will.sunnyweather.databinding.ActivityWeatherBinding
import com.will.sunnyweather.logic.model.Weather
import com.will.sunnyweather.logic.model.getSky
import com.will.sunnyweather.ui.place.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity: AppCompatActivity() {

    companion object {
        const val LNG = "lng"
        const val LAT = "lag"
        const val PLACE_NAME = "placeName"
        fun startWeatherActivity(context: Context, lat: String, lng: String, placeName: String) {
            val intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra(LNG, lng)
            intent.putExtra(LAT, lat)
            intent.putExtra(PLACE_NAME, placeName)
            context.startActivity(intent)
        }
    }

    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    private lateinit var viewBinding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityWeatherBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra(LAT) ?: ""
        }
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra(LNG) ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra(PLACE_NAME) ?: ""
        }

        viewModel.weatherLiveData.observe(this, Observer {result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeather(weather)
            } else{
                Toast.makeText(this, "无法获取天气", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        viewModel.searchWeather(viewModel.locationLat, viewModel.locationLng)
    }

    private fun showWeather(weather: Weather) {
        viewBinding.layoutNow.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充 now 布局中的数据
        val currentTempTxt = "${realtime.temperature.toInt()} ℃"
        viewBinding.layoutNow.currentTemp.text = currentTempTxt

        viewBinding.layoutNow.currentSky.text = getSky(realtime.skycon).info

        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        viewBinding.layoutNow.currentAQI.text = currentPM25Text
        viewBinding.layoutNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        // 填充 forecast 布局中的数据
        viewBinding.layoutForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]

            val view = LayoutInflater.from(this)
                .inflate(R.layout.forecast_item, viewBinding.layoutForecast.forecastLayout, false)

            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault())
            val data = isoFormat.parse(skycon.date)

            dateInfo.text = data?.let { simpleDateFormat.format(it) }
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            viewBinding.layoutForecast.forecastLayout.addView(view)
        }

        // 填充 生活 指数 布局中的数据
        val lifeIndex = daily.lifeIndex
        viewBinding.layoutLifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        viewBinding.layoutLifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        viewBinding.layoutLifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        viewBinding.layoutLifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc

        viewBinding.weatherLayout.visibility = View.VISIBLE
    }
}