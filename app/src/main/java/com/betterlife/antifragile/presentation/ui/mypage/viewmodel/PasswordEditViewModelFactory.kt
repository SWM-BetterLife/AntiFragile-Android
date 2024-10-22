package com.betterlife.antifragile.presentation.ui.mypage.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.MemberRepository

class PasswordEditViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordEditViewModel::class.java)) {
            val memberApiService = RetrofitInterface.createMemberApiService(context)
            val memberRepository = MemberRepository(memberApiService)

            @Suppress("UNCHECKED_CAST")
            return PasswordEditViewModel(memberRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}