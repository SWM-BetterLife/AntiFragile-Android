package com.betterlife.antifragile.presentation.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.auth.request.AuthSignUpRequest
import com.betterlife.antifragile.data.model.auth.response.AuthSignUpResponse
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.member.request.MemberProfileModifyRequest
import com.betterlife.antifragile.data.model.member.response.MemberProfileModifyResponse
import com.betterlife.antifragile.data.model.member.response.NicknameDuplicateResponse
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileEditViewModel(
    private val authRepository: AuthRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _authSignUpResponse = MutableLiveData<BaseResponse<AuthSignUpResponse>>()
    val authSignUpResponse: LiveData<BaseResponse<AuthSignUpResponse>> = _authSignUpResponse

    private val _profileModifyResponse = MutableLiveData<BaseResponse<MemberProfileModifyResponse>>()
    val profileModifyResponse = _profileModifyResponse

    private val _checkNicknameResponse = MutableLiveData<BaseResponse<NicknameDuplicateResponse>>()
    val checkNicknameResponse= _checkNicknameResponse

    init {
        _authSignUpResponse.value = BaseResponse(Status.INIT, null, null)
        _profileModifyResponse.value = BaseResponse(Status.INIT, null, null)
        _checkNicknameResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun signUp(profileImgFile: MultipartBody.Part?, request: AuthSignUpRequest) {
        viewModelScope.launch {
            _authSignUpResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = authRepository.signUp(profileImgFile, request)
            _authSignUpResponse.postValue(response)
            _authSignUpResponse.value = response
        }
    }

    fun modifyProfile(profileImgFile: MultipartBody.Part?, request: MemberProfileModifyRequest) {
        viewModelScope.launch {
            _profileModifyResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.modifyProfile(profileImgFile, request)
            _profileModifyResponse.postValue(response)
            _profileModifyResponse.value = response
        }
    }

    fun checkNicknameInvalid(nickname: String) {
        viewModelScope.launch {
            _checkNicknameResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.checkNicknameExistence(nickname)
            _checkNicknameResponse.postValue(response)
            _checkNicknameResponse.value = response
        }
    }
}