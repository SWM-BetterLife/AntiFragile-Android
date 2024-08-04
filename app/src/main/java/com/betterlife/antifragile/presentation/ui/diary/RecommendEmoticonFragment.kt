package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.diary.llm.DiaryAnalysisData
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.request.Emoticon
import com.betterlife.antifragile.data.model.emoticontheme.response.EmoticonByEmotion
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.data.repository.EmoticonThemeRepository
import com.betterlife.antifragile.databinding.FragmentRecommendEmoticonBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.adapter.EmoticonByEmotionAdapter
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.RecommendEmoticonViewModelFactory
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.RecommendEmoticonViewModel
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import kotlin.math.abs

class RecommendEmoticonFragment : BaseFragment<FragmentRecommendEmoticonBinding>(
    R.layout.fragment_recommend_emoticon
) {

    private lateinit var recommendEmoticonViewModel: RecommendEmoticonViewModel
    private lateinit var emoticonAdapter: EmoticonByEmotionAdapter
    private lateinit var viewPager: ViewPager2
    private var diaryDate: String? = null
    private var emotion: Emotion? = null
    private var selectedPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryAnalysisData = getDiaryAnalysisData()
        diaryDate = diaryAnalysisData.diaryDate

        setupViewModels()
        setupObservers()
        setupViewPager()
        setupButtons()

        emotion = getEmotion()

        if (emotion == Emotion.NOT_SELECTED) {
            // TODO: diaryAnalysisData의 Emotions로 감정티콘 추천
            emotion = Emotion.JOY
        }

        recommendEmoticonViewModel.getEmoticonsByEmotion(emotion!!.name)
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

    private fun setupViewModels() {
        // TODO: 로그인 구현 후, preference나 다른 방법으로 token을 받아와야 함
        val token = Constants.TOKEN
        val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
        val diaryAnalysisRepository = DiaryAnalysisRepository(diaryAnalysisApiService)
        val emoticonThemeApiService = RetrofitInterface.createEmoticonThemeApiService(token)
        val emoticonThemeRepository = EmoticonThemeRepository(emoticonThemeApiService)
        val factory = RecommendEmoticonViewModelFactory(
            diaryAnalysisRepository, emoticonThemeRepository
        )
        recommendEmoticonViewModel =
            ViewModelProvider(this, factory).get(RecommendEmoticonViewModel::class.java)
    }

    private fun setupObservers() {
        setStatusDiaryAnalysisSave()
        setStatusEmoticonByEmotion()
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
                    val pageWidth = width
                    val pageHeight = height
                    val scaleFactor = when {
                        position < -1 || position > 1 -> 0.7f
                        position == 0f -> 1f
                        else -> 0.7f + (1 - 0.7f) * (1 - abs(position))
                    }

                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                    val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horizontalMargin - verticalMargin / 2
                    } else {
                        -horizontalMargin + verticalMargin / 2
                    }
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    selectedPosition = position
                    updateNavigationButtons()
                }
            })
        }

        (viewPager.getChildAt(0) as? RecyclerView)?.apply {
            val padding = resources.getDimensionPixelOffset(R.dimen.viewpager_padding)
            setPadding(padding, 0, padding, 0)
            clipToPadding = false
        }

        updateNavigationButtons()
    }

    private fun setupButtons() {
        binding.btnLeft.setOnClickListener {
            if (selectedPosition > 0) {
                viewPager.setCurrentItem(selectedPosition - 1, true)
            }
        }

        binding.btnRight.setOnClickListener {
            if (selectedPosition < emoticonAdapter.itemCount - 1) {
                viewPager.setCurrentItem(selectedPosition + 1, true)
            }
        }

        binding.btnSave.setOnClickListener {
            saveDiaryAnalysis()
        }

        binding.btnChooseSelf.setOnClickListener {
            navigateToEmotionSelect()
        }
    }


    private fun loadEmoticons(emotion: Emotion) {
        recommendEmoticonViewModel.getEmoticonsByEmotion(emotion.name)
    }

    private fun saveDiaryAnalysis() {
        val selectedEmoticon = emoticonAdapter.getSelectedEmoticon(selectedPosition)
        val diaryAnalysisData = getDiaryAnalysisData()
        val emoticon = Emoticon(
            emoticonThemeId = selectedEmoticon.emoticonThemeId,
            emotion = emotion!!.name
        )
        val request = createDiaryAnalysisRequest(diaryAnalysisData, emoticon)
        recommendEmoticonViewModel.saveDiaryAnalysis(request)

        val action = RecommendEmoticonFragmentDirections
            .actionNavEmoticonRecommendToNavRecommendContent(diaryDate!!, true)
        findNavController().navigate(action)
    }

    private fun navigateToEmotionSelect() {
        val action = RecommendEmoticonFragmentDirections.actionNavEmoticonRecommendToNavEmotionSelect(
            emoticonThemeId = emoticonAdapter.getSelectedEmoticon(selectedPosition).emoticonThemeId,
            emotion = emotion!!,
            diaryAnalysisData = getDiaryAnalysisData()
        )
        findNavController().navigate(action)
    }

    private fun updateNavigationButtons() {
        binding.btnLeft.visibility = if (selectedPosition > 0) View.VISIBLE else View.INVISIBLE
        binding.btnRight.visibility = if (selectedPosition < emoticonAdapter.itemCount - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun getDiaryAnalysisData() =
        RecommendEmoticonFragmentArgs.fromBundle(requireArguments()).diaryAnalysisData

    private fun getEmotion() =
        RecommendEmoticonFragmentArgs.fromBundle(requireArguments()).emotion

    private fun setStatusDiaryAnalysisSave() {
        recommendEmoticonViewModel.saveDiaryResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }

                Status.SUCCESS -> {
                    dismissLoading()
                }

                Status.FAIL, Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "감정 분석 저장에 실패했습니다.")
                }

                else -> {
                    Log.d(
                        "DiaryRecommendEmoticonFragment",
                        "setStatusDiaryAnalysisSave: ${response.status}"
                    )
                }
            }
        }
    }

    private fun setStatusEmoticonByEmotion() {
        recommendEmoticonViewModel.emoticonResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }

                Status.SUCCESS -> {
                    dismissLoading()
                    response.data?.let { emoticonData ->
                        val emoticons = emoticonData.emoticons.map {
                            EmoticonByEmotion(it.emoticonThemeId, it.imgUrl)
                        }
                        emoticonAdapter.updateEmoticons(emoticons)
                        updateNavigationButtons()
                    }
                }

                Status.FAIL, Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "감정티콘 조회에 실패했습니다.")
                }

                else -> {
                    Log.d(
                        "DiaryRecommendEmoticonFragment",
                        "setStatusEmoticonByEmotion: ${response.status}"
                    )
                }
            }
        }
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
}