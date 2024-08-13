package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.ContentRepository
import com.betterlife.antifragile.data.repository.MemberRepository

class ContentRecommendViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentRecommendViewModel::class.java)) {
            val contentApiService = RetrofitInterface.createContentApiService(token)
            val contentRepository = ContentRepository(contentApiService)
            val memberApiService = RetrofitInterface.createMemberApiService(token)
            val memberRepository = MemberRepository(memberApiService)

            @Suppress("UNCHECKED_CAST")
            return ContentRecommendViewModel(contentRepository, memberRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}