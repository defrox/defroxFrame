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
import kotlinx.android.synthetic.main.activity_ken_burns.*


class KenBurnsActivity : AppCompatActivity() {
    private var kbv: KenBurnsView? = null
    private var moving = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ken_burns)
        kbv = findViewById(R.id.kbv)
        val adi = AccelerateDecelerateInterpolator()
        val generator = RandomTransitionGenerator(2000, adi)
        kbv?.setTransitionGenerator(generator)
        kbv?.setOnClickListener(View.OnClickListener {
            moving = if (moving) {
                kbv?.pause()
                false
            } else {
                kbv?.resume()
                true
            }
        })
        kbv?.setTransitionListener(object : KenBurnsView.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                Toast.makeText(this@KenBurnsActivity, "Started", Toast.LENGTH_SHORT).show()
            }

            override fun onTransitionEnd(transition: Transition) {
                Toast.makeText(this@KenBurnsActivity, "Finished", Toast.LENGTH_SHORT).show()
            }
        })
    }
}