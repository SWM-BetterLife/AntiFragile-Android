package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository

class DiarySelectEmoticonViewModelFactory(private val repository: DiaryAnalysisRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiarySelectEmoticonVIewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiarySelectEmoticonVIewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}