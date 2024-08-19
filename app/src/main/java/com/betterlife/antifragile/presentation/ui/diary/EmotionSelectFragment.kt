package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.llm.DiaryAnalysisData
import com.betterlife.antifragile.data.model.emoticontheme.EmotionSelectData
import com.betterlife.antifragile.databinding.FragmentEmotionSelectBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.adapter.EmotionSelectAdapter
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmotionSelectViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmotionSelectViewModelFactory
import com.betterlife.antifragile.presentation.util.CustomToolbar

class EmotionSelectFragment : BaseFragment<FragmentEmotionSelectBinding>(
    R.layout.fragment_emotion_select
) {

    private lateinit var emotionSelectViewModel: EmotionSelectViewModel
    private lateinit var emotionSelectAdapter: EmotionSelectAdapter
    private lateinit var selectedEmotion: EmotionSelectData
    private lateinit var emoticonThemeId: String
    private lateinit var diaryAnalysisData: DiaryAnalysisData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        setupViewModel()
        setupRecyclerView()
        setupObservers(getEmotionFromArguments().name)
        loadEmoticonData()
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("나의 감정티콘")
            showBackButton {
                findNavController().popBackStack()
            }
            showLine()
        }
    }

    private fun setVariables() {
        diaryAnalysisData =
            EmotionSelectFragmentArgs.fromBundle(requireArguments()).diaryAnalysisData
        emoticonThemeId = EmotionSelectFragmentArgs.fromBundle(requireArguments()).emoticonThemeId
    }

    private fun setupViewModel() {
        val factory = EmotionSelectViewModelFactory(requireContext())
        emotionSelectViewModel = factory.create(EmotionSelectViewModel::class.java)
    }

    private fun setupRecyclerView() {
        binding.loSelectEmoticon.layoutManager = GridLayoutManager(context, 5)
    }

    private fun setupObservers(initialEmotion: String) {
        setupBaseObserver(
            liveData = emotionSelectViewModel.emoticonResponse,
            onSuccess = { response ->
                val sortedEmoticons = response.sortedBy { emotion ->
                    emotion.emotionEnum.ordinal
                }
                emotionSelectAdapter = EmotionSelectAdapter(sortedEmoticons, { selectedEmoticon ->
                    selectedEmotion = selectedEmoticon
                }, initialEmotion)
                binding.loSelectEmoticon.adapter = emotionSelectAdapter
            },
            onError = {
                showCustomToast(it.errorMessage ?: "감정티콘 테마의 감정티콘 조회에 실패했습니다.")
            }
        )
    }

    private fun setupButton() {
        binding.btnSave.setOnClickListener {
            findNavController().navigate(
                EmotionSelectFragmentDirections.actionNavEmotionSelectToNavEmoticonRecommend(
                    diaryAnalysisData,
                    selectedEmotion.emotionEnum,
                    emoticonThemeId,
                    getIsUpdateFromArguments()
                )
            )
        }
    }

    private fun loadEmoticonData() {
        emotionSelectViewModel.getEmoticons(emoticonThemeId)
    }

    private fun getEmotionFromArguments() =
        EmotionSelectFragmentArgs.fromBundle(requireArguments()).emotion

    private fun getIsUpdateFromArguments() =
        EmotionSelectFragmentArgs.fromBundle(requireArguments()).isUpdate
}