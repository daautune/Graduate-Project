package com.dtu.capstone2.ereading.ui.newfeed.translate

import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository
import com.dtu.capstone2.ereading.network.request.DataStringReponse
import com.dtu.capstone2.ereading.network.request.Vocabulary
import com.dtu.capstone2.ereading.network.response.DetailResponse
import com.dtu.capstone2.ereading.ui.model.*
import com.dtu.capstone2.ereading.ui.utils.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import org.jsoup.Jsoup

internal class TranslateNewFeedViewModel(private val mReadingRepository: EReadingRepository, private val localRepository: LocalRepository) {
    companion object {
        internal const val NO_ITEM_CHANGE = -1
    }

    internal var urlNewFeed: String? = null
    internal val dataRecyclerView = mutableListOf<LineContentNewFeed>()
    internal var nameListDialogShowing: String = ""
    private val mListVocabularyToTranslateRefresh = mutableListOf<VocabularySelected>()
    private val mListVocabularyToAddFavorite = mutableListOf<VocabularySelected>()
    private val listVocabularyTranslatedResponse = hashMapOf<Int, List<Vocabulary>>()
    private val listVocabularyUntranslatedResponse = hashMapOf<Int, List<Vocabulary>>()
    private val listContentSource = hashMapOf<Int, String>()

    // TRường hợp với báo BBC text ok
    fun getDataFromHTMLAndOnNextDetectWord(): Observable<DataStringReponse> = Observable.create(ObservableOnSubscribe<LineContentNewFeed> { emitter ->
        with(Jsoup.connect(urlNewFeed).get()) {
            emitter.onNext(LineContentNewFeed(TypeContent.TITLE, this.title()))

            with(this.getElementsByTag("p")) contentElem@{
                this@contentElem.forEach { element ->
                    when {
                        element.hasClass(TypeContent.INTRODUCTION.valueType) -> emitter.onNext(LineContentNewFeed(TypeContent.INTRODUCTION, element.text()))
                        element.hasClass(TypeContent.HEADER.valueType) -> emitter.onNext(LineContentNewFeed(TypeContent.HEADER, element.text()))
                        element.hasClass(TypeContent.ITEM.valueType) -> emitter.onNext(LineContentNewFeed(TypeContent.ITEM, element.text()))
                        element.attributes().size() == 0 -> emitter.onNext(LineContentNewFeed(TypeContent.TEXT, element.text()))
                    }
                }
            }
        }
        emitter.onComplete()
    }).publishDialogLoading()
            .dismissDialogLoadingWhenOnNext()
            .flatMapSingle { (typeContent, textContent) ->
                val positionContent = dataRecyclerView.size
                listContentSource[positionContent] = textContent
                mReadingRepository.translateNewFeed(urlNewFeed, positionContent, textContent)
                        .doOnSuccess {
                            setListVocabularyFromSeverByPosition(positionContent, it.listVocabulary, it.listVocabularyNotTranslate)
                            dataRecyclerView.add(LineContentNewFeed(typeContent,
                                    it.stringData,
                                    it.listVocabulary.map { vocabulary -> WordSpannableHighLight(vocabulary.startIndex, vocabulary.endIndex) },
                                    it.listVocabularyNotTranslate.map { vocabulary -> WordSpannableHighLight(vocabulary.startIndex, vocabulary.endIndex) }))
                        }
            }

    fun getSizeListRefresh() = mListVocabularyToTranslateRefresh.size

    fun getSizeListAddFavorite() = mListVocabularyToAddFavorite.size

    fun addOrRemoveVocabularyToListRefresh(vocabularyLocation: VocabularyLocation): Int {
        var isAdd = true
        listVocabularyUntranslatedResponse[vocabularyLocation.positionContent]?.firstOrNull {
            it.startIndex == vocabularyLocation.startIndex && it.endIndex == vocabularyLocation.endIndex
        }?.let {
            mListVocabularyToTranslateRefresh.firstOrNull { checking ->
                checking.positionContent != vocabularyLocation.positionContent && checking.vocabulary.word == it.word
            }?.let { checking ->
                RxBusTransport.publish(Transport(TypeTransportBus.TOAST_WITH_MESSAGE_SELECT_WORD,
                        message = checking.vocabulary.word))
                isAdd = false
                return NO_ITEM_CHANGE
            }
            with(VocabularySelected(vocabulary = it, positionContent = vocabularyLocation.positionContent)) {
                if (mListVocabularyToTranslateRefresh.contains(this)) {
                    mListVocabularyToTranslateRefresh.remove(this)
                    isAdd = false
                } else {
                    mListVocabularyToTranslateRefresh.add(this)
                    isAdd = true
                }
            }
        }
        dataRecyclerView[vocabularyLocation.positionContent].vocabulariesUntranslated?.firstOrNull {
            it.startIndex == vocabularyLocation.startIndex && it.endIndex == vocabularyLocation.endIndex
        }?.let {
            it.isSelected = isAdd
        }
        return vocabularyLocation.positionContent
    }

