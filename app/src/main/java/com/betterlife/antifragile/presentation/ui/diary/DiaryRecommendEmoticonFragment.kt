package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch

class DiaryRecommendEmoticonFragment : BaseFragment<FragmentDiaryRecommendEmoticonBinding>(R.layout.fragment_diary_recommend_emoticon) {

    private lateinit var diaryAnalysisViewModel: DiaryAnalysisViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryAnalysisData = getDiaryAnalysisData()

        setupViewModels()

        // TODO: 감정티콘 추천

        // TODO: '직접 선택' 버튼 클릭 시, 감정티콘 변경 뷰로 이동


        // 서버에 감정 분석 저장 response status 설정
        setStatusDiaryAnalysisSave()

        // 감정티콘 선택 시
        binding.btnSave.setOnClickListener {
            // TODO: 선택한 감정티콘으로 변경
            val emoticon = Emoticon(
                emoticonThemeId = "감정티콘 테마 id",
                emotion = Emotion.JOY.name
            )
            val request = createDiaryAnalysisRequest(diaryAnalysisData, emoticon)
            // 서버에 감정 분석 저장
            diaryAnalysisViewModel.saveDiaryAnalysis(request)
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
        lifecycleScope.launch {
            diaryAnalysisViewModel.saveDiaryStatus.asFlow().collect { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        showCustomToast("일기 분석 저장 성공")
                    }

                    Status.FAIL -> {
                        if (response.errorMessage == "이미 해당 날짜에 사용자의 일기 분석이 존재합니다") {
                            showCustomToast("${response.errorMessage}")
                        } else {
                            showCustomToast("일기 분석 저장 실패")
                        }
                    }

                    Status.ERROR -> {
                        showCustomToast("일기 분석 저장 실패")
                    }

                    else -> {
                        Log.d("EmotionAnalysisFragment", "Unknown status: ${response.status}")
                    }
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