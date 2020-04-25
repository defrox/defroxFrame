package com.defrox.defroxframe.sliderimage

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.Toast
import com.defrox.defroxframe.sliderimage.adapters.ViewPagerAdapter

import com.flaviofaria.kenburnsview.KenBurnsView
import kotlinx.android.synthetic.main.activity_ken_burns.view.*
import kotlinx.android.synthetic.main.layout_slider_image.view.*

class DefroxSliderView: KenBurnsView
{
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet? = null) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : super(ctx, attrs, defStyle)

    // Array of Image URLs
    private var items: List<String> = listOf()

    // Timer schedule
    private var inTimer = false
    private var timeLong = 2000.toLong()


    /**
     * Add timer task for change the current page
     * @param time
     */
    fun addTimerToSlide(time: Long) {
        this.timeLong = time
        this.inTimer = true
        goNextPage()
    }

    /**
     * Remove timer task
     */
    fun removeTimerSlide() {
        this.inTimer = false
    }

    /**
     * Set context
     */
    fun setContext(ctx: Context) {
        DefroxSliderView(ctx)
    }

    /**
     * Action go to next page of view pager
     */
    private fun goNextPage() {
        Handler().postDelayed({
            if (inTimer) {
                Toast.makeText(this.context, "Changing", Toast.LENGTH_SHORT).show()
                /*if (DefroxSlider.currentItem == (items.size-1)) {
                    DefroxSlider.setCurrentItem(0, true)
                } else {
                    DefroxSlider.setCurrentItem(DefroxSliderContainer.currentItem + 1, true)
                }*/
            }
            goNextPage()
        }, timeLong)
    }

    /**
     * Get items of view pager (Image URLs)
     * @return list of string
     */
    fun getItems(): List<String> {
        return items
    }

    /**
     * Add new items "Image URLs"
     * @param items
     */
    fun setItems(items: List<String>) {
/*        (DefroxSlider.adapter as? ViewPagerAdapter)?.setItemsPager(items)
        DefroxSlider.adapter?.notifyDataSetChanged()
        this@DefroxSliderView.items = items
        indicator.setViewPager(DefroxSlider)*/
    }

}