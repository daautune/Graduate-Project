package com.dtu.capstone2.ereading.ui.newfeed.listnewfeed.pagelistnewfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.network.response.BBCRssResponse;
import com.dtu.capstone2.ereading.ui.newfeed.displayanewfeed.DisplayNewFeedFragment;
import com.dtu.capstone2.ereading.ui.newfeed.listnewfeed.ListNewFeedPagerAdapter;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;
import com.dtu.capstone2.ereading.ui.utils.Constants;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PageListNewFeedFragment extends BaseFragment {
    private PageListNewFeedViewModel mViewModel;
    private PageListNewFeedAdapter mAdapter;
    private RecyclerView mRecyclerViewFeedDisplay;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new PageListNewFeedViewModel();
        if (getArguments() != null) {
            mViewModel.setmUrlEndPoint(getArguments().getString(ListNewFeedPagerAdapter.KEY_URL_END_POINT));
            mViewModel.setTypeNewFeed(getArguments().getString(ListNewFeedPagerAdapter.KEY_TYPE_NEW_FEED));
        }
        mAdapter = new PageListNewFeedAdapter(mViewModel.getListRssItemResponse(), getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_list_new_feed_page, container, false);
        mRecyclerViewFeedDisplay = view.findViewById(R.id.recyclerViewPageNewFeedDisplay);
        mSwipeRefresh = view.findViewById(R.id.layoutSwipeRefreshListNewFeed);
        mSwipeRefresh.setRefreshing(true); // Show tiến trình Load data lần đầu

        mSwipeRefresh.setColorSchemeResources(R.color.colorPink, R.color.colorIndigo, R.color.colorLime);
        initEventsView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewFeedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewFeedDisplay.setAdapter(mAdapter);
        loadDataFromServer();
    }

    private void initEventsView() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });
        mAdapter.setmOnItemListener(new PageListNewFeedAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                String urlNewFeed = mViewModel.getListRssItemResponse().get(position).getLink();
                bundle.putString(Constants.KEY_URL_NEW_FEED, urlNewFeed);
                bundle.putString(Constants.KEY_TYPE_NEW_FEED, mViewModel.getTypeNewFeed());
                Fragment fragment = new DisplayNewFeedFragment();
                fragment.setArguments(bundle);
                addFragment(R.id.layoutPageNewFeedContainer, fragment, true, true);
            }
        });
    }

    private void loadDataFromServer() {
        mViewModel.getNewsFeedFromServerBBCPopularTopStories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<BBCRssResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                getManagerSubscribe().add(d);
            }

            @Override
            public void onSuccess(BBCRssResponse rssResponse) {

                mAdapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                showApiErrorDialog();
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }
}
