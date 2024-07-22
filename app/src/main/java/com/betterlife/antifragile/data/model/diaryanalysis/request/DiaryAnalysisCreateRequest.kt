package com.betterlife.antifragile.data.model.diaryanalysis.request

data class DiaryAnalysisCreateRequest(
    val emotions: List<String>,
    val event: String,
    val thought: String,
    val action: String,
    val comment: String,
    val diaryDate: String,
    val emoticon: Emoticon
)

data class Emoticon(
    val emoticonThemeId: String,
    val emotion: String
)