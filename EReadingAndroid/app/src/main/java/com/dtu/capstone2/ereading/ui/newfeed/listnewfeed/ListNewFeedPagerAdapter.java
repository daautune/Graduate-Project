package com.dtu.capstone2.ereading.ui.newfeed.listnewfeed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dtu.capstone2.ereading.ui.model.ItemListNewFeedPager;

import java.util.List;

public class ListNewFeedPagerAdapter extends FragmentStatePagerAdapter {
    public static final String KEY_URL_END_POINT = "key_url_end_point";
    public static final String KEY_TYPE_NEW_FEED = "key_type_new_feed";
    private List<ItemListNewFeedPager> mItemListNewFeedPagers;

    ListNewFeedPagerAdapter(FragmentManager fm, List<ItemListNewFeedPager> itemListNewFeedPagers) {
        super(fm);
        mItemListNewFeedPagers = itemListNewFeedPagers;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL_END_POINT, mItemListNewFeedPagers.get(i).getUrlEndPointRSS());
        bundle.putString(KEY_TYPE_NEW_FEED, mItemListNewFeedPagers.get(i).getTitleFragment());

        Fragment fragment = mItemListNewFeedPagers.get(i).getFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mItemListNewFeedPagers.size();
    }
}
