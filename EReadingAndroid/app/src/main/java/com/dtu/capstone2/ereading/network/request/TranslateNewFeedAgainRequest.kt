package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

/**
 * Create by Nguyen Van Phuc on 4/14/19
 */
data class TranslateNewFeedAgainRequest(@SerializedName("url_source_feed") val urlSourceFeed: String,
                                        @SerializedName("position_content") val positionContent: Int,
                                        val words: String,
                                        val vocabularies: List<Vocabulary>)
