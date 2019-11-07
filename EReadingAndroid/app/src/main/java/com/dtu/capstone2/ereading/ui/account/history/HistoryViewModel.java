package com.dtu.capstone2.ereading.ui.account.history;

import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.network.response.HistoryNewFeed;
import com.dtu.capstone2.ereading.network.response.ListHistoryResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

class HistoryViewModel {
    private EReadingRepository eReadingRepository = new EReadingRepository();
    private List<HistoryNewFeed> mListHistory = new ArrayList<>();
    private boolean isLoadingMore = false;
    private int pageData = 1;
    private boolean isCanLoadMore = false;

    Single<ListHistoryResponse> getListHistoryFromServerFirstLoad() {
        pageData = 1;
        return eReadingRepository.getListHistory(pageData)
                .doOnSuccess(new Consumer<ListHistoryResponse>() {
                    @Override
                    public void accept(ListHistoryResponse listHistoryResponse) throws Exception {
                        mListHistory.clear();
                        mListHistory.addAll(listHistoryResponse.getListData());
                        pageData += 1;
                        isCanLoadMore = listHistoryResponse.getNextPageFlg();
                    }
                });
    }

    List<HistoryNewFeed> getListHistory() {
        return mListHistory;
    }

    boolean getLoadingMore() {
        return isLoadingMore;
    }

    void setLoadingMore(Boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    int getSizeListFavorite() {
        return mListHistory.size();
    }

    Single<ListHistoryResponse> loadMore(int page) {
        return eReadingRepository.getListHistory(page)
                .doOnSuccess(new Consumer<ListHistoryResponse>() {
                    @Override
                    public void accept(ListHistoryResponse listHistoryResponse) throws Exception {
                        pageData += 1;
                        isCanLoadMore = listHistoryResponse.getNextPageFlg();
                    }
                });
    }

    int getPageData() {
        return pageData;
    }

    int getPositionItemLater() {
        return mListHistory.size() - 1;
    }

    boolean getIsCanLoadMore() {
        return isCanLoadMore;
    }
}
