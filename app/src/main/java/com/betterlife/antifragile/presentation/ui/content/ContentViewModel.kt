package com.betterlife.antifragile.presentation.ui.content

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.content.response.ContentDetailResponse
import com.betterlife.antifragile.data.repository.ContentRepository
import kotlinx.coroutines.launch

class ContentViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _contentDetailResponse = MutableLiveData<BaseResponse<ContentDetailResponse>>()
    val contentDetailResponse = _contentDetailResponse

    init {
        _contentDetailResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getMemberDetail(contentId: String) {
        viewModelScope.launch {
            _contentDetailResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.getContentDetail(contentId)
            _contentDetailResponse.postValue(response)
        }
    }
}