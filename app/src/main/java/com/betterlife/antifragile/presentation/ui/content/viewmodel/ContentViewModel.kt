package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.content.response.ContentListResponse
import com.betterlife.antifragile.data.repository.ContentRepository
import kotlinx.coroutines.launch

class ContentViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _contentListResponse = MutableLiveData<BaseResponse<ContentListResponse>>()
    val contentListResponse = _contentListResponse

    init {
        _contentListResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getContentList(date: String) {
        viewModelScope.launch {
            _contentListResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.getContents(date)
            _contentListResponse.postValue(response)
        }
    }
}