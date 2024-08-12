package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.auth.response.AuthLoginResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.member.response.MemberExistenceResponse
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _memberExistenceResponse = MutableLiveData<BaseResponse<MemberExistenceResponse>>()
    val memberExistenceResponse: LiveData<BaseResponse<MemberExistenceResponse>> = _memberExistenceResponse

    private val _authLoginResponse = MutableLiveData<BaseResponse<AuthLoginResponse>>()
    val authLoginResponse: LiveData<BaseResponse<AuthLoginResponse>> = _authLoginResponse

    init {
        _memberExistenceResponse.value = BaseResponse(Status.INIT, null, null)
        _authLoginResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun login(request: AuthLoginRequest) {
        viewModelScope.launch {
            _authLoginResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = authRepository.login(request)
            _authLoginResponse.postValue(response)
        }
    }

    fun checkMemberExistence(email: String, loginType: LoginType) {
        viewModelScope.launch {
            _memberExistenceResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.checkMemberExistence(email, loginType)
            _memberExistenceResponse.postValue(response)
        }
    }
}