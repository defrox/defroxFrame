package com.defrox.defroxframe.sliderimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.defrox.defroxframe.R
import com.defrox.defroxframe.sliderimage.logic.SliderImage
import com.defrox.defroxframe.sliderimage.ui.ClockWeatherFragment
import com.defrox.defroxframe.sliderimage.utils.LocalImages
import kotlinx.android.synthetic.main.activity_slider_image.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SliderImageActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        main_frame.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        clock_weather_fragment.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    private val mShowRunnable = Runnable { show() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_slider_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        dummy_button.setOnTouchListener(mDelayHideTouchListener)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_slider_image)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        // Create slider image component
        val slider = SliderImage(this)

        // add images URLs
        val images = listOf(
                "https://i2.wp.com/background4free.com/download/blue_yellow_light_115379772.jpg?w=1200&q=90",
                "https://i2.wp.com/background4free.com/download/blue_light_electric_blue_1209549705.jpg?w=1200&q=90",
                "https://i2.wp.com/background4free.com/download/violet_purple_light_1357299179.jpg?w=1200&q=90")

        // Add image URLs to sliderImage
        slider.setItems(images)

        // Instantiate local image loader
        val localImages = LocalImages(this)
        val localImagesList = localImages.getAllImagesList()
        slider.setItems(localImagesList)

        // Add timer to sliderImage
        slider.addTimerToSlide(60000)


        // Add slider component to a container
         container_main_images.addView(slider)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.clock_weather_fragment, ClockWeatherFragment.newInstance(), "ClockWeather")
                    .commit()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedShow(500)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        clock_weather_fragment.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        clock_weather_fragment.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    /**
     * Schedules a call to show() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedShow(delayMillis: Int) {
        mHideHandler.removeCallbacks(mShowRunnable)
        mHideHandler.postDelayed(mShowRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
