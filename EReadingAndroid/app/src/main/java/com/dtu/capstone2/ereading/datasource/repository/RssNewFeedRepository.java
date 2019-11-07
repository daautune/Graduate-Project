package com.dtu.capstone2.ereading.datasource.repository;

import com.dtu.capstone2.ereading.network.remote.RssNewFeedRemoteDataSource;
import com.dtu.capstone2.ereading.network.response.BBCRssResponse;
import com.dtu.capstone2.ereading.network.response.RssResponse;

import io.reactivex.Single;

public class RssNewFeedRepository {
    private RssNewFeedRemoteDataSource newFeedRemoteDataSource = new RssNewFeedRemoteDataSource();

    public Single<RssResponse> getNewFeedFromServerCNN() {
        return newFeedRemoteDataSource.getNewFeedFromServerCNN();
    }

    public Single<BBCRssResponse> getNewsFeedFromServerBBC(String urlEndpoint) {
        return newFeedRemoteDataSource.getNewsFeedFromServerBBC(urlEndpoint);
    }
}
