package com.betterlife.antifragile.presentation.ui.emotion

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.DiaryAnalysisRepository
import com.betterlife.antifragile.databinding.FragmentMyEmotionBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil.getTodayDate
import com.betterlife.antifragile.presentation.util.ImageUtil.loadImage

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
            showCustomButton(R.drawable.btn_alarm) {
                // TODO: 알림 버튼 클릭 처리
                showCustomToast("알림 버튼 클릭")
            }
            showLine()
        }
    }

    private fun setupViewModel() {
        // TODO: 로그인 구현 후 preference에서 토큰 가져오기
        val token = Constants.TOKEN
        val diaryAnalysisApiService = RetrofitInterface.createDiaryAnalysisApiService(token)
        val diaryAnalysisRepository = DiaryAnalysisRepository(diaryAnalysisApiService)
        val factory = MyEmotionViewModelFactory(diaryAnalysisRepository)
        myEmotionViewModel = factory.create(MyEmotionViewModel::class.java)
    }

    private fun setupObserver() {
        myEmotionViewModel.diaryAnalysisResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    setupEmotionVisibility(true)
                    binding.apply {
                        ivEmoticon.loadImage(response?.data?.emoticon?.imgUrl)
                        tvEmotion.text = response.data?.emotions?.joinToString(", ")
                    }
                }
                Status.FAIL -> {
                    dismissLoading()
                    if (
                        response.errorMessage == CustomErrorMessage.DIARY_ANALYSIS_NOT_FOUND.message
                    ) {
                        setupEmotionVisibility(false)
                    } else {
                        showCustomToast(response.errorMessage ?: "감정 분석을 불러오는데 실패했습니다.")
                    }
                }
                Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "감정 분석을 불러오는데 실패했습니다.")
                }
                else -> {
                    Log.d("MyEmotionFragment", "Unknown status: ${response.status}")
                }
            }
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

            tvEmoticonInactive.visibility = inactiveVisibility
            btnMoveCreateDiary.visibility = inactiveVisibility
        }
    }

    private fun loadDiaryAnalysisData() {
        myEmotionViewModel.getDailyDiaryAnalysis(getTodayDate())
    }

    private fun setupButton() {
        binding.apply {
            btnMoveCreateDiary.setOnClickListener {
                val action =
                    MyEmotionFragmentDirections.actionNavEmotionToSelectDiaryType(getTodayDate())
                findNavController().navigate(action)
            }

            btnMoveEmotionChart.setOnClickListener {
                // TODO: 감정 차트 화면으로 이동
                btnMoveEmotionChart.visibility = View.GONE
            }
        }
    }
}