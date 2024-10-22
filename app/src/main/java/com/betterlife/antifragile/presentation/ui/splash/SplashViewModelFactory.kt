package com.betterlife.antifragile.presentation.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.data.repository.LLMModelRepository

class SplashViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            val llmModelApiService = RetrofitInterface.createLlmModelApiService(context)
            val llmModelRepository = LLMModelRepository(llmModelApiService)
            val authRepository = AuthRepository(RetrofitInterface.getAuthApiService())

            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(llmModelRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}