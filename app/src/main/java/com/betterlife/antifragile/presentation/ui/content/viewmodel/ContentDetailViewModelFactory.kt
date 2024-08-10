package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.ContentRepository

class ContentDetailViewModelFactory(
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContentDetailViewModel(contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}