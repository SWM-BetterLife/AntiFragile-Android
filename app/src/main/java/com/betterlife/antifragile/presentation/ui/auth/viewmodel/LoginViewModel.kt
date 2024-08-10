package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.auth.AuthSignUpRequest
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authSignUpResponse = MutableLiveData<BaseResponse<Any?>>()
    val authSignUpResponse: LiveData<BaseResponse<Any?>> = _authSignUpResponse

    init {
        _authSignUpResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun signUp(request: AuthSignUpRequest) {
        viewModelScope.launch {
            _authSignUpResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = authRepository.signUp(request)
            _authSignUpResponse.postValue(response)
            _authSignUpResponse.value = response
        }
    }
}