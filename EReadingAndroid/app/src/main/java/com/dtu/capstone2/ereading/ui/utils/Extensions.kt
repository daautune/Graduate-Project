package com.dtu.capstone2.ereading.ui.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.dtu.capstone2.ereading.ui.model.LineContentNewFeed
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.net.ssl.HttpsURLConnection

internal fun <T> Observable<T>.observeOnUiThread(): Observable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

internal fun <T> Observable<T>.publishDialogLoading(): Observable<T> = this.doOnSubscribe {
    RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_LOADING))
}

internal fun <T> Observable<T>.dismissDialogLoadingWhenOnNext(): Observable<T> = this.doOnNext {
    RxBusTransport.publish(Transport(TypeTransportBus.DISMISS_DIALOG_LOADING))
}.doOnError {
    RxBusTransport.publish(Transport(TypeTransportBus.DISMISS_DIALOG_LOADING))
}

internal fun <T> Single<T>.observeOnUiThread(): Single<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

internal fun <T> Single<T>.publishDialogLoading(): Single<T> =
        this.doOnSubscribe {
            RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_LOADING))
        }.doOnSuccess {
            RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_SUCCESS, message = true))
        }.doOnError {
            (it as? HttpException)?.let { exception ->
                if (exception.code() >= HttpsURLConnection.HTTP_INTERNAL_ERROR) {
                    RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_API_ERROR))
                }
            }
        }

internal fun LineContentNewFeed.setSpannerEvent(): SpannableString {
    val result = SpannableString(this.textContent)
    this.vocabulariesTranslated?.forEach {
        result.setSpan(FavoriteWordClickableSpan(), it.startIndex, it.endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (it.isSelected) {
            result.setHighLightWordWhenSelected(it.startIndex, it.endIndex)
        } else {
            result.removeHighLightWordSelected(it.startIndex, it.endIndex)
        }
    }
    this.vocabulariesUntranslated?.forEach {
        result.setSpan(DefaultWordClickableSpan(), it.startIndex, it.endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (it.isSelected) {
            result.setHighLightWordWhenSelected(it.startIndex, it.endIndex)
        } else {
            result.removeHighLightWordSelected(it.startIndex, it.endIndex)
        }
    }
    return result
}

private fun SpannableString.setHighLightWordWhenSelected(startIndex: Int, endIndex: Int): SpannableString {
    this.setSpan(ForegroundColorSpan(Color.GREEN), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

private fun SpannableString.removeHighLightWordSelected(startIndex: Int, endIndex: Int): SpannableString {
    this.getSpans(startIndex, endIndex, ForegroundColorSpan::class.java).forEach {
        this.removeSpan(it)
    }
    return this
}
