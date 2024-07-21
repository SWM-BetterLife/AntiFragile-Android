package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository

class DiaryAnalysisViewModelFactory(private val repository: DiaryAnalysisRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryAnalysisViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}