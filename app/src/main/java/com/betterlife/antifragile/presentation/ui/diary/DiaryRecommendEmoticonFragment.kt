package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

class DiaryRecommendEmoticonFragment : BaseFragment<FragmentDiaryRecommendEmoticonBinding>(R.layout.fragment_diary_recommend_emoticon) {

    private lateinit var diaryAnalysisViewModel: DiaryAnalysisViewModel
    private var diaryDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryAnalysisData = getDiaryAnalysisData()
        diaryDate = diaryAnalysisData.diaryDate

        setupViewModels()

        // TODO: 감정티콘 추천

        // TODO: '직접 선택' 버튼 클릭 시, 감정티콘 변경 뷰로 이동


        // 서버에 감정 분석 저장 response status 설정
        setStatusDiaryAnalysisSave()

        // 감정티콘 선택 시
        binding.btnSave.setOnClickListener {
            // TODO: 선택한 감정티콘으로 변경
            val emoticon = Emoticon(
                emoticonThemeId = "66a8dce326d5e90dd4802923",
                emotion = Emotion.JOY.name
            )
            val request = createDiaryAnalysisRequest(diaryAnalysisData, emoticon)
            // 서버에 감정 분석 저장
            diaryAnalysisViewModel.saveDiaryAnalysis(request)
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