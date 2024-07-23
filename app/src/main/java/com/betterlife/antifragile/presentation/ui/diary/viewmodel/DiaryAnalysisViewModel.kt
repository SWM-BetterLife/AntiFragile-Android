package com.betterlife.antifragile.presentation.ui.diary.viewmodel

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

class DiaryAnalysisViewModel(private val repository: DiaryAnalysisRepository) : ViewModel() {

    // API 응답 상태를 LiveData로 관리
    val saveDiaryStatus = MutableLiveData<BaseResponse<Any?>>()

    init {
        saveDiaryStatus.value = BaseResponse(Status.INIT, null, null)
    }

    fun saveDiaryAnalysis(request: DiaryAnalysisCreateRequest) {
        viewModelScope.launch {
            try {
                val response = repository.saveDiaryAnalysis(request)
                saveDiaryStatus.postValue(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = parseErrorResponse(errorBody)
                saveDiaryStatus.postValue(BaseResponse(errorResponse.status, errorResponse.errorMessage, null))
            } catch (e: Exception) {
                saveDiaryStatus.postValue(BaseResponse(Status.ERROR, e.message, null))
            }
        }
    }
}

