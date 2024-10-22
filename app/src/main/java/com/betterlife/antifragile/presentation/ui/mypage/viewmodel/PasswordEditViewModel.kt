package com.betterlife.antifragile.presentation.ui.mypage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.member.request.MemberPasswordModifyRequest
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch

class PasswordEditViewModel(
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _memberModifyPasswordResponse = MutableLiveData<BaseResponse<Any?>>()
    val memberModifyPasswordResponse = _memberModifyPasswordResponse

    fun modifyPassword(request: MemberPasswordModifyRequest) {
        viewModelScope.launch {
            _memberModifyPasswordResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.modifyPassword(request)
            _memberModifyPasswordResponse.postValue(response)
        }
    }
}