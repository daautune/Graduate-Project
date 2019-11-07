package com.dtu.capstone2.ereading.network.request

import com.google.gson.annotations.SerializedName

data class DataStringReponse(@SerializedName("text") val stringData: String,
                             @SerializedName("listWords") val listVocabulary: List<Vocabulary>,
                             @SerializedName("list_word_not_translate") val listVocabularyNotTranslate: List<Vocabulary>)

data class Vocabulary(@SerializedName("id") val idVocabulary: Int,
                      val word: String,
                      val type: String,
                      @SerializedName("start_index") val startIndex: Int,
                      @SerializedName("end_index") val endIndex: Int)
