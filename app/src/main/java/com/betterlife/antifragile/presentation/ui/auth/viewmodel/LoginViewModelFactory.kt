package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.data.repository.MemberRepository

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository, memberRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}