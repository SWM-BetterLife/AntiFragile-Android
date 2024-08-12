package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthSignUpResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authSignUpResponse = MutableLiveData<BaseResponse<AuthSignUpResponse>>()
    val authSignUpResponse: LiveData<BaseResponse<AuthSignUpResponse>> = _authSignUpResponse

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