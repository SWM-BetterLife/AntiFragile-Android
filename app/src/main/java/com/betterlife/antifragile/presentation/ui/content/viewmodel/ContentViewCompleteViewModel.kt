package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diary.DiaryInfo
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.repository.DiaryRepository
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch

class ContentViewCompleteViewModel(
    private val diaryRepository: DiaryRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _remainRecommendNumber = MutableLiveData<BaseResponse<MemberRemainNumberResponse>>()
    val remainRecommendNumber = _remainRecommendNumber

    private val _diaryInfo = MutableLiveData<BaseResponse<DiaryInfo?>>()
    val diaryInfo = _diaryInfo

    init {
        _remainRecommendNumber.value = BaseResponse(Status.INIT, null, null)
        _diaryInfo.value = BaseResponse(Status.INIT, null, null)
    }

    fun getRemainRecommendNumber() {
        viewModelScope.launch {
            _remainRecommendNumber.value = BaseResponse(Status.LOADING, null, null)
            val response = memberRepository.getRemainRecommendNumber()
            _remainRecommendNumber.postValue(response)
        }
    }

    fun getDiaryInfoByDate(date: String) {
        viewModelScope.launch {
            _diaryInfo.value = BaseResponse(Status.LOADING, null, null)
            val diaryInfo = diaryRepository.getDiaryInfoByDate(date)
            if  (diaryInfo == null) {
                _diaryInfo.postValue(BaseResponse(Status.ERROR, null, null))
            } else {
                _diaryInfo.postValue(BaseResponse(Status.SUCCESS, null, diaryInfo))
            }
        }
    }
}