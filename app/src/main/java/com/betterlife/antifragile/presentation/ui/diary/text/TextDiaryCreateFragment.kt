package com.betterlife.antifragile.presentation.ui.diary.text

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.diary.TextDiary
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import com.betterlife.antifragile.data.model.diary.llm.DiaryAnalysisData
import com.betterlife.antifragile.databinding.FragmentTextDiaryCreateBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.DiaryViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class TextDiaryCreateFragment : BaseFragment<FragmentTextDiaryCreateBinding>(
    R.layout.fragment_text_diary_create
) {

    private lateinit var diaryViewModel: DiaryViewModel
    private var diaryDate: String? = null
    private var textDiaryDetail: TextDiaryDetail? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryDate = getDiaryDateFromArguments()
        textDiaryDetail = getTextDiaryDetailFromArguments()

        setupViewModels()
        setupView()
        setupButton()

        (activity as MainActivity).hideBottomNavigation()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate!!))
            showBackButton {
                findNavController().popBackStack()
                (activity as MainActivity).showBottomNavigation()
            }
        }
    }

    private fun setupViewModels() {
        diaryViewModel = ViewModelProvider(
            this,
            DiaryViewModelFactory(requireActivity().application)
        ).get(DiaryViewModel::class.java)
    }

    private fun setupView() {
        textDiaryDetail?.let {
            binding.etDiaryContent.setText(it.content)
        }
    }

    private fun setupButton() {
        binding.btnSave.setOnClickListener {
            val content = binding.etDiaryContent.text.toString()
            if (textDiaryDetail == null) {
                saveNewDiary(content)
            } else {
                updateExistingDiary(content)
            }
        }
    }

    private fun saveNewDiary(content: String) {
        val textDiary = TextDiary(
            content = content,
            date = diaryDate!!
        )

        diaryViewModel.insertTextDiary(textDiary).observe(viewLifecycleOwner) { diaryId ->
            if (diaryId != -1L) {
                // TODO: 멤버 포인트 증가 api 호출
                binding.tvGetPoint.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val action =
                        TextDiaryCreateFragmentDirections
                            .actionNavTextDiaryCreateToNavEmotionAnalysis(
                            "TEXT",
                            diaryId.toInt()
                        )
                    findNavController().navigate(action)
                }, 1000)
            } else {
                showCustomToast("해당 날짜에 이미 일기가 존재합니다.")
            }
        }
    }

    private fun updateExistingDiary(content: String) {
        textDiaryDetail?.let {
            diaryViewModel.updateTextDiary(
                it.id, content
            ).observe(viewLifecycleOwner) { updateCount ->
                if (updateCount > 0) {
                    showSelectDialog(
                        requireContext(),
                        "감정 분석 여부",
                        "수정한 일기에 대한 감정 분석을 받으시겠습니까?",
                        "건너뛰기",
                        "분석하기",
                        {
                            val action =
                                TextDiaryCreateFragmentDirections
                                    .actionNavTextDiaryCreateToNavEmoticonRecommend(
                                        getDiaryAnalysisData(),
                                        Emotion.fromString(it.emoticonInfo?.emotion),
                                        it.emoticonInfo?.emoticonThemeId ?: ""
                                    )
                            findNavController().navigate(action)
                        },
                        {
                            val action =
                                TextDiaryCreateFragmentDirections
                                    .actionNavTextDiaryCreateToNavEmotionAnalysis(
                                        "TEXT",
                                        it.id
                                    )
                            findNavController().navigate(action)
                        }
                    )
                } else {
                    showCustomToast("일기 수정에 실패했습니다.")
                }
            }
        }
    }

    private fun getDiaryAnalysisData(): DiaryAnalysisData {
        return DiaryAnalysisData(
            diaryDate = diaryDate!!,
            emotions = textDiaryDetail?.emotions ?: arrayListOf(),
            event = "",
            thought = "",
            action = "",
            comment = ""
        )
    }

    private fun getDiaryDateFromArguments(): String =
        TextDiaryCreateFragmentArgs.fromBundle(requireArguments()).diaryDate

    private fun getTextDiaryDetailFromArguments(): TextDiaryDetail? =
        TextDiaryCreateFragmentArgs.fromBundle(requireArguments()).textDiaryDetail
}