package com.defrox.defroxframe.sliderimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.flaviofaria.kenburnsview.Transition
import com.defrox.defroxframe.R
import com.defrox.defroxframe.sliderimage.logic.SliderImage
import kotlinx.android.synthetic.main.activity_ken_burns.*
import kotlinx.android.synthetic.main.activity_ken_burns.view.*


class KenBurnsActivity : AppCompatActivity() {
    private var DefroxSlider: DefroxSliderView? = null
    private var moving = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ken_burns)
        DefroxSlider = findViewById(R.id.DefroxSlider)
        val adi = AccelerateDecelerateInterpolator()
        val generator = RandomTransitionGenerator(2000, adi)

        // Create slider image component
//        DefroxSlider?.context = this
//
        // add images URLs
        val images = listOf(
                "https://i2.wp.com/background4free.com/download/blue_yellow_light_115379772.jpg?w=1200&q=90",
                "https://i2.wp.com/background4free.com/download/blue_light_electric_blue_1209549705.jpg?w=1200&q=90",
                "https://i2.wp.com/background4free.com/download/violet_purple_light_1357299179.jpg?w=1200&q=90")

        // Add image URLs to sliderImage
        DefroxSlider?.setItems(images)

        // Add timer to sliderImage
        DefroxSlider?.addTimerToSlide(2000)

        DefroxSlider?.setTransitionGenerator(generator)
        DefroxSlider?.setOnClickListener(View.OnClickListener {
            moving = if (moving) {
                DefroxSlider?.pause()
                false
            } else {
                DefroxSlider?.resume()
                true
            }
        })
        DefroxSlider?.setTransitionListener(object : KenBurnsView.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                Toast.makeText(this@KenBurnsActivity, "Started", Toast.LENGTH_SHORT).show()
            }

            override fun onTransitionEnd(transition: Transition) {
                Toast.makeText(this@KenBurnsActivity, "Finished", Toast.LENGTH_SHORT).show()
            }
        })
    }
}