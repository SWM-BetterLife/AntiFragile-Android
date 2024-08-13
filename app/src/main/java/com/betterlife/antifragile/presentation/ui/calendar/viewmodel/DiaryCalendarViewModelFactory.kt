package com.betterlife.antifragile.presentation.ui.calendar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.local.DiaryDatabase
import com.betterlife.antifragile.data.repository.CalendarRepository
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.DiaryRepository

class DiaryCalendarViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryCalendarViewModel::class.java)) {
            val diaryDao = DiaryDatabase.getDatabase(context).diaryDao()
            val diaryRepository = DiaryRepository(diaryDao)
            val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(context)
            val diaryAnalysisRepository = DiaryAnalysisRepository(diaryAnalysisApiService)
            val calendarRepository = CalendarRepository(diaryRepository, diaryAnalysisRepository)

            @Suppress("UNCHECKED_CAST")
            return DiaryCalendarViewModel(calendarRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}