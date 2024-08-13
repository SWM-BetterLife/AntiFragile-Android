package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.data.repository.MemberRepository

class ProfileEditViewModelFactory(
    private val token: String?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileEditViewModel::class.java)) {
            val authApiService = RetrofitInterface.createAuthApiService()
            val authRepository = AuthRepository(authApiService)
            val memberApiService = RetrofitInterface.createMemberApiService(token?: "")
            val memberRepository = MemberRepository(memberApiService)

            @Suppress("UNCHECKED_CAST")
            return ProfileEditViewModel(authRepository, memberRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}