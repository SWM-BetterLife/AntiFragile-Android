package com.betterlife.antifragile.data.model.diaryanalysis.response

data class DiaryAnalysisEmoticonsResponse(
    val diaries: List<DiaryEmoticon>
)

data class DiaryEmoticon(
    val emoticonUrl: String,
    val date: String
)