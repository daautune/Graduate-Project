package com.dtu.capstone2.ereading.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.ui.account.PageAccountFragment;
import com.dtu.capstone2.ereading.ui.home.PageHomeFragment;
import com.dtu.capstone2.ereading.ui.model.MainPage;
import com.dtu.capstone2.ereading.ui.newfeed.PageNewFeedFragment;
import com.dtu.capstone2.ereading.ui.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ViewPager mViewPagerMain;
    private TabLayout mTabLayout;
    private MainPagerAdapter mMainPagerAdapter;
    private List<MainPage> mListFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mListFragment);
        mViewPagerMain.setAdapter(mMainPagerAdapter);
        mViewPagerMain.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPagerMain);
    }

    private void initView() {
        mViewPagerMain = findViewById(R.id.viewPagerMain);
        mTabLayout = findViewById(R.id.tabLayoutMain);
    }

    private void initData() {
        mListFragment = new ArrayList<>();
        mListFragment.add(new MainPage(new PageNewFeedFragment(), "Tin tức"));
        mListFragment.add(new MainPage(new PageHomeFragment(), "Dịch"));
        mListFragment.add(new MainPage(new PageAccountFragment(), "Tài khoản"));
    }
}