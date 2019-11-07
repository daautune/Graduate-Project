package com.dtu.capstone2.ereading.network.response

import com.dtu.capstone2.ereading.ui.model.LevelEnglish
import com.google.gson.annotations.SerializedName

data class LevelUserResponse(@SerializedName("level_user") val level: LevelEnglish?)
