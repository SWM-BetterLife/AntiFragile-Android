package com.betterlife.antifragile.data.repository

import android.content.Context
import android.util.Log
import com.betterlife.antifragile.config.LLMTask
import com.betterlife.antifragile.data.model.common.LLMInferenceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class LLMRepository(context: Context) : BaseRepository() {
    private val llmTask= LLMTask.getInstance(context)

    suspend fun getResponseFromLLMInference(
        text: String, llmInferenceType: LLMInferenceType
    ): String? {
        return try {
            val result: String?
            val time = measureTimeMillis {
                result = withContext(Dispatchers.IO) {
                    when(llmInferenceType) {
                        LLMInferenceType.EMOTION -> {
                            llmTask.generateResponse(getPromptWithEmotionInference(text))
                        }
                        else -> {
                            llmTask.generateResponse(getPromptWithChatTemplate(text))
                        }
                    }
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

    private fun getPromptWithEmotionInference(text: String): String {
        val prompt =  "Request: Based on the diary below, identify the emotion that most closely matches one of the following: " +
                "JOY, PASSION, FLUTTER, NORMAL, AMAZEMENT, ANXIETY, PANIC, SAD, PAIN, DEPRESSION, JEALOUSY, ENNUI, FEAR, ANGER, FATIGUE. " +
                "The answer should be only one word, like provided in the following format: emotion\n" +
                "\n" +
                "\"오늘은 정말 힘든 하루였다. 아침부터 회사에서 실수를 연발해서 부장님께 혼도 많이 나고, 자존감이 바닥을 쳤다. " +
                "일을 제대로 못해서 팀원들한테도 미안하고, 스스로도 한심하다는 생각이 들었다. " +
                "순간적으로 '그만두고 싶다'는 생각이 강하게 들었지만, 이러지도 저러지도 못하는 내 자신이 답답했다.\"\n" +
                "\n" +
                "Response: PANIC\n" +
                "\n" +
                "Request: Based on the diary below, identify the emotion that most closely matches one of the following: " +
                "JOY, PASSION, FLUTTER, NORMAL, AMAZEMENT, ANXIETY, PANIC, SAD, PAIN, DEPRESSION, JEALOUSY, ENNUI, FEAR, ANGER, FATIGUE. " +
                "The answer should be only one word, like provided in the following format: emotion\n" +
                "\n" +
                text + "\n"
                "\n" +
                "Response: \n"
        return getPromptWithChatTemplate(prompt)
    }

    private fun getPromptWithChatTemplate(prompt: String): String {
        return "<bos><start_of_turn>$prompt<end_of_turn>\n<start_of_turn>model\n"
    }
}