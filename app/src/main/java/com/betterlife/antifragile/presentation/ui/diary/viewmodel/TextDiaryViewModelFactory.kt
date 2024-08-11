package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.local.DiaryDatabase
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.DiaryRepository
import com.betterlife.antifragile.data.repository.TextDiaryRepository

class TextDiaryViewModelFactory(
    private val context: Context,
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextDiaryViewModel::class.java)) {
            val diaryDao = DiaryDatabase.getDatabase(context).diaryDao()
            val diaryRepository = DiaryRepository(diaryDao)
            val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
            val diaryAnalysisRepository = DiaryAnalysisRepository(diaryAnalysisApiService)
            val textDiaryRepository = TextDiaryRepository(diaryRepository, diaryAnalysisRepository)

            @Suppress("UNCHECKED_CAST")
            return TextDiaryViewModel(textDiaryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}