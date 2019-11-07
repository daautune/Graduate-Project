package com.dtu.capstone2.ereading.ui.account;

import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;
import com.dtu.capstone2.ereading.network.response.ListLevelEnglishResponse;
import com.dtu.capstone2.ereading.ui.model.LevelEnglish;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

class PageAccountViewModel {
    private EReadingRepository mEReadingRepository;
    private LocalRepository mLocalRepository;
    private List<String> nameLevels = new ArrayList<>();
    private int levelSelected = -1;

    PageAccountViewModel(EReadingRepository eReadingRepository, LocalRepository localRepository) {
        mEReadingRepository = eReadingRepository;
        mLocalRepository = localRepository;
    }

    Single<List<String>> getListLevelFromServer() {
        return mEReadingRepository.getLevelEnglishFromServer()
                .doOnSuccess(new Consumer<ListLevelEnglishResponse>() {
                    @Override
                    public void accept(ListLevelEnglishResponse listLevelEnglishResponse) throws Exception {
                        nameLevels.clear();
                        for (LevelEnglish e : listLevelEnglishResponse.getLevels()) {
                            nameLevels.add(e.getName());
                        }
                        levelSelected = listLevelEnglishResponse.getLevelSelected();
                    }
                }).map(new Function<ListLevelEnglishResponse, List<String>>() {
                    @Override
                    public List<String> apply(ListLevelEnglishResponse listLevelEnglishResponse) throws Exception {
                        List<String> lists = new ArrayList<>();
                        for (LevelEnglish e : listLevelEnglishResponse.getLevels()) {
                            lists.add(e.getName());
                        }
                        return lists;
                    }
                });
    }

    String getEmailFromLocal() {
        return mLocalRepository.getEmailUser();
    }

    Single<LevelEnglish> setLevelOfUserToServer(int position) {
        return mEReadingRepository.setLevelEnglishForUserToServer(position).doOnSuccess(new Consumer<LevelEnglish>() {
            @Override
            public void accept(LevelEnglish levelEnglish) throws Exception {
                mLocalRepository.saveNameLevelUser(levelEnglish.getName());
            }
        });
    }

    Boolean isLogin() {
        return mLocalRepository.isLogin();
    }

    int getLevelSelected() {
        return levelSelected;
    }

    void logOut() {
        mLocalRepository.clearTokenUser();
        mLocalRepository.clearEmailUser();
        mLocalRepository.clearNameLevelUser();
    }
}
