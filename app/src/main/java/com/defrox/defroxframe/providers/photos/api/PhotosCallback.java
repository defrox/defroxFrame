package com.defrox.defroxframe.providers.photos.api;

import com.defrox.defroxframe.providers.photos.PhotoItem;

import java.util.ArrayList;

public interface PhotosCallback {

    void completed(ArrayList<PhotoItem> photos, boolean canLoadMore);
    void failed();
}
