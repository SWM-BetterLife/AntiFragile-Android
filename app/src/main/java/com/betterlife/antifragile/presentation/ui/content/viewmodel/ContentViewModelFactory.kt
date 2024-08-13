package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.ContentRepository

class ContentViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java)) {
            val contentApiService = RetrofitInterface.createContentApiService(token)
            val contentRepository = ContentRepository(contentApiService)

            @Suppress("UNCHECKED_CAST")
            return ContentViewModel(contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}