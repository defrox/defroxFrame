package com.defrox.defroxframe.sliderimage.utils;

import android.app.Activity;

import java.io.Serializable;

public interface ClockWeatherListener <T extends Activity> extends Serializable {
    public void retrieveCurrentLocation(T activity);
}
