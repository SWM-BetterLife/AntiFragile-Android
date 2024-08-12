package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.diary.llm.DiaryAnalysisData
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.request.Emoticon
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonByEmotion
import com.betterlife.antifragile.databinding.FragmentEmoticonRecommendBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.adapter.EmoticonByEmotionAdapter
import com.betterlife.antifragile.presentation.ui.diary.handler.EmoticonRecommendViewPagerHandler
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmoticonRecommendViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmoticonRecommendViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.RecommendDialogUtil

class EmoticonRecommendFragment : BaseFragment<FragmentEmoticonRecommendBinding>(
    R.layout.fragment_emoticon_recommend
) {

    private lateinit var recommendEmoticonViewModel: EmoticonRecommendViewModel
    private lateinit var emoticonAdapter: EmoticonByEmotionAdapter
    private lateinit var viewPagerHandler: EmoticonRecommendViewPagerHandler
    private lateinit var diaryAnalysisData: DiaryAnalysisData
    private lateinit var emotion: Emotion
    private var selectedPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVariables()
        setupViewModels()
        setupViewPager()
        setupObservers()
        loadEmoticonData()
        setupButtons()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryAnalysisData.diaryDate))
            showBackButton {
                findNavController().popBackStack()
            }
            showLine()
        }
    }

    private fun setupVariables() {
        diaryAnalysisData =
            EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).diaryAnalysisData
        emotion = EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).emotion
    }

    private fun setupViewModels() {
        val factory = EmoticonRecommendViewModelFactory(Constants.TOKEN)
        recommendEmoticonViewModel =
            ViewModelProvider(this, factory)[EmoticonRecommendViewModel::class.java]
    }

    private fun setupObservers() {
        setupBaseObserver(
            liveData = recommendEmoticonViewModel.saveDiaryResponse,
            onSuccess = { },
            onError = { handleSaveError(it.errorMessage) }
        )

        setupBaseObserver(
            liveData = recommendEmoticonViewModel.emoticonResponse,
            onSuccess = { response ->
                val emoticons = response.emoticons.map {
                    EmoticonByEmotion(it.emoticonThemeId, it.imgUrl)
                }
                emoticonAdapter.updateEmoticons(emoticons)
                updateNavigationButtons()
            },
            onError = {
                showCustomToast(it.errorMessage ?: "감정티콘 조회에 실패했습니다.")
            }
        )

        setupBaseObserver(
            liveData = recommendEmoticonViewModel.remainRecommendNumber,
            onSuccess = { response ->
                RecommendDialogUtil.showRecommendDialogs(
                    fragment = this,
                    remainNumber = response.remainNumber,
                    onLeftButtonClicked = {
                        navigateToRecommendContent(false, null)
                    },
                    onRightButtonFeedbackProvided = { feedback ->
                        navigateToRecommendContent(false, feedback)
                    },
                    onExcessRemainNumber = {
                        navigateToRecommendContent(false, null)
                    }
                )
            },
            onError = {
                showCustomToast(it.errorMessage ?: "남은 추천 횟수 조회에 실패했습니다.")
            }
        )
    }

    private fun setupViewPager() {
        viewPagerHandler = EmoticonRecommendViewPagerHandler(
            viewPager = binding.vpSelectEmotion,
            onUpdateNavigationButtons = { updateNavigationButtons() },
            viewPagerPadding = resources.getDimensionPixelOffset(R.dimen.viewpager_padding)
        )

        emoticonAdapter = EmoticonByEmotionAdapter { position ->
            viewPagerHandler.onPageSelected(position)
        }

        viewPagerHandler.setAdapter(emoticonAdapter)
    }

    private fun setupButtons() {
        binding.btnLeft.setOnClickListener { viewPagerHandler.moveToPreviousPage() }
        binding.btnRight.setOnClickListener { viewPagerHandler.moveToNextPage() }
        binding.btnSave.setOnClickListener { saveDiaryAnalysis() }
        binding.btnChooseSelf.setOnClickListener { navigateToEmotionSelect() }
    }

    private fun loadEmoticonData() {
        recommendEmoticonViewModel.getEmoticonsByEmotion(emotion.name)
    }

    private fun saveDiaryAnalysis() {
        val selectedEmoticon = emoticonAdapter.getSelectedEmoticon(selectedPosition)
        val request = createDiaryAnalysisRequest(
            diaryAnalysisData,
            Emoticon(selectedEmoticon.emoticonThemeId, emotion.name)
        )

        if (getIsUpdate()) {
            recommendEmoticonViewModel.getRemainRecommendNumber()
        } else {
            recommendEmoticonViewModel.saveDiaryAnalysis(request, null)
            navigateToRecommendContent(true, null)
        }
        (activity as MainActivity).showBottomNavigation()
    }

    private fun navigateToRecommendContent(isNewDiary: Boolean, feedback: String?) {
        val action = EmoticonRecommendFragmentDirections
            .actionNavEmoticonRecommendToNavRecommendContent(
                diaryAnalysisData.diaryDate, isNewDiary, feedback
            )
        findNavController().navigate(action)
    }

    private fun navigateToEmotionSelect() {
        findNavController().navigate(
            EmoticonRecommendFragmentDirections.actionNavEmoticonRecommendToNavEmotionSelect(
                emoticonThemeId = emoticonAdapter
                    .getSelectedEmoticon(selectedPosition).emoticonThemeId,
                emotion = emotion,
                diaryAnalysisData = diaryAnalysisData,
                getIsUpdate()
            )
        )
    }

    private fun updateNavigationButtons() {
        binding.btnLeft.visibility = if (selectedPosition > 0) View.VISIBLE else View.INVISIBLE
        binding.btnRight.visibility =
            if (selectedPosition < emoticonAdapter.itemCount - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun createDiaryAnalysisRequest(
        diaryAnalysisData: DiaryAnalysisData,
        emoticon: Emoticon
    ): DiaryAnalysisCreateRequest {
        return DiaryAnalysisCreateRequest(
            emotions = diaryAnalysisData.emotions,
            event = diaryAnalysisData.event,
            thought = diaryAnalysisData.thought,
            action = diaryAnalysisData.action,
            comment = diaryAnalysisData.comment,
            emoticon = emoticon
        )
    }

    private fun handleSaveError(errorMessage: String?) {
        when (errorMessage) {
            CustomErrorMessage.DIARY_ANALYSIS_ALREADY_EXIST.message ->
                showCustomToast("이미 해당 날짜에 사용자의 일기 분석이 존재합니다.")
            CustomErrorMessage.DIARY_ANALYSIS_NOT_FOUND.message ->
                showCustomToast("감정 분석 정보가 존재하지 않아 수정에 실패했습니다.")
            else -> showCustomToast(errorMessage ?: "감정 분석 저장에 실패했습니다.")
        }
    }

    // TODO: 이모티콘 테마에 따라 시작 viewpager position 설정
    private fun getEmoticonThemeId() =
        EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).emoticonThemeId

    private fun getIsUpdate() =
        EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).isUpdate
}