package com.dtu.capstone2.ereading.ui.account.login;

import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;
import com.dtu.capstone2.ereading.network.request.AccountLoginRequest;
import com.dtu.capstone2.ereading.network.request.DataLoginRequest;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

class LoginViewModel {
    private EReadingRepository mEReadingRepository;
    private LocalRepository mLocalRepository;

    LoginViewModel(EReadingRepository eReadingRepository, LocalRepository localRepository) {
        mEReadingRepository = eReadingRepository;
        mLocalRepository = localRepository;
    }

    Single<DataLoginRequest> GetDataLoginRequest(AccountLoginRequest accountLoginRequest) {
        return mEReadingRepository.GetDataLoginRequest(accountLoginRequest).doOnSuccess(new Consumer<DataLoginRequest>() {
            @Override
            public void accept(DataLoginRequest dataLoginRequest) throws Exception {
                mLocalRepository.saveTokenUser(dataLoginRequest.getStringToken());
                mLocalRepository.saveEmailUser(dataLoginRequest.getStringEmail());
                mLocalRepository.saveNameLevelUser(dataLoginRequest.getLevelNameUser());
            }
        });
    }
}
