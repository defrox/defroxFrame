package com.defrox.defroxframe.providers.videos.api;

import com.defrox.defroxframe.providers.videos.api.object.Video;

import java.util.ArrayList;

public interface VideosCallback {

    void completed(ArrayList<Video> videos, boolean canLoadMore, String nextPageToken);
    void failed();
}
