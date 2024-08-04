package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository

class RecommendEmoticonViewModelFactory(
    private val diaryAnalysisRepository: DiaryAnalysisRepository,
    private val emoticonThemeRepository: EmoticonThemeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendEmoticonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendEmoticonViewModel(
                diaryAnalysisRepository, emoticonThemeRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}