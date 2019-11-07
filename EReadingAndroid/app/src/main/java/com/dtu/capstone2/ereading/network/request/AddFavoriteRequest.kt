package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

data class AddFavoriteRequest(@SerializedName("idUser") val idUser: Int,
                              @SerializedName("idVocabulary") val idVocabulary: Int)
