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
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.databinding.FragmentDiaryRecommendEmoticonBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryAnalysisViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryAnalysisViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import kotlin.math.abs

class DiaryRecommendEmoticonFragment : BaseFragment<FragmentDiaryRecommendEmoticonBinding>(
    R.layout.fragment_diary_recommend_emoticon
) {

    private lateinit var diaryAnalysisViewModel: DiaryAnalysisViewModel
    private lateinit var emotionIconAdapter: EmoticonAdapter
    private lateinit var viewPager: ViewPager2
    private var diaryDate: String? = null
    private var selectedPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryAnalysisData = getDiaryAnalysisData()
        diaryDate = diaryAnalysisData.diaryDate

        setupViewModels()
        setupViewPager()
        setStatusDiaryAnalysisSave()

        // TODO: diaryAnalysisData의 Emotions로 감정티콘 추천
        val emoticon = Emotion.JOY

        // TODO: 보유 중의 감정티콘 테마들의 특정 감정만 조회하는 api 연동

        // '마음에 들어요' 선택 시
        binding.btnSave.setOnClickListener {
            // TODO: 선택한 감정티콘으로 변경
            val emoticon = Emoticon(
                emoticonThemeId = "66a8dce326d5e90dd4802923",
                emotion = Emotion.JOY.name
            )
            val request = createDiaryAnalysisRequest(diaryAnalysisData, emoticon)
            diaryAnalysisViewModel.saveDiaryAnalysis(request)
        }

        // '직접 고를게요' 선택 시
        binding.btnChooseSelf.setOnClickListener {
            val action =
                DiaryRecommendEmoticonFragmentDirections.actionNavEmoticonRecommendToNavEmotionSelect(
                    diaryAnalysisData.diaryDate,
                    "66a8dce326d5e90dd4802923",
                    Emotion.JOY
                )
            findNavController().navigate(action)
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

    private fun setupViewPager() {
        val emotions = listOf(
            R.drawable.emoticon_blank,
            R.drawable.emoticon_smile,
            R.drawable.ic_emotion_active,
            R.drawable.ic_emotion_inactive
        )

        emotionIconAdapter = EmoticonAdapter(emotions) { position ->
            selectedPosition = position
            viewPager.setCurrentItem(position, true)
            updateNavigationButtons()
        }

        viewPager = binding.vpSelectEmotion
        viewPager.adapter = emotionIconAdapter

        viewPager.apply {
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

        binding.btnLeft.setOnClickListener {
            if (selectedPosition > 0) {
                viewPager.setCurrentItem(selectedPosition - 1, true)
            }
        }

        binding.btnRight.setOnClickListener {
            if (selectedPosition < emotionIconAdapter.itemCount - 1) {
                viewPager.setCurrentItem(selectedPosition + 1, true)
            }
        }
    }

    private fun updateNavigationButtons() {
        binding.btnLeft.visibility = if (selectedPosition > 0) View.VISIBLE else View.INVISIBLE
        binding.btnRight.visibility = if (selectedPosition < emotionIconAdapter.itemCount - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun getDiaryAnalysisData() =
        DiaryRecommendEmoticonFragmentArgs.fromBundle(requireArguments()).diaryAnalysisData

    private fun setupViewModels() {
        // TODO: 로그인 구현 후, preference나 다른 방법으로 token을 받아와야 함
        val token = Constants.TOKEN
        val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
        val repository = DiaryAnalysisRepository(diaryAnalysisApiService)
        val factory = DiaryAnalysisViewModelFactory(repository)
        diaryAnalysisViewModel =
            ViewModelProvider(this, factory).get(DiaryAnalysisViewModel::class.java)
    }

    // 서버 감정 분석 저장 response status 설정
    private fun setStatusDiaryAnalysisSave() {
        diaryAnalysisViewModel.saveDiaryResponse.observe(viewLifecycleOwner) { response ->
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

    // 테스트 request 생성 함수
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