package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
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
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmoticonRecommendViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.EmoticonRecommendViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import kotlin.math.abs

class EmoticonRecommendFragment : BaseFragment<FragmentEmoticonRecommendBinding>(
    R.layout.fragment_emoticon_recommend
) {

    private lateinit var recommendEmoticonViewModel: EmoticonRecommendViewModel
    private lateinit var emoticonAdapter: EmoticonByEmotionAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var diaryDate: String
    private lateinit var emotion: Emotion
    private var selectedPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVariables()
        setupViewModels()
        setupObservers()
        setupViewPager()
        setupButtons()

        recommendEmoticonViewModel.getEmoticonsByEmotion(emotion.name)
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate))
            showBackButton {
                findNavController().popBackStack()
            }
            showLine()
        }
    }

    private fun setupVariables() {
        diaryDate = getDiaryAnalysisData().diaryDate
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
            onError = { errorMessage ->
                if (errorMessage == CustomErrorMessage.DIARY_ANALYSIS_ALREADY_EXIST.message) {
                    // 일기 분석이 이미 존재하여 저장을 실패한 경우
                    showCustomToast("이미 해당 날짜에 사용자의 일기 분석이 존재합니다.")
                } else if (errorMessage == CustomErrorMessage.DIARY_ANALYSIS_NOT_FOUND.message) {
                    // 일기 분석이 존재하지 않아 수정을 실패한 경우
                    showCustomToast("감정 분석 정보가 존재하지 않아 수정에 실패했습니다.")
                } else {
                    showCustomToast(errorMessage ?: "감정 분석 저장에 실패했습니다.")
                }
            }
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
                showCustomToast(it ?: "감정티콘 조회에 실패했습니다.")
            }
        )
    }

    private fun setupViewPager() {
        emoticonAdapter = EmoticonByEmotionAdapter { position ->
            selectedPosition = position
            viewPager.setCurrentItem(position, true)
            updateNavigationButtons()
        }

        viewPager = binding.vpSelectEmotion.apply {
            adapter = emoticonAdapter
            offscreenPageLimit = 1
            setPageTransformer { page, position ->
                page.apply {
                    val scaleFactor = calculateScaleFactor(position)
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    translationX = calculateTranslationX(page, position, scaleFactor)
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    selectedPosition = position
                    updateNavigationButtons()
                }
            })
        }

        configureViewPagerPadding()
        updateNavigationButtons()
    }

    private fun setupButtons() {
        binding.btnLeft.setOnClickListener {
            viewPager.setCurrentItem(selectedPosition - 1, true)
        }

        binding.btnRight.setOnClickListener {
            viewPager.setCurrentItem(selectedPosition + 1, true)
        }

        binding.btnSave.setOnClickListener { saveDiaryAnalysis() }

        binding.btnChooseSelf.setOnClickListener { navigateToEmotionSelect() }
    }

    private fun loadEmoticons(emotion: Emotion) {
        recommendEmoticonViewModel.getEmoticonsByEmotion(emotion.name)
    }

    private fun saveDiaryAnalysis() {
        val selectedEmoticon = emoticonAdapter.getSelectedEmoticon(selectedPosition)
        val request = createDiaryAnalysisRequest(
            getDiaryAnalysisData(),
            Emoticon(selectedEmoticon.emoticonThemeId, emotion.name)
        )
        recommendEmoticonViewModel.saveDiaryAnalysis(
            request, if (getIsUpdate()) diaryDate else null
        )

        findNavController().navigate(
            EmoticonRecommendFragmentDirections.actionNavEmoticonRecommendToNavRecommendContent(
                diaryDate, true
            )
        )
        (activity as MainActivity).showBottomNavigation()
    }

    private fun navigateToEmotionSelect() {
        findNavController().navigate(
            EmoticonRecommendFragmentDirections.actionNavEmoticonRecommendToNavEmotionSelect(
                emoticonThemeId = emoticonAdapter
                    .getSelectedEmoticon(selectedPosition).emoticonThemeId,
                emotion = emotion,
                diaryAnalysisData = getDiaryAnalysisData(),
                getIsUpdate()
            )
        )
    }
    private fun configureViewPagerPadding() {
        (viewPager.getChildAt(0) as? RecyclerView)?.apply {
            val padding = resources.getDimensionPixelOffset(R.dimen.viewpager_padding)
            setPadding(padding, 0, padding, 0)
            clipToPadding = false
        }
    }

    private fun calculateScaleFactor(position: Float): Float {
        return when {
            position < -1 || position > 1 -> 0.7f
            position == 0f -> 1f
            else -> 0.7f + (1 - 0.7f) * (1 - abs(position))
        }
    }

    private fun calculateTranslationX(page: View, position: Float, scaleFactor: Float): Float {
        val verticalMargin = page.height * (1 - scaleFactor) / 2
        val horizontalMargin = page.width * (1 - scaleFactor) / 2
        return if (position < 0)
            horizontalMargin - verticalMargin / 2 else -horizontalMargin + verticalMargin / 2
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

    private fun getDiaryAnalysisData() =
        EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).diaryAnalysisData

    private fun getEmoticonThemeId() =
        EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).emoticonThemeId

    private fun getIsUpdate() =
        EmoticonRecommendFragmentArgs.fromBundle(requireArguments()).isUpdate
}