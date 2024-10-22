package com.betterlife.antifragile.presentation.ui.mypage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.auth.request.AuthLogoutRequest
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.member.response.MemberMyPageResponse
import com.betterlife.antifragile.data.repository.MyPageRepository
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _memberDetailResponse = MutableLiveData<BaseResponse<MemberMyPageResponse>>()
    val memberDetailResponse = _memberDetailResponse

    private val _memberLogoutResponse = MutableLiveData<BaseResponse<Any?>>()
    val memberLogoutResponse = _memberLogoutResponse

    private val _memberDeleteResponse = MutableLiveData<BaseResponse<Any?>>()
    val memberDeleteResponse = _memberDeleteResponse

    init {
        _memberDetailResponse.value = BaseResponse(Status.INIT, null, null)
        _memberLogoutResponse.value = BaseResponse(Status.INIT, null, null)
        _memberDeleteResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getMemberDetail() {
        viewModelScope.launch {
            _memberDetailResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = myPageRepository.getMemberMyPage()
            _memberDetailResponse.postValue(response)
        }
    }

    fun logout(refreshToken: String) {
        viewModelScope.launch {
            _memberLogoutResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = myPageRepository.logout(AuthLogoutRequest(refreshToken))
            _memberLogoutResponse.postValue(response)
        }
    }

    fun delete() {
        viewModelScope.launch {
            _memberDeleteResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = myPageRepository.delete()
            _memberDeleteResponse.postValue(response)
        }
    }
}