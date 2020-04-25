package com.defrox.defroxframe.sliderimage.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.defrox.defroxframe.R
import com.defrox.defroxframe.sliderimage.SliderImageActivity
import com.defrox.defroxframe.sliderimage.utils.CurrentWeatherModel
import com.kwabenaberko.openweathermaplib.constants.Lang
import com.kwabenaberko.openweathermaplib.constants.Units
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather
import kotlinx.android.synthetic.main.fragment_clock_weather.*


class ClockWeatherFragment : Fragment() {

    val TAG = SliderImageActivity::class.java.simpleName

    private lateinit var weather: CurrentWeatherModel

//    private var weather: FragmentClockWeatherBindingImpl? = null
    private val mUpdateWeatherRunnable = Runnable { updateCurrentWeather() }
    private val mHandler = Handler()

    override fun onAttach(context: Context) {
        super.onAttach(context)

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


/*//        val view: View = inflater.inflate(R.layout.fragment_clock_weather, container, false)
        val fragmentClockWeatherBinding = FragmentClockWeatherBinding.inflate(inflater, container, false)
//        val weather = arguments!!.getSerializable(CURRENT_WEATHER) as CurrentWeatherModel
//        weather = FragmentClockWeatherBindingImpl(null, root)
        fragmentClockWeatherBinding.currentWeather = weather
        return fragmentClockWeatherBinding.root*/

        return inflater.inflate(R.layout.fragment_clock_weather, container, false)
    }

    override fun onResume() {
        super.onResume()
//        updateCurrentWeather()
        mHandler.removeCallbacks(mUpdateWeatherRunnable)
        mHandler.postDelayed(mUpdateWeatherRunnable, 2000)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mUpdateWeatherRunnable)
    }

    fun updateCurrentWeather() {
        val weatherHelper: OpenWeatherMapHelper = OpenWeatherMapHelper(getString(R.string.openweathermap_api_key))
        weatherHelper.setUnits(Units.METRIC)
        weatherHelper.setLang(Lang.SPANISH)
        val city = "Madrid"
        weatherHelper.getCurrentWeatherByCityName(city, object : CurrentWeatherCallback {
            override fun onSuccess(currentWeather: CurrentWeather) {
                Log.v(TAG,
                        """
                                Coordinates: ${currentWeather.coord.lat}, ${currentWeather.coord.lon}
                                Weather Description: ${currentWeather.weather[0].description}
                                Weather Icon: ${currentWeather.weather[0].icon}
                                Temperature: ${currentWeather.main.temp}
                                Humidity: ${currentWeather.main.humidity}
                                Wind Speed: ${currentWeather.wind.speed}
                                City, Country: ${currentWeather.name}, ${currentWeather.sys.country}
                                """.trimIndent()
                )
                handleCurrentWeatherResponse(currentWeather)
            }

            override fun onFailure(throwable: Throwable) {
                Log.v(TAG, throwable.message)
            }
        })
    }

    fun handleCurrentWeatherResponse(currentWeather: CurrentWeather) {
        weather.description.setValue(currentWeather.weather[0].description)
        weather.temperature.setValue(currentWeather.main.temp.toShort().toString())
        weather.humidity.setValue(currentWeather.main.humidity.toShort().toString())
        weather.latitude.setValue(currentWeather.coord.lat.toString())
        weather.longitude.setValue(currentWeather.coord.lon.toString())
        weather.city.setValue(currentWeather.name)
        weather.country.setValue(currentWeather.sys.country)

//        weather = CurrentWeatherModel(currentWeather.coord.lat.toString(), currentWeather.coord.lon.toString(), currentWeather.name, currentWeather.sys.country)
    }

    //2
    companion object {

//        private const val CURRENT_WEATHER = "weather"

        fun newInstance(): ClockWeatherFragment {
            /*val args = Bundle()
            args.putSerializable(CURRENT_WEATHER, currentWeather)
            val fragment = ClockWeatherFragment()
            fragment.arguments = args
            return fragment*/

            return ClockWeatherFragment()
        }
    }

}

/*
class ClockWeatherFragmentBindingImpl: BaseObservable() {
    private var currentWeather: CurrentWeatherModel = CurrentWeatherModel("llueve","12", "60", "", "", "Madrid", "")

    public fun setCurrentWeather(currentWeather: CurrentWeatherModel) {
        this.currentWeather = currentWeather
        notifyPropertyChanged(BR.currentWeather)
    }

}*/
