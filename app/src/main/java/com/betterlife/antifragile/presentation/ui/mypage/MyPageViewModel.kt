package com.betterlife.antifragile.presentation.ui.mypage

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

    init {
        _memberDetailResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getMemberDetail() {
        viewModelScope.launch {
            _memberDetailResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = myPageRepository.getMemberMyPage()
            _memberDetailResponse.postValue(response)
        }
    }

    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                myPageRepository.logout(AuthLogoutRequest())
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "로그아웃 실패")
            }
        }
    }

    fun delete(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                myPageRepository.delete()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "회원 탈퇴 실패")
            }
        }
    }
}