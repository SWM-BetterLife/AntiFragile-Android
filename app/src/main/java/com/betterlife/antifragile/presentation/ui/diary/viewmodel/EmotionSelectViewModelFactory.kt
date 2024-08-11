package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository

class EmotionSelectViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmotionSelectViewModel::class.java)) {
            val emoticonThemeApiService = RetrofitInterface.createEmoticonThemeApiService(token)
            val emoticonThemeRepository = EmoticonThemeRepository(emoticonThemeApiService)

            @Suppress("UNCHECKED_CAST")
            return EmotionSelectViewModel(emoticonThemeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}