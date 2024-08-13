package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import com.betterlife.antifragile.data.repository.MemberRepository

class EmoticonRecommendViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmoticonRecommendViewModel::class.java)) {
            val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(context)
            val diaryAnalysisRepository = DiaryAnalysisRepository(diaryAnalysisApiService)
            val emoticonThemeApiService = RetrofitInterface.createEmoticonThemeApiService(context)
            val emoticonThemeRepository = EmoticonThemeRepository(emoticonThemeApiService)
            val memberApiService = RetrofitInterface.createMemberApiService(context)
            val memberRepository = MemberRepository(memberApiService)

            @Suppress("UNCHECKED_CAST")
            return EmoticonRecommendViewModel(
                diaryAnalysisRepository, emoticonThemeRepository, memberRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}