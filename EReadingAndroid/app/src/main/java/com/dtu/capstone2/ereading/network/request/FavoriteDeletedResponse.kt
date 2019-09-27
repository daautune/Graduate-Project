package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

/**
 * Create by Vo The Doan on 05/07/2019
 */
data class FavoriteDeletedResponse(@SerializedName("detail") val idfavorite: Int)
