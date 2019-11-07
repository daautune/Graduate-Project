package com.dtu.capstone2.ereading.ui.model

import com.dtu.capstone2.ereading.network.request.Vocabulary

data class VocabularySelected(val positionContent: Int,
                              val vocabulary: Vocabulary,
                              val isChecked: Boolean = true)
