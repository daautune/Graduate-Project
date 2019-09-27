package com.dtu.capstone2.ereading.ui.model

import com.google.gson.annotations.SerializedName

/**
 * Create by Nguyen Van Phuc on 4/3/19
 */
data class AccountErrorResponse(@SerializedName("username") val userName: List<String>?,
                                val password: List<String>?,
                                @SerializedName("password_confirm") val passwordConfirm: List<String>?,
                                val email: List<String>?) {

    val userNameError
        get() = userName?.joinToString() ?: ""

    val passwordError
        get() = password?.joinToString() ?: ""

    val emailError
        get() = email?.joinToString() ?: ""

    val passwordConfirmError
        get() = passwordConfirm?.joinToString() ?: ""
}
