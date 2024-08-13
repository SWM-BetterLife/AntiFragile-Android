package com.betterlife.antifragile.data.repository

import android.content.Context
import android.util.Log
import com.betterlife.antifragile.config.LLMTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class LLMRepository(context: Context) : BaseRepository() {
    private val llmTask= LLMTask.getInstance(context)

    suspend fun getResponseFromLLMInference(prompt: String): String? {
        return try {
            val result: String?
            val time = measureTimeMillis {
                result = withContext(Dispatchers.IO) {
                    llmTask.generateResponse(getPromptWithChatTemplate(prompt))
                }
            }
            Log.d("LLMRepository", "LLM Task took $time ms.")
            result
        } catch (e: Exception) {
            Log.e("LLMRepository", e.localizedMessage)
            null
        } finally {
            Log.d("LLMRepository", "LLM Inference is done.")
        }
    }

    private fun getPromptWithChatTemplate(prompt: String): String {
        return "<bos><start_of_turn>$prompt<end_of_turn>\n<start_of_turn>model\n"
    }
}