    fun addOrRemoveVocabularyToListAddFavoriteByLocationVocabulary(vocabularyLocation: VocabularyLocation): Int {
        var isAdd = true
        listVocabularyTranslatedResponse[vocabularyLocation.positionContent]?.firstOrNull {
            it.startIndex == vocabularyLocation.startIndex && it.endIndex == vocabularyLocation.endIndex
        }?.let {
            mListVocabularyToAddFavorite.firstOrNull { checking ->
                checking.positionContent != vocabularyLocation.positionContent && checking.vocabulary.word == it.word
            }?.let { checking ->
                RxBusTransport.publish(Transport(TypeTransportBus.TOAST_WITH_MESSAGE_SELECT_WORD,
                        message = checking.vocabulary.word))
                isAdd = false
                return NO_ITEM_CHANGE
            }
            with(VocabularySelected(vocabulary = it, positionContent = vocabularyLocation.positionContent)) {
                if (mListVocabularyToAddFavorite.contains(this)) {
                    mListVocabularyToAddFavorite.remove(this)
                    isAdd = false
                } else {
                    mListVocabularyToAddFavorite.add(this)
                    isAdd = true
                }
            }
        }
        dataRecyclerView[vocabularyLocation.positionContent].vocabulariesTranslated?.firstOrNull {
            it.startIndex == vocabularyLocation.startIndex && it.endIndex == vocabularyLocation.endIndex
        }?.let {
            it.isSelected = isAdd
        }
        return vocabularyLocation.positionContent
    }

    fun getArrayWordRefresh() = mListVocabularyToTranslateRefresh.map {
        it.vocabulary.word
    }.toTypedArray()

    fun getArrayWordAddFavorite() = mListVocabularyToAddFavorite.map {
        it.vocabulary.word
    }.toTypedArray()

    fun getArraySelectedRefresh() = mListVocabularyToTranslateRefresh.map {
        it.isChecked
    }.toBooleanArray()

    fun getArraySelectedAddFavorite() = mListVocabularyToAddFavorite.map {
        it.isChecked
    }.toBooleanArray()

    fun resetListVocabularyRefresh() {
        mListVocabularyToTranslateRefresh.clear()
        dataRecyclerView.forEach {
            it.vocabulariesUntranslated?.forEach { wordHighLight ->
                wordHighLight.isSelected = false
            }
        }
    }

    fun resetListVocabularyAddFavorite() {
        mListVocabularyToAddFavorite.clear()
        dataRecyclerView.forEach {
            it.vocabulariesTranslated?.forEach { wordHighLight ->
                wordHighLight.isSelected = false
            }
        }
    }

    fun getPositionItemInsertedOfRV() = dataRecyclerView.size

    fun addFavoriteToServer(): Single<DetailResponse> = mReadingRepository.setListVocabularyFavorite(mListVocabularyToAddFavorite.filter {
        it.isChecked
    }.map {
        it.vocabulary
    })

    fun sendVocabularySelectedToServerToTranslateAgain(): Observable<Int> = Observable.create(ObservableOnSubscribe<Map.Entry<Int, List<VocabularySelected>>> {
        mListVocabularyToTranslateRefresh.groupBy { selected ->
            selected.positionContent
        }.forEach { map ->
            it.onNext(map)
        }
    }).flatMapSingle { map ->
        mReadingRepository.translateNewFeedAgain(urlNewFeed,
                map.key,
                listContentSource[map.key]
                , mListVocabularyToTranslateRefresh.map { vocabularySelected ->
            vocabularySelected.vocabulary
        }).doOnSuccess {
            val oldTypeContent = dataRecyclerView[map.key].typeContent
            dataRecyclerView[map.key] = LineContentNewFeed(oldTypeContent,
                    it.stringData,
                    it.listVocabulary.map { vocabulary -> WordSpannableHighLight(vocabulary.startIndex, vocabulary.endIndex) },
                    it.listVocabularyNotTranslate.map { vocabulary -> WordSpannableHighLight(vocabulary.startIndex, vocabulary.endIndex) })
            // Xoá các từ được dịch thành công khỏi danh sách
            mListVocabularyToTranslateRefresh.removeAll(map.value)
        }.map {
            map.key
        }
    }

    fun isLogin(): Boolean = localRepository.isLogin()

    fun getNameLevelOfUser(): String = localRepository.nameLevelUser

    private fun setListVocabularyFromSeverByPosition(positionContent: Int, vocabulariesTranslated: List<Vocabulary>, vocabulariesUntranslated: List<Vocabulary>) {
        listVocabularyTranslatedResponse[positionContent] = vocabulariesTranslated
        listVocabularyUntranslatedResponse[positionContent] = vocabulariesUntranslated
    }
}
