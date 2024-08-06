package com.betterlife.antifragile.data.model.diaryanalysis.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class DiaryAnalysisDailyResponse(
    val emotions: List<String>,
    val emoticon: DiaryEmoticon
)

@Parcelize
data class DiaryEmoticon(
    val imgUrl: String,
    val emoticonThemeId: String,
    val emotion: String
): Parcelable