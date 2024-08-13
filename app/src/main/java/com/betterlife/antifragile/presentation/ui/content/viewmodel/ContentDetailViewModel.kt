package com.betterlife.antifragile.presentation.ui.content.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.content.response.ContentDetailResponse
import com.betterlife.antifragile.data.repository.ContentRepository
import kotlinx.coroutines.launch

class ContentDetailViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _contentDetailResponse = MutableLiveData<BaseResponse<ContentDetailResponse>>()
    val contentDetailResponse = _contentDetailResponse

    private val _contentLikeResponse = MutableLiveData<BaseResponse<Any?>>()
    val contentLikeResponse = _contentLikeResponse

    private var _likeNumber = MutableLiveData<Long>()
    val likeNumber = _likeNumber

    private var _isLiked = MutableLiveData<Boolean>()
    val isLiked = _isLiked

    init {
        _contentDetailResponse.value = BaseResponse(Status.INIT, null, null)
        _contentLikeResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getContentDetail(contentId: String) {
        viewModelScope.launch {
            _contentDetailResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.getContentDetail(contentId)
            _contentDetailResponse.postValue(response)

            _isLiked.value = response.data?.isLiked ?: false
            _likeNumber.value = response.data?.likeNumber ?: 0
        }
    }

    fun changeLikeState(contentId: String) {
        val currentLiked = _isLiked.value ?: false
        if (currentLiked) {
            unlikeContent(contentId)
        } else {
            likeContent(contentId)
        }
    }

    private fun likeContent(contentId: String) {
        viewModelScope.launch {
            _contentLikeResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.likeContent(contentId)
            _contentLikeResponse.postValue(response)

            _isLiked.value = true
            _likeNumber.value = _likeNumber.value?.plus(1)
        }
    }

    private fun unlikeContent(contentId: String) {
        viewModelScope.launch {
            _contentLikeResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = contentRepository.unlikeContent(contentId)
            _contentLikeResponse.postValue(response)

            _isLiked.value = false
            _likeNumber.value = _likeNumber.value?.minus(1)
        }
    }
}