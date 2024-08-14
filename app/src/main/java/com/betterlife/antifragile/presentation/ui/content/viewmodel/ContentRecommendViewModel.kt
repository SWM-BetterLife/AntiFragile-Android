package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.content.response.ContentListResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.repository.ContentRepository
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch

class ContentRecommendViewModel(
    private val contentRepository: ContentRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _contentResponse = MutableLiveData<BaseResponse<ContentListResponse>>()
    val contentResponse = _contentResponse

    private val _remainRecommendNumber = MutableLiveData<BaseResponse<MemberRemainNumberResponse>>()
    val remainRecommendNumber = _remainRecommendNumber

    init {
        _contentResponse.value = BaseResponse(Status.INIT, null, null)
        _remainRecommendNumber.value = BaseResponse(Status.INIT, null, null)
    }

    // 해당 일자 추천 콘텐츠 조회인 경우 호출
    fun getContentList(date: String) {
        viewModelScope.launch {
            _contentResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.getContents(date)
            _contentResponse.postValue(response)
        }
    }

    fun getReRecommendContents(date: String, feedback: String) {
        viewModelScope.launch {
            _contentResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.getReRecommendContents(date, feedback)
            _contentResponse.postValue(response)
        }
    }

    fun getRemainRecommendNumber() {
        viewModelScope.launch {
            _remainRecommendNumber.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.getRemainRecommendNumber()
            _remainRecommendNumber.postValue(response)
        }
    }
}