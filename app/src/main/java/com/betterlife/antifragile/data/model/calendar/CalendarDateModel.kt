package com.betterlife.antifragile.data.model.calendar

import com.betterlife.antifragile.data.model.diary.DiaryType

data class CalendarDateModel(
    val date: String,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false,
    val diaryType: DiaryType? = null,
    val emoticonUrl: String? = null,
    val diaryId: Int? = null
)
