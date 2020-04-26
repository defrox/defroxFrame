package com.defrox.defroxframe.sliderimage.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.defrox.defroxframe.R
import com.defrox.defroxframe.sliderimage.SliderImageActivity
import com.defrox.defroxframe.sliderimage.utils.CurrentWeatherModel
import com.defrox.defroxframe.sliderimage.utils.DefroxHelper
import com.defrox.defroxframe.sliderimage.utils.WeatherIcon
import com.kwabenaberko.openweathermaplib.constants.Lang
import com.kwabenaberko.openweathermaplib.constants.Units
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather
import kotlinx.android.synthetic.main.fragment_clock_weather.*


class ClockWeatherFragment : Fragment() {

    val TAG = SliderImageActivity::class.java.simpleName

    private lateinit var weather: CurrentWeatherModel
    private lateinit var weatherIcons: List<WeatherIcon>

    private lateinit var mUpdateWeatherHandler: Handler
    private val mUpdateWeatherRunnable = object : Runnable {
        override fun run() {
            updateCurrentWeather()
            mUpdateWeatherHandler.postDelayed(this, 60000)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        weatherIcons = DefroxHelper.getWeatherIcons(context, "weather-icons.json")
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        weather = ViewModelProviders.of(this).get(CurrentWeatherModel::class.java)

        val temperatureObserver = Observer<String> { res ->
            slider_temperature_text.text = res
        }
        val humidityObserver = Observer<String> { res ->
            slider_humidity_text.text = res
        }
        val iconObserver = Observer<String> { res ->
            slider_weather_icon.text = res
        }
        weather.temperature.observe(viewLifecycleOwner, temperatureObserver)
        weather.humidity.observe(viewLifecycleOwner, humidityObserver)
        weather.icon.observe(viewLifecycleOwner, iconObserver)
        mUpdateWeatherHandler = Handler(Looper.getMainLooper())

        return inflater.inflate(R.layout.fragment_clock_weather, container, false)
    }

    override fun onResume() {
        super.onResume()
//        mUpdateWeatherHandler.removeCallbacks(mUpdateWeatherRunnable)
        mUpdateWeatherHandler.post(mUpdateWeatherRunnable)
    }

    override fun onPause() {
        super.onPause()
        mUpdateWeatherHandler.removeCallbacks(mUpdateWeatherRunnable)
    }

    fun updateCurrentWeather() {
        val weatherHelper: OpenWeatherMapHelper = OpenWeatherMapHelper(getString(R.string.openweathermap_api_key))
        weatherHelper.setUnits(Units.METRIC)
        weatherHelper.setLang(Lang.SPANISH)
        val city = "Madrid"
        weatherHelper.getCurrentWeatherByCityName(city, object : CurrentWeatherCallback {
            override fun onSuccess(currentWeather: CurrentWeather) {
                /*Log.v(TAG,
                        """
                                Coordinates: ${currentWeather.coord.lat}, ${currentWeather.coord.lon}
                                Weather Description: ${currentWeather.weather[0].description}
                                Weather Condition: ${currentWeather.weather[0].main}
                                Weather Icon: ${currentWeather.weather[0].icon}
                                Weather Code: ${currentWeather.weather[0].id}
                                Temperature: ${currentWeather.main.temp}
                                Humidity: ${currentWeather.main.humidity}
                                Wind Speed: ${currentWeather.wind.speed}
                                Time: ${currentWeather.dt}
                                Sunrise: ${currentWeather.sys.sunrise}
                                Sunset: ${currentWeather.sys.sunset}
                                City, Country: ${currentWeather.name}, ${currentWeather.sys.country}
                                """.trimIndent()
                )*/
                handleCurrentWeatherResponse(currentWeather)
            }

            override fun onFailure(throwable: Throwable) {
                Log.v(TAG, throwable.message)
            }
        })
    }

    fun handleCurrentWeatherResponse(currentWeather: CurrentWeather) {
        val icon: WeatherIcon? = weatherIcons.find { it.id == currentWeather.weather[0].id.toInt() }
        val nowUtc = System.currentTimeMillis() / 1000
        val isDay = currentWeather.sys.sunrise < nowUtc && nowUtc > currentWeather.sys.sunset
        weather.description.value = currentWeather.weather[0].description
        weather.temperature.value = currentWeather.main.temp.toShort().toString()
        weather.humidity.value = currentWeather.main.humidity.toShort().toString()
        weather.latitude.value = currentWeather.coord.lat.toString()
        weather.longitude.value = currentWeather.coord.lon.toString()
        weather.city.value = currentWeather.name
        weather.country.value = currentWeather.sys.country
        val newIcon = DefroxHelper.getWeatherIconStyle(icon!!, isDay,this@ClockWeatherFragment)
        if (newIcon !== null) {
            weather.icon.value = newIcon
        }
/*        Toast.makeText(context, "Weather API: " + """
                                Coordinates: ${currentWeather.coord.lat}, ${currentWeather.coord.lon}
                                Weather Description: ${currentWeather.weather[0].description}
                                Weather Condition: ${currentWeather.weather[0].main}
                                Weather Icon: ${currentWeather.weather[0].icon}
                                Weather Code: ${currentWeather.weather[0].id}
                                Temperature: ${currentWeather.main.temp}
                                Humidity: ${currentWeather.main.humidity}
                                Wind Speed: ${currentWeather.wind.speed}
                                City, Country: ${currentWeather.name}, ${currentWeather.sys.country}
                                """.trimIndent(), Toast.LENGTH_SHORT).show()*/
    }

    companion object {
        fun newInstance(): ClockWeatherFragment {
            return ClockWeatherFragment()
        }
    }

}
