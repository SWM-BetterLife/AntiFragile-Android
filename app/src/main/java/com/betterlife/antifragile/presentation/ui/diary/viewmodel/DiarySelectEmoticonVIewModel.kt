package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.presentation.util.ApiErrorUtil.parseErrorResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

/*
 * UI 관련 데이터를 관리하고, View와 Model 사이의 커뮤니케이션을 담당하는 ViewModel
 */
class DiarySelectEmoticonVIewModel(private val repository: DiaryAnalysisRepository): ViewModel() {

    // API 응답 상태를 LiveData로 관리
    private val _saveDiaryStatus = MutableLiveData<BaseResponse<Any?>>()
    val saveDiaryStatus: LiveData<BaseResponse<Any?>> get() = _saveDiaryStatus

    // 일기 분석을 저장하는 함수
    fun saveDiaryAnalysis(request: DiaryAnalysisCreateRequest) {
        viewModelScope.launch {
            try {
                val response = repository.saveDiaryAnalysis(request)
                _saveDiaryStatus.postValue(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = parseErrorResponse(errorBody)
                _saveDiaryStatus.postValue(BaseResponse(errorResponse.status, errorResponse.errorMessage, null))
            } catch (e: Exception)  {
                _saveDiaryStatus.postValue(BaseResponse(Status.ERROR, e.message, null))
            }
        }
    }
}