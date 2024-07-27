package com.betterlife.antifragile.presentation.ui.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.CalendarRepository

class DiaryCalendarViewModelFactory(
    private val calendarRepository: CalendarRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryCalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryCalendarViewModel(calendarRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}