package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import kotlinx.coroutines.launch

class DiaryAnalysisViewModel(
    private val diaryAnalysisRepository: DiaryAnalysisRepository
) : ViewModel() {

    private val _saveDiaryResponse = MutableLiveData<BaseResponse<Any?>>()
    val saveDiaryResponse: LiveData<BaseResponse<Any?>> = _saveDiaryResponse

    init {
        _saveDiaryResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun saveDiaryAnalysis(request: DiaryAnalysisCreateRequest) {
        viewModelScope.launch {
            _saveDiaryResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = diaryAnalysisRepository.saveDiaryAnalysis(request)
            _saveDiaryResponse.postValue(response)
            _saveDiaryResponse.value = response
        }
    }
}

