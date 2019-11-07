package com.dtu.capstone2.ereading.ui.newfeed.displayanewfeed;

import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;

class DisplayNewFeedViewModel {
    private String urlNewFeed;
    private String typeNewFeed;
    private LocalRepository mLocalRepository;

    DisplayNewFeedViewModel(LocalRepository localRepository) {
        mLocalRepository = localRepository;
    }

    String getUrlNewFeed() {
        return urlNewFeed;
    }

    void setUrlNewFeed(String urlNewFeed) {
        this.urlNewFeed = urlNewFeed;
    }

    String getTypeNewFeed() {
        return typeNewFeed;
    }

    void setTypeNewFeed(String typeNewFeed) {
        this.typeNewFeed = typeNewFeed;
    }

    Boolean isNotSetLevelWhenLogin() {
        return mLocalRepository.isLogin() && mLocalRepository.getNameLevelUser().isEmpty();
    }
}
