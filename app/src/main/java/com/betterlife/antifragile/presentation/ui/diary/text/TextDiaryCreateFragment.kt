package com.betterlife.antifragile.presentation.ui.diary.text

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import com.betterlife.antifragile.data.model.llm.DiaryAnalysisData
import com.betterlife.antifragile.databinding.FragmentTextDiaryCreateBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.SimpleTextWatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TextDiaryCreateFragment : BaseFragment<FragmentTextDiaryCreateBinding>(
    R.layout.fragment_text_diary_create
) {

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryDate: String
    private var textDiaryDetail: TextDiaryDetail? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVariables()
        setupViewModels()
        setupData()
        setupButton()

        (activity as MainActivity).hideBottomNavigation()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate))
            showBackButton {
                findNavController().popBackStack()
                (activity as MainActivity).showBottomNavigation()
            }
            showLine()
        }
    }

    private fun setupVariables() {
        diaryDate = TextDiaryCreateFragmentArgs.fromBundle(requireArguments()).diaryDate
        textDiaryDetail = TextDiaryCreateFragmentArgs.fromBundle(requireArguments()).textDiaryDetail
    }

    private fun setupViewModels() {
        val factory = DiaryViewModelFactory(requireActivity().application)
        diaryViewModel = ViewModelProvider(this, factory)[DiaryViewModel::class.java]
    }

    private fun setupData() {
        textDiaryDetail?.let {
            binding.etDiaryContent.setText(it.content)
        }

        binding.etDiaryContent.addTextChangedListener(SimpleTextWatcher {
            updateSaveButtonState(it.toString())
        })

        updateSaveButtonState(binding.etDiaryContent.text.toString())
    }

    private fun updateSaveButtonState(content: String) {
        val color = if (content.isEmpty()) {
            ContextCompat.getColor(requireContext(), R.color.light_gray_2)
        } else {
            ContextCompat.getColor(requireContext(), R.color.main_color)
        }

        binding.btnSave.apply {
            isEnabled = content.isNotEmpty()
            backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun setupButton() {
        binding.btnSave.setOnClickListener {
            val content = binding.etDiaryContent.text.toString()
            if (textDiaryDetail == null) {
                saveDiary(content)
            } else {
                updateDiary(content)
            }
        }
    }

    private fun saveDiary(content: String) {
        val textDiary = TextDiary(content = content, date = diaryDate)

        diaryViewModel.insertTextDiary(textDiary).observe(viewLifecycleOwner) { diaryId ->
            if (diaryId != -1L) {
                displaySaveSuccess(diaryId)
            } else {
                // TODO: 일기 저장 실패 시 처리
                showCustomToast("해당 날짜에 이미 일기가 존재합니다.")
            }
        }
    }

    private fun updateDiary(content: String) {
        textDiaryDetail?.let {
            diaryViewModel
                .updateTextDiary(it.id, content).observe(viewLifecycleOwner) { updateCount ->
                    if (updateCount > 0) {
                        promptEmotionAnalysis(it)
                    } else {
                        // TODO: 일기 수정 실패 시 처리
                        showCustomToast("일기 수정에 실패했습니다.")
                    }
            }
        }
    }

    private fun displaySaveSuccess(diaryId: Long) {
        // TODO: 포인트 획득 api 호출
        binding.tvGetPoint.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(500)
            navigateToEmotionAnalysis(diaryId.toInt(), false)
        }
    }

    private fun promptEmotionAnalysis(diaryDetail: TextDiaryDetail) {
        showSelectDialog(
            requireContext(),
            "감정 분석 여부",
            "수정한 일기에 대한 감정 분석을 받으시겠습니까?",
            "건너뛰기",
            "분석하기",
            { navigateToEmoticonRecommend(diaryDetail) },
            { navigateToEmotionAnalysis(diaryDetail.id, true) }
        )
    }

    private fun navigateToEmotionAnalysis(diaryId: Int, isUpdate: Boolean) {
        val action = TextDiaryCreateFragmentDirections
            .actionNavTextDiaryCreateToNavEmotionAnalysis("TEXT", isUpdate, diaryId)
        findNavController().navigate(action)
    }

    private fun navigateToEmoticonRecommend(diaryDetail: TextDiaryDetail) {
        val action = TextDiaryCreateFragmentDirections
            .actionNavTextDiaryCreateToNavEmoticonRecommend(
                getDiaryAnalysisData(),
                Emotion.fromString(diaryDetail.emoticonInfo?.emotion),
                diaryDetail.emoticonInfo?.emoticonThemeId ?: "",
                true
            )
        findNavController().navigate(action)
    }

    private fun getDiaryAnalysisData(): DiaryAnalysisData {
        return DiaryAnalysisData(
            diaryDate = diaryDate,
            emotions = textDiaryDetail?.emotions ?: arrayListOf(),
            event = "",
            thought = "",
            action = "",
            comment = ""
        )
    }
}