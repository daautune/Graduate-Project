package com.dtu.capstone2.ereading.ui.newfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.ui.model.ItemPageNewFeed;
import com.dtu.capstone2.ereading.ui.newfeed.listnewfeed.ListNewFeedFragment;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class PageNewFeedFragment extends BaseFragment {
    public static final String KEY_POSITION_GROUP_NEW_FEED = "position_group_new_feed";
    private RecyclerView mRecyclerView;
    private PageNewFeedAdapter mAdapter;
    private List<ItemPageNewFeed> mItemPageNewFeeds;

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_page_new_feed, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerViewPageNewFeed);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PageNewFeedAdapter(mItemPageNewFeeds, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initEventsView();
    }

    public void initData() {
        mItemPageNewFeeds = new ArrayList<>();

        mItemPageNewFeeds.add(new ItemPageNewFeed("https://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif", "BCC Popular news"));
        mItemPageNewFeeds.add(new ItemPageNewFeed("https://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif", "BCC Global and UK news"));
        mItemPageNewFeeds.add(new ItemPageNewFeed("https://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif", "BCC Sports news"));
//        mItemPageNewFeeds.add(new ItemPageNewFeed("", "CNN News"));
    }

    public void initEventsView() {
        mAdapter.setmItemPageNewFeeds(new PageNewFeedAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(KEY_POSITION_GROUP_NEW_FEED, position);
                Fragment fragment = new ListNewFeedFragment();
                fragment.setArguments(bundle);
                addFragment(R.id.layoutPageNewFeedContainer, fragment, true, true);
            }
        });
    }
}
