package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonsByEmotionResponse
import com.betterlife.antifragile.data.model.member.response.MemberRemainNumberResponse
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import com.betterlife.antifragile.data.repository.MemberRepository
import kotlinx.coroutines.launch

class EmoticonRecommendViewModel(
    private val diaryAnalysisRepository: DiaryAnalysisRepository,
    private val emoticonThemeRepository: EmoticonThemeRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _emoticonResponse = MutableLiveData<BaseResponse<EmoticonsByEmotionResponse>>()
    val emoticonResponse = _emoticonResponse

    private val _saveDiaryResponse = MutableLiveData<BaseResponse<Any?>>()
    val saveDiaryResponse = _saveDiaryResponse

    private val _remainRecommendNumber = MutableLiveData<BaseResponse<MemberRemainNumberResponse>>()
    val remainRecommendNumber = _remainRecommendNumber

    init {
        _emoticonResponse.value = BaseResponse(Status.INIT, null, null)
        _saveDiaryResponse.value = BaseResponse(Status.INIT, null, null)
        _remainRecommendNumber.value = BaseResponse(Status.INIT, null, null)
    }

    fun getEmoticonsByEmotion(emotion: String) {
        viewModelScope.launch {
            _emoticonResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = emoticonThemeRepository.getEmoticonByEmotion(emotion)
            _emoticonResponse.postValue(response)
        }
    }

    fun saveDiaryAnalysis(request: DiaryAnalysisCreateRequest, date: String?) {

        if (date != null) {
            updateDiaryAnalysis(date, request)
            return
        }
        viewModelScope.launch {
            _saveDiaryResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = diaryAnalysisRepository.saveDiaryAnalysis(request)
            _saveDiaryResponse.postValue(response)
        }
    }

    private fun updateDiaryAnalysis(date: String, request: DiaryAnalysisCreateRequest) {
        viewModelScope.launch {
            _saveDiaryResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = diaryAnalysisRepository.updateDiaryAnalysis(date, request)
            _saveDiaryResponse.postValue(response)
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

