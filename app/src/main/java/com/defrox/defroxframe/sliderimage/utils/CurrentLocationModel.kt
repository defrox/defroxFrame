package com.defrox.defroxframe.sliderimage.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*class CurrentLocationModel : ViewModel() {
    val latitude: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val longitude: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
}*/

class CurrentLocationModel (var latitude: Double, var longitude: Double)