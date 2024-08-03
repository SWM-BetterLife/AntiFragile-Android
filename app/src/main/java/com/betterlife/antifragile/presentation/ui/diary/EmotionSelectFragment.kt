package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonThemeEmoticon
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import com.betterlife.antifragile.databinding.FragmentEmotionSelectBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmotionSelectViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmotionSelectViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class EmotionSelectFragment : BaseFragment<FragmentEmotionSelectBinding>(
    R.layout.fragment_emotion_select
) {

    private lateinit var emotionSelectViewModel: EmotionSelectViewModel
    private lateinit var emotionSelectAdapter: EmotionSelectAdapter
    private var diaryDate: String? = null
    private lateinit var selectedEmotion: EmoticonThemeEmoticon

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryDate = getDiaryDateFromArguments()
        val emoticonThemeId = getEmoticonThemeIdFromArguments()
        val initialEmotion = getEmotionFromArguments().name

        setupViewModel()
        setupRecyclerView()
        setupObservers(emoticonThemeId, initialEmotion)

        binding.btnSave.setOnClickListener {
            // TODO: 감정 선택 완료 버튼 클릭 처리
        }
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate!!))
            showBackButton(true) {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupViewModel() {
        val token = Constants.TOKEN
        val emoticonThemeApiService = RetrofitInterface.createEmoticonThemeApiService(token)
        val emoticonThemeRepository = EmoticonThemeRepository(emoticonThemeApiService)
        val factory = EmotionSelectViewModelFactory(emoticonThemeRepository)
        emotionSelectViewModel = ViewModelProvider(this, factory)[EmotionSelectViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.loSelectEmoticon.layoutManager = GridLayoutManager(context, 5)
    }

    private fun setupObservers(emoticonThemeId: String, initialEmotion: String) {
        emotionSelectViewModel.emoticonResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    response.data?.let { emoticons ->
                        val sortedEmoticons = emoticons.emoticons.sortedBy { emotion ->
                            Emotion.valueOf(emotion.emotion).ordinal
                        }
                        emotionSelectAdapter = EmotionSelectAdapter(sortedEmoticons, { selectedEmoticon ->
                            selectedEmotion = selectedEmoticon
                        }, initialEmotion)
                        binding.loSelectEmoticon.adapter = emotionSelectAdapter
                    }
                }
                Status.FAIL, Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "감정티콘 테마의 감정티콘 조회에 실패했습니다.")
                }
                else -> {
                    Log.d("EmotionSelectFragment", "Unknown status: ${response.status}")
                }
            }
        }
        emotionSelectViewModel.getEmoticons(emoticonThemeId)
    }

    private fun getDiaryDateFromArguments(): String {
        return EmotionSelectFragmentArgs.fromBundle(requireArguments()).diaryDate
    }

    private fun getEmoticonThemeIdFromArguments(): String {
        return EmotionSelectFragmentArgs.fromBundle(requireArguments()).emoticonThemeId
    }

    private fun getEmotionFromArguments(): Emotion {
        return EmotionSelectFragmentArgs.fromBundle(requireArguments()).emotion
    }

}