package com.dtu.capstone2.ereading.network.utils

import io.reactivex.*
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Override this class to handle http exception and convert api success to error
 */
open class BaseRxCallAdapterWrapper<R>(private val type: Type, private val retrofit: Retrofit, private val wrapped: CallAdapter<R, *>?) : CallAdapter<R, Any> {

    override fun adapt(call: Call<R>): Any? {
        val adapt = wrapped?.adapt(call)
        return when (adapt) {
            is Maybe<*> -> adapt.map(this::handleCustomError).onErrorResumeNext(this::handleMaybeRetrofitError)
            is Single<*> -> adapt.map(this::handleCustomError).onErrorResumeNext(this::handleSingleRetrofitError)
            is Flowable<*> -> adapt.map(this::handleCustomError).onErrorResumeNext(this::handleFlowableRetrofitError)
            is Observable<*> -> adapt.map(this::handleCustomError).onErrorResumeNext(this::handleObservableRetrofitError)
            else -> CustomCallAdapter<R>(call, retrofit)
        }
    }

    private fun handleMaybeRetrofitError(t: Throwable): MaybeSource<R>? = MaybeSubject.error<R> { convertRetrofitExceptionToCustomException(t, retrofit) }

    private fun handleSingleRetrofitError(t: Throwable): SingleSource<R>? = SingleSubject.error<R> { convertRetrofitExceptionToCustomException(t, retrofit) }

    private fun handleFlowableRetrofitError(t: Throwable): Flowable<R>? = Flowable.error<R> { convertRetrofitExceptionToCustomException(t, retrofit) }

    private fun handleObservableRetrofitError(t: Throwable): ObservableSource<R>? = Observable.error { convertRetrofitExceptionToCustomException(t, retrofit) }

    /**
     * Generate app api exception
     */
    open fun convertRetrofitExceptionToCustomException(throwable: Throwable, retrofit: Retrofit): Throwable = throwable

    /**
     * Some resful api handle error in success response, this method is used to return exception
     */
    open fun createExceptionForSuccessResponse(response: Any?): Throwable? = null

    private fun handleCustomError(response: Any): Any {
        createExceptionForSuccessResponse(response)?.let {
            throw it
        }
        return response
    }

    override fun responseType(): Type? = type
}
