package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

data class DataLoginRequest(@SerializedName("token") val stringToken: String,
                            @SerializedName("user_id") val intId: Int,
                            @SerializedName("email") val stringEmail: String,
                            @SerializedName("level_english") val levelNameUser: String? = "")
