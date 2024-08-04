package com.betterlife.antifragile.presentation.ui.diary.text

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
            val textDiary = TextDiary(
                content = binding.etDiaryContent.text.toString(),
                date = diaryDate!!
            )

            // 텍스트 일기 삽입 및 ID 반환
            diaryViewModel.insertTextDiary(textDiary).observe(viewLifecycleOwner) { diaryId ->
                if (diaryId != -1L) {

                    binding.tvGetPoint.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        val action =
                            TextDiaryCreateFragmentDirections.actionNavTextDiaryCreateToNavEmotionAnalysis(
                                "TEXT",
                                diaryId.toInt()
                            )
                        findNavController().navigate(action)
                    }, 1000) // 2000 milliseconds = 2 seconds

                } else {
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