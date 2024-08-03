package com.betterlife.antifragile.presentation.ui.diary.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.betterlife.antifragile.config.LLMTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class LLMViewModel(application: Application) : AndroidViewModel(application) {
    private val llmTask= LLMTask.getInstance(application)

    fun getResponseFromLLMInference(prompt: String) {
        val TAG = "LLMViewModel"    //todo: remove

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val time = measureTimeMillis {
                    val result = async { llmTask.generateResponse(getPromptWithChatTemplate(prompt)) }
                    result.await()?.let { Log.i(TAG, "LLM Response : $it") }
                }
                Log.d(TAG, "LLM Task took $time ms.")
            } catch (e: Exception) {
                Log.e(TAG, e.localizedMessage)
            }
        }
        Log.d(TAG, "Method is done.")

    }

    private fun getPromptWithChatTemplate(prompt: String): String {
        return "<bos><start_of_turn>$prompt<end_of_turn>\n<start_of_turn>model\n"
    }

}