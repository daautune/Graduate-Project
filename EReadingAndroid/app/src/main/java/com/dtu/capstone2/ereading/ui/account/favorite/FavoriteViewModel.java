package com.dtu.capstone2.ereading.ui.account.favorite;

import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.network.request.DataFavoriteResponse;
import com.dtu.capstone2.ereading.network.request.Favorite;
import com.dtu.capstone2.ereading.network.request.FavoriteDeletedResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

class FavoriteViewModel {
    private EReadingRepository eReadingRepository = new EReadingRepository();
    private List<Favorite> listFavorite = new ArrayList<>();
    private boolean isLoadingMore = false;
    private int pageData = 1;
    private boolean isCanLoadMore = false;

    Single<DataFavoriteResponse> getDataFavoriteFromServerFirstLoad() {
        pageData = 1;
        return eReadingRepository.getDataFavorite(pageData)
                .doOnSuccess(new Consumer<DataFavoriteResponse>() {
                    @Override
                    public void accept(DataFavoriteResponse dataFavoriteReponse) throws Exception {
                        listFavorite.clear();
                        listFavorite.addAll(dataFavoriteReponse.getListData());
                        pageData += 1;
                        isCanLoadMore = dataFavoriteReponse.getNextPageFlg();
                    }
                });
    }

    Single<FavoriteDeletedResponse> deleteFavorite(int position) {
        return eReadingRepository.deleteFavorite(listFavorite.get(position).getIntId());
    }

    List<Favorite> getListFavorite() {
        return listFavorite;
    }

    boolean getLoadingMore() {
        return isLoadingMore;
    }

    void setLoadingMore(Boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    int getSizeListFavorite() {
        return listFavorite.size();
    }

    Single<DataFavoriteResponse> loadMore(int page) {
        return eReadingRepository.getDataFavorite(page).doOnSuccess(new Consumer<DataFavoriteResponse>() {
            @Override
            public void accept(DataFavoriteResponse dataFavoriteResponse) throws Exception {
                pageData += 1;
                isCanLoadMore = dataFavoriteResponse.getNextPageFlg();
            }
        });
    }

    int getPageData() {
        return pageData;
    }

    int getPositionItemLater() {
        return listFavorite.size() - 1;
    }

    boolean getIsCanLoadMore() {
        return isCanLoadMore;
    }
}
