package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonThemeEmoticonsResponse
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import kotlinx.coroutines.launch

class EmotionSelectViewModel(
    private val emoticonThemeRepository: EmoticonThemeRepository
) : ViewModel() {

    private val _emoticonResponse = MutableLiveData<BaseResponse<EmoticonThemeEmoticonsResponse>>()
    val emoticonResponse: LiveData<BaseResponse<EmoticonThemeEmoticonsResponse>> = _emoticonResponse

    init {
        _emoticonResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getEmoticons(emoticonThemeId: String) {
        viewModelScope.launch {
            _emoticonResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = emoticonThemeRepository.getEmoticonThemeEmoticons(emoticonThemeId)
            _emoticonResponse.postValue(response)
            _emoticonResponse.value = response
        }
    }
}