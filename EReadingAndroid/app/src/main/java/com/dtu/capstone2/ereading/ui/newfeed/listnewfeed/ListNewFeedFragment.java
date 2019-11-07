package com.dtu.capstone2.ereading.ui.newfeed.listnewfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.ui.newfeed.PageNewFeedFragment;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;

public class ListNewFeedFragment extends BaseFragment {
    private ListNewFeedPagerAdapter mAdapter;
    private ListNewFeedViewModel mViewModel;
    private ImageView mImgActionBarBack;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView mTitle;

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ListNewFeedViewModel();
        if (getArguments() != null) {
            mViewModel.setPositionGroup(getArguments().getInt(PageNewFeedFragment.KEY_POSITION_GROUP_NEW_FEED));
        }
        mAdapter = new ListNewFeedPagerAdapter(getActivity().getSupportFragmentManager(), mViewModel.getGroupNewFeed().get(mViewModel.getPositionGroup()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_list_new_feed, container, false);

        mImgActionBarBack = view.findViewById(R.id.imgListNewFeedBack);
        mTabLayout = view.findViewById(R.id.tabLayoutListNewFeed);
        mViewPager = view.findViewById(R.id.viewPagerListNewFeed);
        mTitle = view.findViewById(R.id.tvListNewFeedTitle);

        if (!mViewModel.getGroupNewFeed().get(mViewModel.getPositionGroup()).isEmpty()) {
            mTitle.setText(mViewModel.getGroupNewFeed().get(mViewModel.getPositionGroup()).get(0).getTitleFragment());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventsView();
    }

    private void initEventsView() {
        mImgActionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(mViewPager, true);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTitle.setText(mViewModel.getGroupNewFeed().get(mViewModel.getPositionGroup()).get(position).getTitleFragment());
            }
        });
    }
}
