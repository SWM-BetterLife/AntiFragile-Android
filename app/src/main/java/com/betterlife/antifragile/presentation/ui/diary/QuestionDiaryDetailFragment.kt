package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentQuestionDiaryDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.TextDiaryDetailFragmentArgs
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class QuestionDiaryDetailFragment: BaseFragment<FragmentQuestionDiaryDetailBinding>(
    R.layout.fragment_question_diary_detail
) {

    private var diaryDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryDate = getDiaryDateFromArguments()
        val diaryId = getDiaryIdFromArguments()

        // TODO: 해당 일기 정보를 가져와서 화면에 표시하는 로직 구현

        // TODO: 추천 콘텐츠 버튼 클릭 시 추천 콘텐츠 화면으로 이동하는 로직 구현
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate!!))
            showBackButton(true) {
                findNavController().popBackStack()
            }
            showEditButton("수정") {
                // TODO: 수정 버튼 클릭 처리
            }
        }
    }

    private fun getDiaryDateFromArguments(): String {
        return QuestionDiaryDetailFragmentArgs.fromBundle(requireArguments()).diaryDate
    }

    private fun getDiaryIdFromArguments(): Int {
        return QuestionDiaryDetailFragmentArgs.fromBundle(requireArguments()).diaryId
    }
}