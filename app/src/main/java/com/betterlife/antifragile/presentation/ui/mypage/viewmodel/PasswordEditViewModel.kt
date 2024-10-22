package com.betterlife.antifragile.presentation.ui.mypage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.auth.request.AuthPasswordModifyRequest
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.AuthRepository
import kotlinx.coroutines.launch

class PasswordEditViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authModifyPasswordResponse = MutableLiveData<BaseResponse<Any?>>()
    val authModifyPasswordResponse = _authModifyPasswordResponse

    fun modifyPassword(request: AuthPasswordModifyRequest) {
        viewModelScope.launch {
            _authModifyPasswordResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = authRepository.modifyPassword(request)
            _authModifyPasswordResponse.postValue(response)
        }
    }
}