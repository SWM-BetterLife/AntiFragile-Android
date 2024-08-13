package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.AuthRepository

class LoginViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val authApiService = RetrofitInterface.getAuthApiService()
            val authRepository = AuthRepository(authApiService)

            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}