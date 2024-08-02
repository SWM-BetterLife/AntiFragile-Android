package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.data.model.diary.llm.DiaryAnalysisData
import com.betterlife.antifragile.databinding.FragmentEmotionAnalysisBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.LLMViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.LLMViewModelFactory

class EmotionAnalysisFragment : BaseFragment<FragmentEmotionAnalysisBinding>(R.layout.fragment_emotion_analysis){

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var llmViewModel: LLMViewModel
    private var textDiary: TextDiary? = null
    private var questionDiary: QuestionDiary? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryId = getDiaryIdFromArguments()
        val diaryType = getDiaryTypeFromArguments()
        val diaryAnalysisData: DiaryAnalysisData

        // ID가 음수인 경우 예외 처리
        if (diaryId < 0) {
            showCustomToast("잘못된 데이터입니다. 이전 화면으로 돌아갑니다.")
            findNavController().popBackStack()
        }

        setupViewModels()

        // 로컬 DB에 저장된 텍스트 일기 또는 질문 일기 조회
        if (diaryType == "TEXT") {
            getTextDiary(diaryId) { retrievedTextDiary ->
                textDiary = retrievedTextDiary
                if (textDiary == null) {
                    showCustomToast("일기를 불러오지 못했습니다. 이전 화면으로 돌아갑니다.")
                    findNavController().popBackStack()
                    return@getTextDiary
                }
                Log.d("EmotionAnalysisFragment", "$textDiary")
                val diaryAnalysisData = createDiaryAnalysisData(textDiary?.date ?: "")

                setupSaveButton(diaryAnalysisData)

            }
        } else if (diaryType == "QUESTION"){
            getQuestionDiary(diaryId) { retrievedQuestionDiary ->
                questionDiary = retrievedQuestionDiary
                if (questionDiary == null) {
                    showCustomToast("일기를 불러오지 못했습니다. 이전 화면으로 돌아갑니다.")
                    findNavController().popBackStack()
                    return@getQuestionDiary
                }
                val diaryAnalysisData = DiaryAnalysisData(
                    emotions = questionDiary?.emotions ?: arrayListOf(),
                    event = questionDiary?.event ?: "",
                    thought = questionDiary?.thought ?: "",
                    action = questionDiary?.action ?: "",
                    comment = questionDiary?.comment ?: "",
                    diaryDate = questionDiary?.date ?: ""
                )

                setupSaveButton(diaryAnalysisData)
            }
        } else {
            showCustomToast("잘못된 diaryType입니다. 이전 화면으로 돌아갑니다.")
            findNavController().popBackStack()
            return
        }

    }

    private fun getDiaryIdFromArguments() =
        EmotionAnalysisFragmentArgs.fromBundle(requireArguments()).diaryId

    private fun getDiaryTypeFromArguments() =
        EmotionAnalysisFragmentArgs.fromBundle(requireArguments()).diaryType

    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this, DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)

        llmViewModel = ViewModelProvider(
            this, LLMViewModelFactory(requireActivity().application)
        ).get(LLMViewModel::class.java) //todo: App 처음 로딩 시, llmTask Instance 생성하는 로직으로 변경
    }

    // 로컬 db에 저장된 텍스트 일기 조회
    private fun getTextDiary(diaryId: Int, callback: (TextDiary?) -> Unit) {
        diaryViewModel.getTextDiaryById(diaryId).observe(viewLifecycleOwner) { retrievedDiary ->
            callback(retrievedDiary)
            Log.d("EmotionAnalysisFragment", "Retrieved Diary: $textDiary")
        }
    }

    private fun getQuestionDiary(diaryId: Int, callback: (QuestionDiary?) -> Unit) {
        diaryViewModel.getQuestionDiaryById(diaryId).observe(viewLifecycleOwner) { retrievedDiary ->
            callback(retrievedDiary)
            Log.d("EmotionAnalysisFragment", "Retrieved QuestionDiary: $retrievedDiary")
        }
    }

    private fun createDiaryAnalysisData(date: String): DiaryAnalysisData {
        val prompt = "\"Today was a really busy day. In the morning, I had to rush to prepare for my classes, and I felt so frazzled. During the lectures, it was really hard for me to stay focused on the professor's lessons. During lunchtime, I was able to take a quick break and have a meal with my friends, but in the afternoon, I had to dive back into working on my assignments. I was so tired, but I felt proud of myself for diligently completing the assignments.\n" +
                "\n" +
                "When I came home and was able to rest, my mood improved. The various emotions I experienced throughout the day were complex, but ultimately, a sense of accomplishment and satisfaction were the most prominent feelings. I hope tomorrow I can have a more leisurely routine. I'm happy to be growing day by day through my university life.\"\n" +
                "\n" +
                "The content above is my diary entry for today. Summarize the content above."
        Log.i("createDiaryAnalysisData", date)

        llmViewModel.getResponseFromLLMInference(prompt)

        return DiaryAnalysisData(
            emotions = listOf("wwwww", "감정2"),
            event = "사건",
            thought = "생각",
            action = "행동",
            comment = "다짐",
            diaryDate = date
        )
    }

    private fun setupSaveButton(diaryAnalysisData: DiaryAnalysisData) {
        binding.btnSave.setOnClickListener {
            // 값 전달 및 Fragment 전환
            val action = EmotionAnalysisFragmentDirections
                .acitonNavEmotionAnalysisToNavEmoticonRecommend(diaryAnalysisData)
            findNavController().navigate(action)
        }
    }
}