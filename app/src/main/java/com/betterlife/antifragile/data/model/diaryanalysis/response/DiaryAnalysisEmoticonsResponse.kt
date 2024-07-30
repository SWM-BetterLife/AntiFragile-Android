package com.betterlife.antifragile.data.model.diaryanalysis.response

data class DiaryAnalysisEmoticonsResponse(
    val emoticons: List<DiaryEmoticon>
)

data class DiaryEmoticon(
    val imgUrl: String,
    val diaryDate: String
)