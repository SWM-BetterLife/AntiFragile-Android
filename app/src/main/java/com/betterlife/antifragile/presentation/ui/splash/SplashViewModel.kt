package com.betterlife.antifragile.presentation.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.base.BaseResponse
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.llm.LlmModelUrlResponse
import com.betterlife.antifragile.data.repository.LLMModelRepository
import kotlinx.coroutines.launch

class SplashViewModel(
    private val llmModelRepository: LLMModelRepository
) : ViewModel() {

    private val _llmModelUrl = MutableLiveData<BaseResponse<LlmModelUrlResponse>>()
    val llmModelUrl = _llmModelUrl

    init {
        _llmModelUrl.value = BaseResponse(Status.INIT, null, null)
    }

    fun getLlmModelUrl() {
        viewModelScope.launch {
            _llmModelUrl.value = BaseResponse(Status.LOADING, null, null)
            val response = llmModelRepository.getLlmModelUrl()
            _llmModelUrl.postValue(response)
        }
    }
}