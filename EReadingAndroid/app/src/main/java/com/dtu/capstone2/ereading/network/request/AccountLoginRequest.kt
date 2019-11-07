package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

data class AccountLoginRequest(@SerializedName("username") val userName: String,
                               @SerializedName("password") val password: String)
