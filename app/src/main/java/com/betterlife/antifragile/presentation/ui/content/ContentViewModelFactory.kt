package com.betterlife.antifragile.presentation.ui.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.ContentRepository

class ContentViewModelFactory(
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {

    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContentViewModel(
                contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}