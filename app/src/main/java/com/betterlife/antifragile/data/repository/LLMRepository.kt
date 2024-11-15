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
        text: String,
        llmInferenceType: LLMInferenceType
    ): String? {
        return try {
            val result: String?
            val time = measureTimeMillis {
                result = withContext(Dispatchers.IO) {
                    when(llmInferenceType) {
                        LLMInferenceType.EMOTION -> {
                            val promptOfEmotion = getPromptWithEmotionInference(text)
                            llmTask.generateResponse(promptOfEmotion)
                        }
                        LLMInferenceType.SUMMATION -> {
                            val promptOfSummation = getPromptWithSummarizeInference(text)
                            llmTask.generateResponse(getPromptWithChatTemplate(promptOfSummation))
                        } else -> {
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

    private fun getPromptWithSummarizeInference(text: String): String {
        val prompt =  "Request: 다음 글을 요약해주세요: \n\n" +
                text + "\n"
        return getPromptWithChatTemplate(prompt)
    }

    private fun getPromptWithEmotionInference(text: String): String {
        val prompt =  "Request: Based on the diary below, identify the emotion that most closely matches one of the following: " +
            "JOY, PASSION, FLUTTER, NORMAL, AMAZEMENT, ANXIETY, PANIC, SAD, PAIN, DEPRESSION, JEALOUSY, ENNUI, FEAR, ANGER, FATIGUE. " +
            "The answer should be only one word, like provided in the following format: emotion\n" +
            "\n" +
            "\"오늘은 정말 무서웠다. 실수로 중요한 파일을 삭제했는데, 복구할 수 없는 건 아닌지 순간 너무 당황했다.\"\n" +
            "\n" +
            "Response: PANIC\n" +
            "\n" +
            "\"오늘은 너무 즐거웠다! 오랜만에 친구들과 만나 즐겁게 수다도 떨고 행복한 시간을 보냈다.\"\n" +
            "\n" +
            "Response: JOY\n" +
            "\n" +
            "\"정말 열정이 넘친다. 내 꿈에 한 발짝 더 가까워진 것 같아 정말 설렌다.\"\n" +
            "\n" +
            "Response: PASSION\n" +
            "\n" +
            "\"오늘 그 사람과 마주쳤는데 설렜다. 잠깐 대화도 나누었는데, 계속 웃음이 나는 걸 참기 어려웠다.\"\n" +
            "\n" +
            "Response: FLUTTER\n" +
            "\n" +
            "\"오늘 하루는 그저 평범했다. 별다른 일 없이 조용히 지나간 하루.\"\n" +
            "\n" +
            "Response: NORMAL\n" +
            "\n" +
            "\"오늘 갔던 전시회가 정말 대단했다! 예술이 이렇게 사람에게 감동을 줄 수 있다는 게 놀라웠다.\"\n" +
            "\n" +
            "Response: AMAZEMENT\n" +
            "\n" +
            "\"마음이 불안하다. 잘할 수 있을지 확신이 안 서서 두렵다.\"\n" +
            "\n" +
            "Response: ANXIETY\n" +
            "\n" +
            "\"슬프다. 오랜만에 친구랑 연락했는데, 좋은 추억들이 점점 옅어지는 것 같아 슬프다.\"\n" +
            "\n" +
            "Response: SAD\n" +
            "\n" +
            "\"오늘 아침부터 머리가 너무 아팠다. 아무리 쉬어도 나아지지 않아서 정말 고통스럽다.\"\n" +
            "\n" +
            "Response: PAIN\n" +
            "\n" +
            "\"요즘 들어서 모든 게 무의미하게 느껴진다. 아무런 의욕도 없고 그냥 하루하루를 겨우 버티는 느낌이다.\"\n" +
            "\n" +
            "Response: DEPRESSION\n" +
            "\n" +
            "\"부럽다. 친구가 좋은 회사에 취업했다는 소식을 들었다.\"\n" +
            "\n" +
            "Response: JEALOUSY\n" +
            "\n" +
            "\"오늘은 지루했다. 특별히 할 일도 없고, 그냥 무기력하게 하루를 보냈다.\"\n" +
            "\n" +
            "Response: ENNUI\n" +
            "\n" +
            "\"오늘은 무서웠다. 어두운 길을 걷다가 누군가 뒤따라오는 것 같았다.\"\n" +
            "\n" +
            "Response: FEAR\n" +
            "\n" +
            "\"오늘은 화가 났다. 동료가 나에게 무례하게 대했다.\"\n" +
            "\n" +
            "Response: ANGER\n" +
            "\n" +
            "\"요즘 너무 피곤하다. 계속 이렇게 일해야 한다는 생각에 지친다.\"\n" +
            "\n" +
            "Response: FATIGUE\n" +
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