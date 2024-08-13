package com.betterlife.antifragile.presentation.ui.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.MyPageRepository

class MyPageViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            val memberApiService = RetrofitInterface.createMemberApiService(context)
            val authApiService = RetrofitInterface.getAuthApiService()
            val myPageRepository = MyPageRepository(memberApiService, authApiService)

            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(myPageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}