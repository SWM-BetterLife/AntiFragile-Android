package com.betterlife.antifragile.data.model.diaryanalysis.response

data class DiaryAnalysisEmoticonsResponse(
    val emoticons: List<CalendarDiaryEmoticon>
)

data class CalendarDiaryEmoticon(
    val imgUrl: String,
    val diaryDate: String
)