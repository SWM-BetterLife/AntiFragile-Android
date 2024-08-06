package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository

class EmotionSelectViewModelFactory(
    private val repository: EmoticonThemeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmotionSelectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmotionSelectViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}