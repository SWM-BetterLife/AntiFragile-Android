package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.diaryanalysis.request.DiaryAnalysisCreateRequest
import com.betterlife.antifragile.data.model.diaryanalysis.request.Emoticon
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.databinding.FragmentDiarySelectEmoticonBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiarySelectEmoticonVIewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiarySelectEmoticonViewModelFactory

class DiarySelectEmoticonFragment : BaseFragment<FragmentDiarySelectEmoticonBinding>(R.layout.fragment_diary_select_emoticon) {

    private lateinit var viewModel: DiarySelectEmoticonVIewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwibWVtYmVySWQiOiI2NjkwZGQ4YTEzZjk1ZTUxZGI4MGYwNDkiLCJsb2dpblR5cGUiOiJLQUtBTyIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzIxNTg4OTU2fQ.yz3MI9zoyN8hjN-HFTWU-5UeIHmPcdfyvmrALLoslIi56rKX0bC7kOp6Fu3lhwzPw7eMbAPG3EVRItO0OX-lFQ"
        val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
        val repository = DiaryAnalysisRepository(diaryAnalysisApiService)
        val factory = DiarySelectEmoticonViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(DiarySelectEmoticonVIewModel::class.java)

        viewModel.saveDiaryStatus.observe(viewLifecycleOwner, Observer { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "일기 분석 저장 성공", Toast.LENGTH_SHORT).show()
                }
                Status.FAIL -> {
                    if (response.errorMessage == "이미 해당 날짜에 사용자의 일기 분석이 존재합니다") {
                        Toast.makeText(requireContext(), "${response.errorMessage}", Toast.LENGTH_SHORT).show()
                    } else {
                        // 추가적인 예외처리에 따라 다른 메시지를 보여줄 수 있음
                        Toast.makeText(requireContext(), "Failed to save diary: ${response.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), "일기 분석 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.btnSave.setOnClickListener {
            val request = DiaryAnalysisCreateRequest(
                emotions = listOf("감정1", "감정2"),
                event = "사건",
                thought = "생각",
                action = "행동",
                comment = "다짐",
                diaryDate = "2024-07-15",
                emoticon = Emoticon(
                    emoticonThemeId = "감정티콘 테마 id",
                    emotion = "감정"
                )
            )
            viewModel.saveDiaryAnalysis(request)
        }

    }
}