package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

/**
 * Create by Nguyen Van Phuc on 3/11/19
 */
data class AccountLoginRequest(@SerializedName("username") val userName: String,
                               @SerializedName("password") val password: String)
