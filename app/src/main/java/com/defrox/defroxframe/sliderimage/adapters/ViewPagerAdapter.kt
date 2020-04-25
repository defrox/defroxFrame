package com.defrox.defroxframe.sliderimage.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.defrox.defroxframe.attachmentviewer.loader.PicassoImageLoader
import com.defrox.defroxframe.sliderimage.activities.FullScreenImageActivity
import com.defrox.defroxframe.sliderimage.utils.ImageDownloader
import com.defrox.defroxframe.sliderimage.zoomable.DoubleTapGestureListener
import com.defrox.defroxframe.sliderimage.zoomable.ZoomableDraweeView
import com.facebook.drawee.backends.pipeline.Fresco
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.Transition
import com.squareup.picasso.Picasso


/**
 * Created by Ivan on 16/08/18.
 */
class ViewPagerAdapter(val context: Context, var items: List<String>): PagerAdapter() {

    private val imageDownloader = ImageDownloader()

    init {
        imageDownloader.setMode(ImageDownloader.Mode.CORRECT)
        // Fresco.initialize(context.applicationContext)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        return if (context is FullScreenImageActivity) {
            val item = ZoomableDraweeView(context)
            item.setAllowTouchInterceptionWhileZoomed(true)
            item.setIsLongpressEnabled(false)
            item.setTapListener(DoubleTapGestureListener(item))
            item.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            item.controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(items[position]))
                    .build()
            container.addView(item)
            item
        } else {
            // val item = SliderImageView(context)
            val item = KenBurnsView(context)
            // val item = SimpleDraweeView(context)
            item.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            //val imageUri = Uri.parse(items[position])
            loadKenBurnsView(position, item, context)
            //item.setImageDrawable(imageUri)
            // item.setImageURI(items[position])
            item.scaleType = ImageView.ScaleType.CENTER

            item.setOnClickListener {
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("items", items.toTypedArray())
                intent.putExtra("position", position)
                context.startActivity(intent)
            }

            item.setTransitionListener(object : KenBurnsView.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    Toast.makeText(context, "Started: " + getItem(position), Toast.LENGTH_SHORT).show()
                }

                override fun onTransitionEnd(transition: Transition) {
                    Toast.makeText(context, "Finished", Toast.LENGTH_SHORT).show()
                }
            })


            container.addView(item)
            item
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return items.indexOf(`object`)
    }

    /**
     * Destroy unused element from view pager
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as View))
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 === p1 as View
    }

    override fun getCount(): Int {
        return items.size
    }

    /**
     * Set new items for view pager
     * @param items
     */
    fun setItemsPager(items: List<String>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    fun getItem(position: Int): String {
        return items[position]
    }

    fun getItemId(position: Int): Int {
        return items[position].hashCode()
    }

    fun loadKenBurnsView(position: Int, view: View?, context: Context): View {
        var newView: View
        if (view == null) {
            newView = ImageView(context)
        } else{
            newView = view
        }
        val item = getItem(position)
        if (URLUtil.isValidUrl(item)) {
            Picasso.get().load(item).into(newView as KenBurnsView)
            //imageDownloader.download(items[position], newView as ImageView)
        } else {
            Picasso.get().load(item).into(newView as KenBurnsView)
            //newView = view as KenBurnsView
        }
        return newView
    }

    fun getView(position: Int, view: View?, context: Context): View {
        var newView: View
        if (view == null) {
            newView = ImageView(context)
//            newView.setPadding(6, 6, 6, 6)
        } else{
            newView = view
        }

        imageDownloader.download(items[position], newView as ImageView)

        return newView
    }

    fun getImageDownloader(): ImageDownloader? {
        return imageDownloader
    }
}
