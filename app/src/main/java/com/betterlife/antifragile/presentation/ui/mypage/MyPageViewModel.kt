package com.betterlife.antifragile.presentation.ui.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.member.response.MemberDetailResponse
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val memberRepository: MemberRepository
) : ViewModel(){

    private val _memberDetailResponse = MutableLiveData<BaseResponse<MemberDetailResponse>>()
    val memberDetailResponse = _memberDetailResponse

    init {
        _memberDetailResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getMemberDetail() {
        viewModelScope.launch {
            _memberDetailResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.getMemberDetail()
            _memberDetailResponse.postValue(response)
        }
    }


}