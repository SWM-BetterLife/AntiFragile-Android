package com.betterlife.antifragile.presentation.ui.diary

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.databinding.FragmentTextDiaryCreateBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class TextDiaryCreateFragment : BaseFragment<FragmentTextDiaryCreateBinding>(
    R.layout.fragment_text_diary_create
) {

    private lateinit var diaryViewModel: DiaryViewModel
    private var diaryDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryDate = getDiaryDateFromArguments()
        showCustomToast(diaryDate!!)

        setupViewModels()

        binding.btnSave.setOnClickListener {
            // TODO: 사용자가 입력한 값으로 대체
            val textDiary = TextDiary(
                content = "임시 내용",
                date = diaryDate!!
            )

            // 텍스트 일기 삽입 및 ID 반환
            diaryViewModel.insertTextDiary(textDiary).observe(viewLifecycleOwner) { diaryId ->
                if (diaryId != -1L) {
                    val action =
                        TextDiaryCreateFragmentDirections.actionNavTextDiaryCreateToNavEmotionAnalysis(
                            "TEXT",
                            diaryId.toInt()
                        )
                    findNavController().navigate(action)
                } else {
                    // 예외 처리: 동일한 날짜의 일기가 이미 존재함을 사용자에게 알림
                    showCustomToast("해당 날짜에 이미 일기가 존재합니다.")
                }
            }
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

    private fun getDiaryDateFromArguments(): String {
        return TextDiaryCreateFragmentArgs.fromBundle(requireArguments()).diaryDate
    }

    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this,
            DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)
    }
}