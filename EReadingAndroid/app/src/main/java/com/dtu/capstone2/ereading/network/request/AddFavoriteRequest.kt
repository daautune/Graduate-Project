package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

/**
 * Create by Vo The Doan on 3/21/2019
 */
data class AddFavoriteRequest(@SerializedName("idUser") val idUser: Int,
                              @SerializedName("idVocabulary") val idVocabulary: Int)
