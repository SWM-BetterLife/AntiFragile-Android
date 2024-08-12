package com.betterlife.antifragile.presentation.ui.diary.text

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import com.betterlife.antifragile.databinding.FragmentTextDiaryDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.TextDiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.TextDiaryViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil

class TextDiaryDetailFragment: BaseFragment<FragmentTextDiaryDetailBinding>(
    R.layout.fragment_text_diary_detail
) {

    private lateinit var textDiaryViewModel: TextDiaryViewModel
    private lateinit var diaryDate: String
    private var textDiaryDetail: TextDiaryDetail? = null

    private var diaryId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVariables()
        setupViewModel()
        setupObservers()
        loadDiaryData(diaryId, diaryDate)
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate))
            showBackButton() {
                findNavController().popBackStack()
            }
            showCustomButton(R.drawable.btn_edit) {
                val action =
                    TextDiaryDetailFragmentDirections.actionNavTextDiaryDetailToNavTextDiaryCreate(
                    diaryDate, textDiaryDetail
                )
                findNavController().navigate(action)
            }
            showLine()
        }
    }

    private fun setupVariables() {
        diaryDate = TextDiaryDetailFragmentArgs.fromBundle(requireArguments()).diaryDate
        diaryId = TextDiaryDetailFragmentArgs.fromBundle(requireArguments()).diaryId
    }

    private fun setupViewModel() {
        val factory = TextDiaryViewModelFactory(requireContext(), Constants.TOKEN)
        textDiaryViewModel = ViewModelProvider(this, factory)[TextDiaryViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        setupBaseObserver(
            liveData = textDiaryViewModel.textDiaryDetail,
            onSuccess = { textDiaryDetail ->
                this.textDiaryDetail = textDiaryDetail
                binding.textDiaryDetail = textDiaryDetail
                setEmotionBackground(
                    binding.loEmoticon, textDiaryDetail.emoticonInfo?.emotion ?: "오류"
                )
            },
            onError = {
                if (
                    it.errorMessage == CustomErrorMessage.DIARY_ANALYSIS_NOT_FOUND.message
                ) {
                    // TODO: 감정 분석을 하지 않은 경우 -> 감정분석하러 이동
                    showCustomToast("감정 분석을 하지 않은 일기입니다.")
                } else {
                    showCustomToast(it.errorMessage ?: "일기를 불러오는 데 실패했습니다.")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 1000)
                }
            }
        )
    }

    private fun loadDiaryData(id: Int, date: String) {
        textDiaryViewModel.getTextDiaryDetail(id, date)
    }

    private fun setEmotionBackground(layout: ConstraintLayout, emotion: String) {
        layout.setBackgroundResource(Emotion.fromString(emotion).getBackgroundResource())
    }

    private fun setupButton() {
        binding.btnMoveContent.setOnClickListener {
            val action =
                TextDiaryDetailFragmentDirections.actionNavTextDiaryDetailToNavContentRecommend(
                diaryDate, false, null
            )
            findNavController().navigate(action)
        }
    }
}