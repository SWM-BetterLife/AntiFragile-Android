package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.DiaryRepository

class TextDiaryViewModelFactory(
    private val diaryRepository: DiaryRepository,
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextDiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TextDiaryViewModel(
                diaryRepository, diaryAnalysisRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}