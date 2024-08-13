package com.betterlife.antifragile.presentation.ui.emotion

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.databinding.FragmentMyEmotionBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil.getTodayDate
import com.betterlife.antifragile.presentation.util.ImageUtil.setImage
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken

class MyEmotionFragment : BaseFragment<FragmentMyEmotionBinding>(
    R.layout.fragment_my_emotion
) {

    private lateinit var myEmotionViewModel: MyEmotionViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()
        loadDiaryAnalysisData()
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("나의 감정")
            showLine()
        }
    }

    private fun setupViewModel() {
        val factory = MyEmotionViewModelFactory(getAccessToken(requireContext())!!)
        myEmotionViewModel = factory.create(MyEmotionViewModel::class.java)
    }

    private fun setupObserver() {
        setupBaseObserver(
            liveData = myEmotionViewModel.diaryAnalysisResponse,
            onSuccess = {
                setupEmotionVisibility(true)
                binding.ivEmoticon.setImage(it.emoticon.imgUrl)
                binding.tvEmotion.text = it.emoticon.emotion
                setEmotionBackground(
                    binding.loEmoticon, it.emoticon.emotion
                )
            },
            onError = {
                handleSaveError(it.errorMessage)
            }
        )
    }

    private fun handleSaveError(errorMessage: String?) {
        if (errorMessage == CustomErrorMessage.DIARY_ANALYSIS_NOT_FOUND.message) {
            setupEmotionVisibility(false)
        } else {
            showCustomToast(errorMessage ?: "감정 분석을 불러오는데 실패했습니다.")
        }
    }

    private fun setupEmotionVisibility(isActive: Boolean) {
        binding.apply {
            val activeVisibility = if (isActive) View.VISIBLE else View.GONE
            val inactiveVisibility = if (isActive) View.GONE else View.VISIBLE

            ivEmoticon.visibility = activeVisibility
            tvEmotion.visibility = activeVisibility
            tvMyEmotion.visibility = activeVisibility
            ivBackgroundEmoticon.visibility = activeVisibility

            ivEmoticonInactive.visibility = inactiveVisibility
            btnMoveCreateDiary.visibility = inactiveVisibility
        }
    }

    private fun loadDiaryAnalysisData() {
        myEmotionViewModel.getDailyDiaryAnalysis(getTodayDate())
    }

    private fun setEmotionBackground(layout: ConstraintLayout, emotion: String) {
        layout.setBackgroundResource(Emotion.fromString(emotion).getBackgroundResource())
    }

    private fun setupButton() {
        binding.apply {
            btnMoveCreateDiary.setOnClickListener {
                val action =
                    MyEmotionFragmentDirections.actionNavEmotionToSelectDiaryType(getTodayDate())
                findNavController().navigate(action)
            }

            btnMoveEmotionChart.visibility = View.GONE
            btnMoveEmotionChart.setOnClickListener {
                // TODO: 감정 차트 화면으로 이동
            }
        }
    }
}