package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.common.LLMInferenceType.EMOTION
import com.betterlife.antifragile.data.model.common.LLMInferenceType.SUMMATION
import com.betterlife.antifragile.data.model.llm.LlmInferenceResultData
import com.betterlife.antifragile.data.repository.LLMRepository
import kotlinx.coroutines.launch

class LLMViewModel(
    private val llmRepository: LLMRepository
) : ViewModel() {

    private val _llmResponse = MutableLiveData<LlmInferenceResultData?>()
    val llmResponse: LiveData<LlmInferenceResultData?> get() = _llmResponse

    fun getResponseFromLLM(prompt: String) {
        viewModelScope.launch {
            val emotion = llmRepository.getResponseFromLLMInference(prompt, EMOTION)
            val summation = llmRepository.getResponseFromLLMInference(prompt, SUMMATION)
            _llmResponse.postValue(LlmInferenceResultData(emotion, summation))
        }
    }

}