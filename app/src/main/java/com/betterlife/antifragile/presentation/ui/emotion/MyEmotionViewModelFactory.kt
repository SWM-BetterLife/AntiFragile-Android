package com.betterlife.antifragile.presentation.ui.emotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository

class MyEmotionViewModelFactory(
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyEmotionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyEmotionViewModel(
                diaryAnalysisRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}