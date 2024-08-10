package com.betterlife.antifragile.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.data.repository.MemberRepository

class MyPageViewModelFactory(
    private val memberRepository: MemberRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(
                memberRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
