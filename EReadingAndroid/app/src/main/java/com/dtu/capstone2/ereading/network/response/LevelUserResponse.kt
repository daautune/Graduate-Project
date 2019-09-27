package com.dtu.capstone2.ereading.network.response

import com.dtu.capstone2.ereading.ui.model.LevelEnglish
import com.google.gson.annotations.SerializedName

/**
 * Create by Nguyen Van Phuc on 4/12/19
 */
data class LevelUserResponse(@SerializedName("level_user") val level: LevelEnglish?)
