package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.emoticontheme.EmotionSelectData
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import kotlinx.coroutines.launch

class EmotionSelectViewModel(
    private val emoticonThemeRepository: EmoticonThemeRepository
) : ViewModel() {

    private val _emoticonResponse = MutableLiveData<BaseResponse<List<EmotionSelectData>>>()
    val emoticonResponse: LiveData<BaseResponse<List<EmotionSelectData>>> = _emoticonResponse

    init {
        _emoticonResponse.value = BaseResponse(Status.INIT, null, null)
    }

    fun getEmoticons(emoticonThemeId: String) {
        viewModelScope.launch {
            _emoticonResponse.value = BaseResponse(Status.LOADING, null, null)
            val response = emoticonThemeRepository.getEmoticonThemeEmoticons(emoticonThemeId)
            val transformedData = response.data?.emoticons?.map { emoticon ->
                EmotionSelectData(
                    emotion = emoticon.emotion,
                    imgUrl = emoticon.imgUrl,
                    emotionEnum = Emotion.entries.find { it.name == emoticon.emotion } ?: Emotion.ERROR
                )
            } ?: emptyList()
            _emoticonResponse.postValue(BaseResponse(response.status, response.errorMessage, transformedData))
        }
    }
}