package com.defrox.defroxframe.sliderimage.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrentWeatherModel : ViewModel() {
    val description: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val temperature: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val humidity: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val latitude: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val longitude: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val city: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val country: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val icon: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val code: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val main: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val units: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}