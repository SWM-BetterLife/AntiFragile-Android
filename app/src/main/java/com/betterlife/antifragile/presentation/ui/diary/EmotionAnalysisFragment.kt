package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.request.Emoticon
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.databinding.FragmentEmotionAnalysisBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryAnalysisViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryAnalysisViewModelFactory
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory
import kotlinx.coroutines.launch

class EmotionAnalysisFragment : BaseFragment<FragmentEmotionAnalysisBinding>(R.layout.fragment_emotion_analysis){

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryAnalysisViewModel: DiaryAnalysisViewModel
    private var textDiary: TextDiary? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달 받은 diaryId 받기
        val diaryId = getDiaryIdFromArguments()

        setupViewModels()

        // 로컬 db에 저장된 텍스트 일기 조회
        getTextDiary(diaryId)

        // TODO: on-device LLM 으로 감정 분석 실시

        setStatusDiaryAnalysisSave()

        binding.btnSave.setOnClickListener {
            // TODO: 이 request는 on-device LLM의 결과로 생성되어야 함
            val request = createDiaryAnalysisRequest()
            diaryAnalysisViewModel.saveDiaryAnalysis(request)

            // 값 전달 및 Fragment 전환
            val action = EmotionAnalysisFragmentDirections.acitonNavEmotionAnalysisToNavEmoticonRecommend(diaryId.toInt())
            findNavController().navigate(action)
        }
    }

    private fun getDiaryIdFromArguments() = EmotionAnalysisFragmentArgs.fromBundle(requireArguments()).diaryId

    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this,
            DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)

        // TODO: 로그인 구현 후, preference나 다른 방법으로 token을 받아와야 함
        val token =
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwibWVtYmVySWQiOiI2NjkwZGQ4YTEzZjk1ZTUxZGI4MGYwNDkiLCJsb2dpblR5cGUiOiJLQUtBTyIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzIxNzU5ODA4fQ.QJDdt1EiU0qybA2K8Qd-pH6CCa2wjLFu9QKI_gNT4ooGCRwCuh4-CdTwVLDBAIxyZjEZvKTLL061d38gB4yquQ"
        val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
        val repository = DiaryAnalysisRepository(diaryAnalysisApiService)
        val factory = DiaryAnalysisViewModelFactory(repository)
        diaryAnalysisViewModel =
            ViewModelProvider(this, factory).get(DiaryAnalysisViewModel::class.java)

    }

    // 로컬 db에 저장된 텍스트 일기 조회
    private fun getTextDiary(diaryId: Int) {
        diaryViewModel.getTextDiaryById(diaryId).observe(viewLifecycleOwner) { retrievedDiary ->
            retrievedDiary?.let {
                textDiary = it
                Log.d("EmotionAnalysisFragment", "Retrieved Diary: $textDiary")
            }
        }
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
    private fun createDiaryAnalysisRequest(): DiaryAnalysisCreateRequest {
        return DiaryAnalysisCreateRequest(
            emotions = listOf("감정1", "감정2"),
            event = "사건",
            thought = "생각",
            action = "행동",
            comment = "다짐",
            diaryDate = textDiary?.date ?: "",  // textDiary의 날짜를 사용
            emoticon = Emoticon(
                emoticonThemeId = "감정티콘 테마 id",
                emotion = "감정"
            )
        )
    }
}