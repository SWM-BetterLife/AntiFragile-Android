package com.betterlife.antifragile.data.model.llm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiaryAnalysisData(
    val emotions: List<String>,
    val event: String,
    val thought: String,
    val action: String,
    val comment: String,
    val diaryDate: String
) : Parcelable