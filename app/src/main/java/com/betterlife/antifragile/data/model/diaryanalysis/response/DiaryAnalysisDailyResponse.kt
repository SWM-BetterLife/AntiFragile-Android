package com.betterlife.antifragile.data.model.diaryanalysis.response

data class DiaryAnalysisDailyResponse(
    val emotions: List<String>,
    val emoticon: DiaryEmoticon
)

data class DiaryEmoticon(
    val imgUrl: String,
    val emoticonThemeId: String,
    val emotion: String
)