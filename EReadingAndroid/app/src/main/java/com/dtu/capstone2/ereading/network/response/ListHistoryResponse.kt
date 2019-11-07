package com.dtu.capstone2.ereading.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class ListHistoryResponse(@SerializedName("result_count") val resultCount: Int,
                               @SerializedName("page") val page: Int,
                               @SerializedName("next_page_flg") val nextPageFlg: Boolean,
                               @SerializedName("result") val listData: ArrayList<HistoryNewFeed>)

data class HistoryNewFeed(@SerializedName("title_new_feed") val titleNewsFeed: String,
                          @SerializedName("time_create") val timeCreate: String,
                          @SerializedName("introduction_new_feed") val introduction: String)
