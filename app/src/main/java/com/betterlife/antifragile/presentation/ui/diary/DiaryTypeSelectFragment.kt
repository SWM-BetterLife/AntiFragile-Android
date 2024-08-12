package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentDiaryTypeSelectBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class DiaryTypeSelectFragment : BaseFragment<FragmentDiaryTypeSelectBinding>(
    R.layout.fragment_diary_type_select
) {

    private lateinit var diaryDate: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVariables()
        setupButtons()
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
        diaryDate = DiaryTypeSelectFragmentArgs.fromBundle(requireArguments()).diaryDate
    }

    private fun setupButtons() {
        binding.btnTextType.setOnClickListener {
            findNavController().navigate(
                DiaryTypeSelectFragmentDirections.actionNavDiaryTypeSelectToNavTextDiaryCreate(
                    diaryDate, null)
            )
        }

        binding.btnQuestionType.setOnClickListener {
            // TODO: 질문형 일기 작성 화면으로 이동
        }
    }
}