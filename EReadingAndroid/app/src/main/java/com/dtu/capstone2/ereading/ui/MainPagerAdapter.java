package com.dtu.capstone2.ereading.ui;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dtu.capstone2.ereading.ui.model.MainPage;

import java.util.List;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private List<MainPage> mFragmentList;

    MainPagerAdapter(FragmentManager fm, List<MainPage> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentList.get(position).getTitleFragment();
    }
}
