package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import com.betterlife.antifragile.data.repository.TextDiaryRepository
import kotlinx.coroutines.launch

class TextDiaryViewModel(
    private val textDiaryRepository: TextDiaryRepository
) : ViewModel() {

    private val _textDiaryDetail = MutableLiveData<BaseResponse<TextDiaryDetail>>()
    val textDiaryDetail: LiveData<BaseResponse<TextDiaryDetail>> = _textDiaryDetail


    init {
        _textDiaryDetail.value = BaseResponse(Status.INIT, null, null)
    }

    fun getTextDiaryDetail(id: Int, date: String) {
        viewModelScope.launch {
            _textDiaryDetail.value = BaseResponse(Status.LOADING, null, null)
            val response = textDiaryRepository.getTextDiaryDetailData(id, date)
            _textDiaryDetail.postValue(response)
        }
    }
}