package com.defrox.defroxframe.sliderimage.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.defrox.defroxframe.R
import com.defrox.defroxframe.sliderimage.SliderImageActivity
import com.defrox.defroxframe.sliderimage.utils.*
import com.google.android.gms.location.*
import com.kwabenaberko.openweathermaplib.constants.Lang
import com.kwabenaberko.openweathermaplib.constants.Units
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather
import kotlinx.android.synthetic.main.fragment_clock_weather.*


class ClockWeatherFragment : Fragment() {

    val TAG = SliderImageActivity::class.java.simpleName

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    public var currentLocation: CurrentLocationModel = CurrentLocationModel(0.0, 0.0)
    private lateinit var weather: CurrentWeatherModel
    private lateinit var weatherIcons: List<WeatherIcon>
    private lateinit var sharedPreferences: SharedPreferences

    // TODO: show with delay
    // TODO: use settings
    // TODO: location selector or current location
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        retrieveCurrentLocation()
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
        val unitsObserver = Observer<String> { res ->
            if (res == Units.IMPERIAL) {
                slider_temperature_scale.text = getString(R.string.wi_fahrenheit)
            } else {
                slider_temperature_scale.text = getString(R.string.wi_celsius)
            }
        }
        weather.temperature.observe(viewLifecycleOwner, temperatureObserver)
        weather.humidity.observe(viewLifecycleOwner, humidityObserver)
        weather.icon.observe(viewLifecycleOwner, iconObserver)
        weather.units.observe(viewLifecycleOwner, unitsObserver)
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
        if (sharedPreferences.getString("settings_slider_units", null) == Units.IMPERIAL) {
            weatherHelper.setUnits(Units.IMPERIAL)
            weather.units.value = Units.IMPERIAL
        } else {
            weatherHelper.setUnits(Units.METRIC)
            weather.units.value = Units.METRIC
        }
        weatherHelper.setLang(Lang.SPANISH)
        val city = "Madrid"
        retrieveCurrentLocation()
        weatherHelper.getCurrentWeatherByGeoCoordinates(currentLocation.latitude, currentLocation.longitude, object : CurrentWeatherCallback {
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

/*
        weatherHelper.getCurrentWeatherByCityName(city, object : CurrentWeatherCallback {
            override fun onSuccess(currentWeather: CurrentWeather) {
                */
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
                )*//*

                handleCurrentWeatherResponse(currentWeather)
            }

            override fun onFailure(throwable: Throwable) {
                Log.v(TAG, throwable.message)
            }
        })
*/
    }

    fun handleCurrentWeatherResponse(currentWeather: CurrentWeather) {
        val icon: WeatherIcon? = weatherIcons.find { it.id == currentWeather.weather[0].id.toInt() }
        val nowUtc = System.currentTimeMillis() / 1000
        val isDay = currentWeather.sys.sunrise < nowUtc && nowUtc < currentWeather.sys.sunset
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
        Log.v(TAG,
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
                                NowUTC: ${nowUtc}
                                isDay: ${isDay}
                                City, Country: ${currentWeather.name}, ${currentWeather.sys.country}
                                """.trimIndent()
        )
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

    fun fadeIn(view: View, duration: Long = 2000) {
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", .3f, 1f)
        fadeIn.duration = duration
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.v(TAG,
                    """
                                Lat: ${mLastLocation.latitude.toString()}
                                Lon: ${mLastLocation.longitude.toString()}
                                """.trimIndent()
            )
            currentLocation.latitude = mLastLocation.latitude
            currentLocation.longitude = mLastLocation.longitude
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.v(TAG,
                                """
                                Lat: ${location.latitude.toString()}
                                Lon: ${location.longitude.toString()}
                                """.trimIndent()
                        )
                        currentLocation.latitude = location.latitude
                        currentLocation.longitude = location.longitude
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    public fun retrieveCurrentLocation(): CurrentLocationModel {
        getLastLocation()
        return currentLocation
    }

    companion object {
        fun newInstance(): ClockWeatherFragment {
            return ClockWeatherFragment()
        }
    }

}
