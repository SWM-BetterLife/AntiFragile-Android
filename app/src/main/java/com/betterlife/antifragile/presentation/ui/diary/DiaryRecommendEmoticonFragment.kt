package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentDiaryRecommendEmoticonBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory

class DiaryRecommendEmoticonFragment : BaseFragment<FragmentDiaryRecommendEmoticonBinding>(R.layout.fragment_diary_recommend_emoticon) {

    private lateinit var diaryViewModel: DiaryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diaryId = getDiaryIdFromArguments()
        val diaryType = getDiaryTypeFromArguments()

        setupViewModels()


        // TODO: 감정티콘 추천

        // TODO: '직접 선택' 버튼 클릭 시, 감정티콘 변경 뷰로 이동

        // 감정티콘 선택 시
        binding.btnSave.setOnClickListener {
            // 로컬 디비 감정티콘 업데이터
            val newEmotionIconUrl = "http://example.com/new_emotion.png"
            if (diaryType == "TEXT") {
                diaryViewModel.updateTextDiaryEmotionIcon(diaryId, newEmotionIconUrl)
            } else if (diaryType == "QUESTION") {
                diaryViewModel.updateQuestionDiaryEmotionIcon(diaryId, newEmotionIconUrl)
            } else {
                // TODO: 에러 처리
                Log.d("DiaryRecommendEmoticonFragment", "Unknown diary type: $diaryType")
            }
        }
    }

    private fun getDiaryIdFromArguments() = EmotionAnalysisFragmentArgs
        .fromBundle(requireArguments()).diaryId

    private fun getDiaryTypeFromArguments() = EmotionAnalysisFragmentArgs
        .fromBundle(requireArguments()).diaryType

    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this,
            DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)
    }
}