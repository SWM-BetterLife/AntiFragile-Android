package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository

class EmotionSelectViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmotionSelectViewModel::class.java)) {
            val emoticonThemeApiService = RetrofitInterface.createEmoticonThemeApiService(context)
            val emoticonThemeRepository = EmoticonThemeRepository(emoticonThemeApiService)

            @Suppress("UNCHECKED_CAST")
            return EmotionSelectViewModel(emoticonThemeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}