package com.betterlife.antifragile.config

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File

class LLMTask(context: Context) {
    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var llmInference: LlmInference

    init {
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(getModelPath(context))  // 기존 하드코딩된 경로 대신 동적으로 경로를 설정하도록 변경
            .setMaxTokens(2048)
            .setTopK(50)
            .setTemperature(0.7f)
            .setRandomSeed(1)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }.build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    private fun getModelPath(context: Context): String {
        val modelFile = File(context.filesDir, "llm/gemma2b-cpu.bin") // 모델이 저장된 경로를 내부 저장소로 설정
        return modelFile.absolutePath
    }

    fun generateResponse(prompt: String): String? {
        return llmInference.generateResponse(prompt)
    }

    fun generateResponseAsync(prompt: String) {
        llmInference.generateResponseAsync(prompt)
    }

    companion object {
        private var instance: LLMTask? = null
        fun getInstance(context: Context): LLMTask {
            return if (instance != null) {
                instance!!
            } else {
                LLMTask(context).also {
                    instance = it
                }
            }
        }
    }
}