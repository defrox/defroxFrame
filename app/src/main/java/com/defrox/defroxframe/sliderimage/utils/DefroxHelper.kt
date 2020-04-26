package com.defrox.defroxframe.sliderimage.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.provider.Settings.Global.getString
import androidx.fragment.app.Fragment
import com.defrox.defroxframe.R
import com.defrox.defroxframe.util.Helper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.json.JSONObject
import java.sql.Time


//import javax.script.*

public object DefroxHelper {

    private val json = Json(JsonConfiguration.Stable)

    public fun readFileUsingGetResource(fileName: String) = this::class.java.getResource(fileName)!!.readText(Charsets.UTF_8)

    public fun getWeatherIcons(context: Context, fileName: String): List<WeatherIcon> {
        val reader: JSONObject = Helper.getJSONObjectFromAsset(context, fileName)
        val iteratorObj = reader.keys()
        val allIcons: MutableList<WeatherIcon> = emptyList<WeatherIcon>().toMutableList()
        while (iteratorObj.hasNext()) {
            val code = iteratorObj.next()
            val iconObj = reader.getJSONObject(code)
            val icon = iconObj["icon"].toString()
            val label = iconObj["label"].toString()
            allIcons.add(WeatherIcon(code.toInt(), label, icon))
        }
        return allIcons
    }

    public fun getWeatherIconStyle(icon: WeatherIcon, isDay: Boolean, fragment: Fragment): String? {
        var result: String? = null
        try {
            result = fragment.resources.getString(fragment.resources.getIdentifier("wi_" + (if (isDay) "day" else "night") + "_" + icon.icon, "string", fragment.activity!!.packageName))
        } catch (e: Resources.NotFoundException) {
            println("Weather icon resource not found")
        }
        return result
    }

    public fun isDayOrNight(currentTime: String, sunrise: String, sunset: String) {

    }

    fun getAndroidDrawable(pDrawableName: String?): Drawable? {
        val resourceId: Int = Resources.getSystem().getIdentifier(pDrawableName, "drawable", "android")
        return if (resourceId == 0) {
            null
        } else {
            Resources.getSystem().getDrawable(resourceId)
        }
    }

    fun getAndroidString(pStringName: String?): String? {
        val resourceId: Int = Resources.getSystem().getIdentifier(pStringName, "tools", "android")
        return if (resourceId == 0) {
            null
        } else {
            Resources.getSystem().getString(resourceId)
        }
    }
}