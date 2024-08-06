package com.betterlife.antifragile.data.model.diary

import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryEmoticon

data class TextDiaryDetail(
    val id: Int?,
    val date: String?,
    val content: String?,
    val emotions: List<String>?,
    val emoticonInfo: DiaryEmoticon?
)