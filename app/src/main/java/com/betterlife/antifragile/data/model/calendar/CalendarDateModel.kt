package com.betterlife.antifragile.data.model.calendar

data class CalendarDateModel(
    val date: String,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false,
    val emoticonUrl: String? = null,
    val diaryId: Int? = null
)
