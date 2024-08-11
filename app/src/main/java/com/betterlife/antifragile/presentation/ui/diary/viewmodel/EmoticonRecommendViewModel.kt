package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonsByEmotionResponse
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import kotlinx.coroutines.launch

class EmoticonRecommendViewModel(
    private val diaryAnalysisRepository: DiaryAnalysisRepository,
    private val emoticonThemeRepository: EmoticonThemeRepository
) : ViewModel() {

    private val _emoticonResponse = MutableLiveData<BaseResponse<EmoticonsByEmotionResponse>>()
    val emoticonResponse: LiveData<BaseResponse<EmoticonsByEmotionResponse>> = _emoticonResponse

    private val _saveDiaryResponse = MutableLiveData<BaseResponse<Any?>>()
    val saveDiaryResponse: LiveData<BaseResponse<Any?>> = _saveDiaryResponse

    init {
        _emoticonResponse.value = BaseResponse(Status.INIT, null, null)
        _saveDiaryResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getEmoticonsByEmotion(emotion: String) {
        viewModelScope.launch {
            _emoticonResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = emoticonThemeRepository.getEmoticonByEmotion(emotion)
            _emoticonResponse.postValue(response)
            _emoticonResponse.value = response
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
            _saveDiaryResponse.value = response
        }
    }

    private fun updateDiaryAnalysis(date: String, request: DiaryAnalysisCreateRequest) {
        viewModelScope.launch {
            _saveDiaryResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = diaryAnalysisRepository.updateDiaryAnalysis(date, request)
            _saveDiaryResponse.postValue(response)
            _saveDiaryResponse.value = response
        }
    }
}

