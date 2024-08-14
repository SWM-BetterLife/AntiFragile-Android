package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betterlife.antifragile.data.model.common.LLMInferenceType
import com.betterlife.antifragile.data.repository.LLMRepository
import kotlinx.coroutines.launch

class LLMViewModel(
    private val llmRepository: LLMRepository
) : ViewModel() {

    private val _llmResponse = MutableLiveData<String?>()
    val llmResponse: LiveData<String?> get() = _llmResponse

    fun getResponseFromLLM(prompt: String, llmInferenceType: LLMInferenceType) {
        viewModelScope.launch {
            val response = llmRepository.getResponseFromLLMInference(prompt, llmInferenceType)
            _llmResponse.postValue(response)
        }
    }

}