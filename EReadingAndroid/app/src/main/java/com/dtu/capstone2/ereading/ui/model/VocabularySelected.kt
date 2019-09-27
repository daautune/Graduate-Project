package com.dtu.capstone2.ereading.ui.model

import com.dtu.capstone2.ereading.network.request.Vocabulary

/**
 * Create by Nguyen Van Phuc on 2019-04-25
 */
data class VocabularySelected(val positionContent: Int,
                              val vocabulary: Vocabulary,
                              val isChecked: Boolean = true)
