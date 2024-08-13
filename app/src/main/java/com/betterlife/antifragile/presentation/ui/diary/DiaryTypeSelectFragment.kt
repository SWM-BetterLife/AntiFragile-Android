package com.betterlife.antifragile.presentation.ui.diary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
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
        setupButtonTouchListeners()
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtonTouchListeners() {
        binding.btnTextType.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.btnTextType.setImageResource(R.drawable.btn_type_text_active)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.btnTextType.setImageResource(R.drawable.btn_type_text_inactive)
                }
            }
            return@setOnTouchListener true
        }

        binding.btnQuestionType.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.btnQuestionType.setImageResource(R.drawable.btn_type_question_active)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.btnQuestionType.setImageResource(R.drawable.btn_type_question_inactive)
                }
            }
            return@setOnTouchListener true
        }
    }
}