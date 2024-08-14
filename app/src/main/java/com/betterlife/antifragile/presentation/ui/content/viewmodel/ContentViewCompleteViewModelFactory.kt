package com.betterlife.antifragile.presentation.ui.content.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.local.DiaryDatabase
import com.betterlife.antifragile.data.repository.ContentRepository
import com.betterlife.antifragile.data.repository.DiaryRepository
import com.betterlife.antifragile.data.repository.MemberRepository

class ContentViewCompleteViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewCompleteViewModel::class.java)) {
            val diaryDao = DiaryDatabase.getDatabase(context).diaryDao()
            val diaryRepository = DiaryRepository(diaryDao)
            val memberApiService = RetrofitInterface.createMemberApiService(context)
            val memberRepository = MemberRepository(memberApiService)
            val contentApiService = RetrofitInterface.createContentApiService(context)
            val contentRepository = ContentRepository(contentApiService)

            @Suppress("UNCHECKED_CAST")
            return ContentViewCompleteViewModel(
                diaryRepository, memberRepository, contentRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}