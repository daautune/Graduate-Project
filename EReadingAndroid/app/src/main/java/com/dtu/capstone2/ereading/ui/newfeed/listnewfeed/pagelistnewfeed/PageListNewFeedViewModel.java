package com.dtu.capstone2.ereading.ui.newfeed.listnewfeed.pagelistnewfeed;

import android.util.Log;

import com.dtu.capstone2.ereading.datasource.repository.RssNewFeedRepository;
import com.dtu.capstone2.ereading.network.response.BBCRssItemResponse;
import com.dtu.capstone2.ereading.network.response.BBCRssResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

class PageListNewFeedViewModel {
    private RssNewFeedRepository mNewFeedRepository = new RssNewFeedRepository();
    private List<BBCRssItemResponse> mRssItemResponses = new ArrayList<>();
    private String mUrlEndPoint;
    private String mTypeNewFeed;

//    Single<RssResponse> getNewFeedOfServerCNN() {
//        return mNewFeedRepository.getNewFeedFromServerCNN().doOnSuccess(new Consumer<RssResponse>() {
//            @Override
//            public void accept(RssResponse rssResponse) throws Exception {
//                mRssItemResponses.clear();
//                mRssItemResponses.addAll(rssResponse.getRssItemResponses());
//            }
//        });
//    }

    List<BBCRssItemResponse> getListRssItemResponse() {
        return mRssItemResponses;
    }

    Single<BBCRssResponse> getNewsFeedFromServerBBCPopularTopStories() {
        return mNewFeedRepository.getNewsFeedFromServerBBC(mUrlEndPoint).doOnSuccess(new Consumer<BBCRssResponse>() {
            @Override
            public void accept(BBCRssResponse rssResponse) throws Exception {
                mRssItemResponses.clear();
                Log.d("BBBB", rssResponse.getBbcRssItemResponses().get(0).getPushDate());
                mRssItemResponses.addAll(rssResponse.getBbcRssItemResponses());
            }
        });
    }

    void setmUrlEndPoint(String mUrlEndPoint) {
        this.mUrlEndPoint = mUrlEndPoint;
    }

    void setTypeNewFeed(String mTypeNewFeed) {
        this.mTypeNewFeed = mTypeNewFeed;
    }

    String getTypeNewFeed() {
        return mTypeNewFeed;
    }
}
