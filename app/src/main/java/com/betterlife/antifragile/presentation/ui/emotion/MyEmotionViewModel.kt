package com.betterlife.antifragile.presentation.ui.emotion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.response.DiaryAnalysisDailyResponse
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import kotlinx.coroutines.launch

class MyEmotionViewModel(
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) : ViewModel(){

    private val _diaryAnalysisResponse = MutableLiveData<BaseResponse<DiaryAnalysisDailyResponse>>()
    val diaryAnalysisResponse = _diaryAnalysisResponse

    init {
        _diaryAnalysisResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getDailyDiaryAnalysis(date: String) {
        viewModelScope.launch {
            _diaryAnalysisResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = diaryAnalysisRepository.getDailyDiaryAnalysis(date)
            _diaryAnalysisResponse.postValue(response)
        }
    }

}