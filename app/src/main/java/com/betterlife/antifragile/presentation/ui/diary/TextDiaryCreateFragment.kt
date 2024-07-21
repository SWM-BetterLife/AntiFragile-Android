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

class TextDiaryCreateFragment : BaseFragment<FragmentTextDiaryCreateBinding>(R.layout.fragment_text_diary_create) {

    private lateinit var diaryViewModel: DiaryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModels()

        binding.btnSave.setOnClickListener {
            // TODO: 사용자가 입력한 값으로 대체
            val textDiary = TextDiary(
                content = "임시 내용",
                date = "2024-07-21",
                emotionIconUrl = null
            )

            // 텍스트 일기 삽입 및 ID 반환
            diaryViewModel.insertTextDiary(textDiary).observe(viewLifecycleOwner) { diaryId ->
                val action =
                    TextDiaryCreateFragmentDirections.actionNavTextDiaryCreateToNavEmotionAnalysis(
                        diaryId.toInt()
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this,
            DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)
    }
}