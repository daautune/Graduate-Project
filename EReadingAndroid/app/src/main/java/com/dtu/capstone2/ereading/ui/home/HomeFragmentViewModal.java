package com.dtu.capstone2.ereading.ui.home;

import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;
import com.dtu.capstone2.ereading.network.request.DataStringReponse;

import io.reactivex.Single;

class HomeFragmentViewModal {
    private EReadingRepository mReadingRepository;
    private LocalRepository mLocalRepository;

    HomeFragmentViewModal(EReadingRepository eReadingRepository, LocalRepository localRepository) {
        mReadingRepository = eReadingRepository;
        mLocalRepository = localRepository;
    }

    Single<DataStringReponse> getDataStringReponse(String para) {
        return mReadingRepository.GetDataStringReponse(para, mLocalRepository.getNameLevelUser());
    }
}
