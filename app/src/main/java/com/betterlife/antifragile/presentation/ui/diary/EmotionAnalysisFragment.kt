package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.common.LLMInferenceType.EMOTION
import com.betterlife.antifragile.data.model.diary.QuestionDiary
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.data.model.llm.DiaryAnalysisData
import com.betterlife.antifragile.databinding.FragmentEmotionAnalysisBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.customview.CustomLoadingDialog
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.LLMViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.LLMViewModelFactory
import com.betterlife.antifragile.presentation.util.CustomToolbar

class EmotionAnalysisFragment : BaseFragment<FragmentEmotionAnalysisBinding>(
    R.layout.fragment_emotion_analysis
){

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var llmViewModel: LLMViewModel
    private var textDiary: TextDiary? = null
    private var questionDiary: QuestionDiary? = null
    private var customLoadingDialog: CustomLoadingDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryId = getDiaryIdFromArguments()
        val diaryType = getDiaryTypeFromArguments()

        // ID가 음수인 경우 예외 처리
        if (diaryId < 0) {
            showCustomToast("잘못된 데이터입니다. 이전 화면으로 돌아갑니다.")
            findNavController().popBackStack()
        }

        setupViewModels()
        showLoading()
        setStatusLLMResponse()

        // 로컬 DB에 저장된 텍스트 일기 또는 질문 일기 조회
        if (diaryType == "TEXT") {
            getTextDiary(diaryId) { retrievedTextDiary ->
                textDiary = retrievedTextDiary
                if (textDiary == null) {
                    showCustomToast("일기를 불러오지 못했습니다. 이전 화면으로 돌아갑니다.")
                    findNavController().popBackStack()
                    return@getTextDiary
                }

                llmViewModel.getResponseFromLLM(textDiary?.content ?: "", EMOTION)
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

                moveToEmoticonRecommend(diaryAnalysisData)
            }
        } else {
            showCustomToast("잘못된 diaryType입니다. 이전 화면으로 돌아갑니다.")
            findNavController().popBackStack()
            return
        }

    }

    private fun setStatusLLMResponse() {
        setUpLLMObserver(
            liveData = llmViewModel.llmResponse,
            onSuccess = {
                if (it != null) {
                    Log.d("LLMViewModel", "LLM Response: $it")
                    val responseEmotion = Emotion.parseEmotionFromStr(it)
                    val diaryAnalysisData = createDiaryAnalysisData(
                        textDiary?.date ?: "", responseEmotion.toKorean
                    )
                    findNavController().navigate(
                        EmotionAnalysisFragmentDirections
                            .actionNavEmotionAnalysisToNavEmoticonRecommend(
                                diaryAnalysisData,
                                responseEmotion,
                                null,
                                getIsUpdateFromArguments()
                            )
                    )
                } else {
                    Log.e("LLMViewModel", "LLM Response is null")
                }
            }
        )
    }
    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
        }
    }

    private fun getDiaryIdFromArguments() =
        EmotionAnalysisFragmentArgs.fromBundle(requireArguments()).diaryId

    private fun getDiaryTypeFromArguments() =
        EmotionAnalysisFragmentArgs.fromBundle(requireArguments()).diaryType

    private fun getIsUpdateFromArguments() =
        EmotionAnalysisFragmentArgs.fromBundle(requireArguments()).isUpdate


    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this,
            DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)
        val llmViewModelFactory = LLMViewModelFactory(requireContext())
        llmViewModel = llmViewModelFactory.create(LLMViewModel::class.java)
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

    private fun showLoading() {
        customLoadingDialog = CustomLoadingDialog(
            context = requireContext(),
            emoticonResId = R.drawable.emoticon_smile,
            loadingTexts = arrayOf("감정 분석 중", "감정 분석 중.", "감정 분석 중..", "감정 분석 중...")
        )
        customLoadingDialog?.show()
    }

    private fun createDiaryAnalysisData(date: String, emotion: String): DiaryAnalysisData {

        return DiaryAnalysisData(
            emotions = listOf(emotion),
            event = "사건",
            thought = "생각",
            action = "행동",
            comment = "다짐",
            diaryDate = date
        )
    }

    private fun moveToEmoticonRecommend(diaryAnalysisData: DiaryAnalysisData) {
        val action = EmotionAnalysisFragmentDirections
            .actionNavEmotionAnalysisToNavEmoticonRecommend(
                diaryAnalysisData,
                Emotion.JOY,
                null,
                getIsUpdateFromArguments()
            )
        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        customLoadingDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customLoadingDialog?.dismiss()
    }

}