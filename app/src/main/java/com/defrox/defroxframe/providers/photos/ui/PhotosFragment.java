package com.defrox.defroxframe.providers.photos.ui;

import android.Manifest;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.defrox.defroxframe.MainActivity;
import com.defrox.defroxframe.R;
import com.defrox.defroxframe.inherit.ConfigurationChangeFragment;
import com.defrox.defroxframe.inherit.PermissionsFragment;
import com.defrox.defroxframe.providers.Provider;
import com.defrox.defroxframe.providers.photos.PhotoAdapter;
import com.defrox.defroxframe.providers.photos.PhotoItem;
import com.defrox.defroxframe.providers.photos.api.FlickrClient;
import com.defrox.defroxframe.providers.photos.api.PhotoProvider;
import com.defrox.defroxframe.providers.photos.api.PhotosCallback;
import com.defrox.defroxframe.providers.photos.api.TumblrClient;
import com.defrox.defroxframe.providers.photos.api.WordpressClient;
import com.defrox.defroxframe.util.Helper;
import com.defrox.defroxframe.util.InfiniteRecyclerViewAdapter;
import com.defrox.defroxframe.util.ThemeUtils;
import com.defrox.defroxframe.util.layout.StaggeredGridSpacingItemDecoration;

import java.util.ArrayList;

/**
 * This activity is used to display a list of tumblr imagess
 */

public class PhotosFragment extends Fragment implements PermissionsFragment, InfiniteRecyclerViewAdapter.LoadMoreListener, PhotosCallback, ConfigurationChangeFragment {

    private RecyclerView listView;
    ArrayList<PhotoItem> photoItems;
    private PhotoAdapter photoAdapter = null;
    private ViewTreeObserver.OnGlobalLayoutListener recyclerListener;

    private Activity mAct;
    private RelativeLayout ll;

    private PhotoProvider provider;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ll = (RelativeLayout) inflater.inflate(R.layout.fragment_list, container, false);
        return ll;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        listView = ll.findViewById(R.id.list);
        photoItems = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), photoItems, this);
        photoAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_PROGRESS);
        listView.setAdapter(photoAdapter);

        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new StaggeredGridSpacingItemDecoration((int) getResources().getDimension(R.dimen.woocommerce_padding), true));

        recyclerListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Verify that the fragment is still attached
                if (getActivity() == null || !isAdded()) return;

                //Get the view width, and check if it could be valid
                int viewWidth = listView.getMeasuredWidth();
                if (viewWidth <= 0 ) return;

                //Remove the VTO
                listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //Calculate and update the span
                float cardViewWidth = getResources().getDimension(R.dimen.card_width_image);
                int newSpanCount = Math.max(1, (int) Math.floor(viewWidth / cardViewWidth));
                RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(newSpanCount, StaggeredGridLayoutManager.VERTICAL);
                listView.setLayoutManager(layoutManager);
                layoutManager.requestLayout();
            }
        };
        listView.getViewTreeObserver().addOnGlobalLayoutListener(recyclerListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAct = getActivity();

        String[] parameters = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA);
        String providerString = this.getArguments().getString(MainActivity.FRAGMENT_PROVIDER);
        if (providerString.equals(Provider.FLICKR)) {
            provider = new FlickrClient(parameters, mAct, this);
        } else if (providerString.equals(Provider.TUMBLR)) {
            provider = new TumblrClient(parameters, mAct, this);
        } else {
            provider = new WordpressClient(parameters, mAct, this);
        }

        refreshItems();
    }

    public void updateList(ArrayList<PhotoItem> result) {
        if (result.size() > 0)
            photoItems.addAll(result);

        photoAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_LIST);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        listView.getViewTreeObserver().addOnGlobalLayoutListener(recyclerListener);
    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    @Override
    public void onMoreRequested() {
        provider.requestPhotos(page++);
    }

    void refreshItems() {
        page= 1;
        photoItems.clear();
        photoAdapter.setHasMore(true);
        photoAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_PROGRESS);
        provider.requestPhotos(page++);
    }

    @Override
    public void completed(ArrayList<PhotoItem> photos, boolean canLoadMore) {
        updateList(photos);
        photoAdapter.setHasMore(canLoadMore);
    }

    @Override
    public void failed() {
        Helper.noConnection(mAct);
        photoAdapter.setHasMore(false);
        photoAdapter.setModeAndNotify(InfiniteRecyclerViewAdapter.MODE_EMPTY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
        ThemeUtils.tintAllIcons(menu, mAct);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshItems();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}