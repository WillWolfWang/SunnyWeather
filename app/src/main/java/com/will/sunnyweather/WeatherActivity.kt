package com.will.sunnyweather

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.will.sunnyweather.databinding.ActivityWeatherBinding
import com.will.sunnyweather.logic.model.Weather
import com.will.sunnyweather.logic.model.getSky
import com.will.sunnyweather.ui.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
            // 废弃版本方法
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = android.graphics.Color.TRANSPARENT

        viewBinding = ActivityWeatherBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)
        // 设置窗口适配

//        val controller = window.insetsController
//        if (controller != null) {
        // 这里是将状态栏和底部菜单栏全部隐藏
////            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        // 行为 是隐藏 SystemBar 后滑动屏幕边缘方向显示 SystemBar
////            controller.systemBarsBehavior =
////                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            controller.hide(WindowInsets.Type.statusBars())
////            window.statusBarColor = android.graphics.Color.TRANSPARENT
//        }

//        hideSystemUI(viewBinding.root);

        // 告诉系统你的应用希望内容绘制到系统窗口（如状态栏和导航栏）后面。
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 设置透明色
        window.statusBarColor = Color.TRANSPARENT
        // 监听获取窗口，可以获取窗口的高度信息
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.layoutNow.titleLayout,  object : OnApplyWindowInsetsListener{
            override fun onApplyWindowInsets(
                v: View,
                insets: WindowInsetsCompat
            ): WindowInsetsCompat {
                // 获取状态栏的高度
                val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                Log.d("WillWolf", "statusInsets area ${statusInsets.top}")
                // 设置 view 的 top margin
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = statusInsets.top
                }

                return insets
            }
        })


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

//            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault())
//            val data = isoFormat.parse(skycon.date)

//            dateInfo.text = data?.let { simpleDateFormat.format(it) }
            dateInfo.text = formatDateWithJavaTime(skycon.date)

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateWithJavaTime(isoDateString: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoDateString)
        val localDate = zonedDateTime.toLocalDate()
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun hideSystemUI(mainContainer: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI(mainContainer: View) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, mainContainer).show(WindowInsetsCompat.Type.systemBars())
    }
}