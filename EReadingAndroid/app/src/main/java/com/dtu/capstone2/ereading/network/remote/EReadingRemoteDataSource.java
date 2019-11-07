package com.dtu.capstone2.ereading.network.remote;

import com.dtu.capstone2.ereading.network.ApiClient;
import com.dtu.capstone2.ereading.network.ApiServer;
import com.dtu.capstone2.ereading.network.request.AccountLoginRequest;
import com.dtu.capstone2.ereading.network.request.AccountRegisterRequest;
import com.dtu.capstone2.ereading.network.request.AddFavoriteRequest;
import com.dtu.capstone2.ereading.network.request.DataFavoriteResponse;
import com.dtu.capstone2.ereading.network.request.DataLoginRequest;
import com.dtu.capstone2.ereading.network.request.DataStringReponse;
import com.dtu.capstone2.ereading.network.request.DetectWordRequest;
import com.dtu.capstone2.ereading.network.request.FavoriteDeletedResponse;
import com.dtu.capstone2.ereading.network.request.ListVocabularyFavoriteRequest;
import com.dtu.capstone2.ereading.network.request.TranslateNewFeedAgainRequest;
import com.dtu.capstone2.ereading.network.request.TranslateNewFeedRequest;
import com.dtu.capstone2.ereading.network.request.Vocabulary;
import com.dtu.capstone2.ereading.network.response.DetailResponse;
import com.dtu.capstone2.ereading.network.response.LevelUserResponse;
import com.dtu.capstone2.ereading.network.response.ListHistoryResponse;
import com.dtu.capstone2.ereading.network.response.ListLevelEnglishResponse;
import com.dtu.capstone2.ereading.network.response.Token;
import com.dtu.capstone2.ereading.ui.model.LevelEnglish;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Function;

public class EReadingRemoteDataSource {
    private ApiServer mApiServer = ApiClient.getInstants().createServer();

    public Single<Token> login(AccountLoginRequest account) {
        return mApiServer.loginForServer(account);
    }

    public Single<Boolean> addFavorite(AddFavoriteRequest paraFavorite) {
        return mApiServer.AddFavoriteServer(paraFavorite);
    }

    public Single<DataStringReponse> GetDataStringReponseRemote(DetectWordRequest detectWordRequest, String nameLevel) {
        return mApiServer.GetDataStringReponse(detectWordRequest, nameLevel);
    }

    public Single<DataLoginRequest> GetDataLoginRequest(AccountLoginRequest accountLoginRequest) {
        return mApiServer.GetDataLoginRequest(accountLoginRequest);
    }

    public Single<AccountRegisterRequest> registerNewAccount(AccountRegisterRequest accountRegisterRequest) {
        return mApiServer.registerAccount(accountRegisterRequest);
    }

    public Single<ListLevelEnglishResponse> getLevelEnglishFromServer() {
        return mApiServer.getListLevelEnglish();
    }

    public Single<LevelEnglish> setLevelEnglishForUser(int levelPosition) {
        return mApiServer.setLevelEnglishForUser(levelPosition).map(new Function<LevelUserResponse, LevelEnglish>() {
            @Override
            public LevelEnglish apply(LevelUserResponse levelUserResponse) {
                return levelUserResponse.getLevel();
            }
        });
    }

    public Single<DataFavoriteResponse> getDataFavorite(int page) {
        return mApiServer.getDataFavorite(page);
    }

    public Single<DetailResponse> setListVocabularyFavorite(List<Vocabulary> vocabularyList) {
        return mApiServer.setListVocabularyFavorite(new ListVocabularyFavoriteRequest(vocabularyList));
    }

    public Single<DataStringReponse> translateNewFeed(TranslateNewFeedRequest translateNewFeedRequest) {
        return mApiServer.translateNewFeed(translateNewFeedRequest);
    }

    public Single<DataStringReponse> translateNewFeedAgain(TranslateNewFeedAgainRequest translateNewFeedAgainRequest) {
        return mApiServer.translateNewFeedAgain(translateNewFeedAgainRequest);
    }

    public Single<ListHistoryResponse> getListHistory(int page) {
        return mApiServer.getListHistory(page);
    }

    public Single<FavoriteDeletedResponse> deleteFavorite(int id) {
        return mApiServer.deleteFavorite(id);
    }
}
