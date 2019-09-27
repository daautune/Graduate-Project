package com.dtu.capstone2.ereading.network.utils

import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection


class RxCallAdapterWrapper<R>(type: Type, retrofit: Retrofit, wrapped: CallAdapter<R, *>?) : BaseRxCallAdapterWrapper<R>(type, retrofit, wrapped) {

    override fun convertRetrofitExceptionToCustomException(throwable: Throwable, retrofit: Retrofit): Throwable {

        if (throwable is HttpException) {
            val response: Response<*>? = throwable.response()
            when (response?.code()) {
                HttpsURLConnection.HTTP_BAD_REQUEST -> response.errorBody()?.let {
                    val messageError = it.string()
                    val apiException = ApiExceptionResponse(messageError)
                    apiException.statusCode = HttpsURLConnection.HTTP_BAD_REQUEST
                    return apiException
                }
                HttpsURLConnection.HTTP_UNAUTHORIZED -> response.errorBody()?.let {
                    val messageError = it.string()
                    val apiException = ApiExceptionResponse(messageError)
                    apiException.statusCode = HttpsURLConnection.HTTP_UNAUTHORIZED
                    return apiException
                }
            }
        }

        if (throwable is UnknownHostException || throwable is ConnectException) {
            // Set message error of this case in activity extension
            val apiException = ApiExceptionResponse("", MessageApiException())
            apiException.statusCode = ApiExceptionResponse.NETWORK_ERROR_CODE
            return apiException
        }

        if (throwable is SocketTimeoutException) {
            val apiException = ApiExceptionResponse("", MessageApiException())
            apiException.statusCode = HttpURLConnection.HTTP_CLIENT_TIMEOUT
            return apiException
        }

        return throwable
    }
}
