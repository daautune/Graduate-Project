package com.dtu.capstone2.ereading.network.utils

import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.*
import java.io.EOFException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

interface CustomCallback<T> {
    /** Called for [200] responses.  */
    fun success(call: Call<T>, response: Response<T>)

    /** Called for [401] responses.  */
    fun unauthenticated(t: Throwable)

    /** Called for [400, 500) responses, except 401.  */
    fun clientError(t: Throwable)

    /** Called for [500, 600) response.  */
    fun serverError(t: Throwable)

    /** Called for network errors while making the call.  */
    fun networkError(e: Throwable)

    /** Called for unexpected errors while making the call.  */
    fun unexpectedError(t: Throwable)
}

/**
 * CustomCall
 */
interface CustomCall<T> {
    /**
     * Cancel call
     */
    fun cancel()

    /**
     * Enqueue call
     */
    fun enqueue(callback: CustomCallback<T>)

    /**
     * Execute call
     */
    fun execute(): Response<T>

    /**
     * Clone
     */
    fun clone(): CustomCall<T>

    /**
     * Request call
     */
    fun request(): Request

    /**
     * Check Call is canceled
     */
    fun isCanceled(): Boolean

    /**
     * Check Call is executed
     */
    fun isExecuted(): Boolean
}

internal class CustomCallAdapter<T>(private val call: Call<T>, private val retrofit: Retrofit) : CustomCall<T> {
    override fun execute() = call.execute()

    override fun clone() = this

    override fun request() = Request.Builder().build()

    override fun isCanceled() = call.isCanceled

    override fun isExecuted() = call.isExecuted

    override fun cancel() {
        call.cancel()
    }

    override fun enqueue(callback: CustomCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val code = response.code()
                try {
                    when (code) {
                        HttpURLConnection.HTTP_OK -> callback.success(call, response)
                        HttpURLConnection.HTTP_UNAUTHORIZED -> {
                            val converter: Converter<ResponseBody, ApiExceptionResponse> = retrofit.responseBodyConverter(ApiExceptionResponse::class.java, arrayOfNulls<Annotation>(0))
                            val responseAfterConvert = converter.convert(response.errorBody())
                            responseAfterConvert?.statusCode = HttpURLConnection.HTTP_UNAUTHORIZED
                            callback.serverError(responseAfterConvert!!)
                        }

                        HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                            val converter: Converter<ResponseBody, ApiExceptionResponse> = retrofit.responseBodyConverter(ApiExceptionResponse::class.java, arrayOfNulls<Annotation>(0))
                            val responseAfterConvert = converter.convert(response.errorBody())
                            callback.serverError(responseAfterConvert!!)
                        }

                        HttpURLConnection.HTTP_BAD_REQUEST -> {
                            val converter: Converter<ResponseBody, ApiExceptionResponse> = retrofit.responseBodyConverter(ApiExceptionResponse::class.java, arrayOfNulls<Annotation>(0))
                            val responseAfterConvert = converter.convert(response.errorBody())
                            callback.clientError(responseAfterConvert!!)
                        }

                        HttpURLConnection.HTTP_NOT_ACCEPTABLE -> {
                            val converter: Converter<ResponseBody, ApiExceptionResponse> = retrofit.responseBodyConverter(ApiExceptionResponse::class.java, arrayOfNulls<Annotation>(0))
                            val responseAfterConvert = converter.convert(response.errorBody())
                            callback.clientError(responseAfterConvert!!)
                        }

                        HttpsURLConnection.HTTP_NOT_FOUND -> {
                            val converter: Converter<ResponseBody, ApiExceptionResponse> = retrofit.responseBodyConverter(ApiExceptionResponse::class.java, arrayOfNulls<Annotation>(0))
                            val responseAfterConvert = converter.convert(response.errorBody())
                            callback.clientError(responseAfterConvert!!)
                        }

                        HttpsURLConnection.HTTP_FORBIDDEN -> {
                            val converter: Converter<ResponseBody, ApiExceptionResponse> = retrofit.responseBodyConverter(ApiExceptionResponse::class.java, arrayOfNulls<Annotation>(0))
                            val responseAfterConvert = converter.convert(response.errorBody())
                            callback.clientError(responseAfterConvert!!)
                        }

                        //Todo: Handle another status code
                        else -> callback.unexpectedError(Throwable("Error unknow"))
                    }
                } catch (e: EOFException) {
                    callback.unexpectedError(e)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (t is IOException) {
                    when (t) {
                        is UnknownHostException -> {
                            val apiException = ApiExceptionResponse("", MessageApiException())
                            apiException.statusCode = ApiExceptionResponse.NETWORK_ERROR_CODE
                            callback.networkError(apiException)
                        }
                        is SocketTimeoutException -> {
                            val apiException = ApiExceptionResponse("", MessageApiException())
                            apiException.statusCode = HttpURLConnection.HTTP_CLIENT_TIMEOUT
                            callback.networkError(apiException)
                        }
                        else -> callback.networkError(t)
                    }
                } else {
                    callback.unexpectedError(t)
                }
            }
        })
    }
}
