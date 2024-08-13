package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.LLMRepository

class LLMViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LLMViewModel::class.java)) {
            val llmModelRepository = LLMRepository(context)

            @Suppress("UNCHECKED_CAST")
            return LLMViewModel(llmModelRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}