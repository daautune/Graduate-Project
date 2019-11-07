package com.dtu.capstone2.ereading.ui;


import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.network.request.AddFavoriteRequest;
import com.dtu.capstone2.ereading.network.request.DataStringReponse;

import io.reactivex.Single;

public class MainViewModel {

    public static class MainActivityViewModel {
        EReadingRepository Repository = new EReadingRepository();
        String dataReponse;

        /*
         * Đây là funtion send request add favorite*/
        public void addFavoriteMD(int idUser, int idVocabulary) {
            Repository.addFavorite(new AddFavoriteRequest(idUser, idVocabulary));

        }

        /*
         * đây là function nhận reponse sau khi request translate*/
        public Single<DataStringReponse> GetDataStringReponse(String paraString) {
            return Repository.GetDataStringReponse(paraString, "");

        }
    }
}
