package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentDiaryTypeSelectBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class DiaryTypeSelectFragment : BaseFragment<FragmentDiaryTypeSelectBinding>(R.layout.fragment_diary_type_select) {

    private var diaryDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryDate = getDiaryDateFromArguments()

        binding.btnTextType.setOnClickListener {
            val action = DiaryTypeSelectFragmentDirections.actionNavDiaryTypeSelectToNavTextDiaryCreate(diaryDate!!)
            findNavController().navigate(action)
        }

        binding.btnQuestionType.setOnClickListener {
            // TODO: 질문형 일기 작성 화면으로 이동
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

    private fun getDiaryDateFromArguments() =
        DiaryTypeSelectFragmentArgs.fromBundle(requireArguments()).diaryDate
}