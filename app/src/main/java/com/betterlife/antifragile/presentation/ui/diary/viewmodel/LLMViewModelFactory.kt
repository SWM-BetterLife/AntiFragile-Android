package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LLMViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LLMViewModel::class.java)) {
            return LLMViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}