package com.betterlife.antifragile.presentation.ui.emotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository

class MyEmotionViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyEmotionViewModel::class.java)) {
            val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
            val diaryAnalysisRepository = DiaryAnalysisRepository(diaryAnalysisApiService)

            @Suppress("UNCHECKED_CAST")
            return MyEmotionViewModel(
                diaryAnalysisRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}