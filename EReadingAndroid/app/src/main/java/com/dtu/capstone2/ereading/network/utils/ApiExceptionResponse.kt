package com.dtu.capstone2.ereading.network.utils

import com.google.gson.annotations.SerializedName

/**
 * Use this file to handle error from api
 */
data class ApiExceptionResponse(
        @SerializedName("message") val messageError: String,
        val errors: MessageApiException? = MessageApiException(),
        @SerializedName("status") val status: String = "",
        @SerializedName("string") val latestVersion: String = "",
        @SerializedName("update_url") val updateUrl: String = "") : Throwable(messageError) {

    companion object {
        internal const val FORCE_UPDATE_ERROR_CODE = 426
        internal const val NETWORK_ERROR_CODE = 700
        internal const val UNKNOWN_ERROR = 999
        internal const val MAINTENANCE = 503
        internal const val USER_BANNER = 451
        internal const val FUNC_BANNER = 423
        internal const val LIMIT_MESSAGE_REQUEST = 429
        internal const val LIMIT_LIKE_REQUEST = 422
        internal const val MAINTENANCE_CARD = 603
    }

    var statusCode: Int? = 0
}
