package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

data class FavoriteDeletedResponse(@SerializedName("detail") val idfavorite: Int)
