package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.repository.ContentRepository
import com.betterlife.antifragile.databinding.FragmentRecommendContentBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import java.time.LocalDate

class RecommendContentFragment : BaseFragment<FragmentRecommendContentBinding>(
    R.layout.fragment_recommend_content
) {

    private lateinit var contentRecommendViewModel: ContentRecommendViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

        val diaryDateString = arguments?.getString("diaryDate")
        val isNewDiary = arguments?.getBoolean("isNewDiary") ?: false
        val feedback = arguments?.getString("feedback")

        val diaryDate = diaryDateString?.let { LocalDate.parse(it) }

        // 로직 구성
        diaryDate?.let { date ->
            when {
                isNewDiary -> {
                    // 신규 일기 생성 -> 추천 API 호출
                    contentRecommendViewModel.getRecommendContents(null, date)
                }
                !isNewDiary && feedback == null -> {
                    // 기존 일기 -> 조회 API 호출
                    contentRecommendViewModel.getContentList(date)
                }
                !isNewDiary && feedback != null -> {
                    // 재추천 -> 재추천 API 호출
                    contentRecommendViewModel.getRecommendContents(feedback, date)
                }
                else -> {
                    Log.e("RecommendContentFragment", "Unhandled case for isNewDiary and feedback")
                }
            }
        } ?: run {
            Log.e("RecommendContentFragment", "Invalid or missing diaryDate")
        }
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("맞춤형 콘텐츠")
            showBackButton {
                findNavController().popBackStack()
            }
            showCustomButton(R.drawable.btn_re) {
                // TODO: 재추천 받기 버튼 클릭 처리
            }
        }
    }

    private fun setupViewModel() {
        val factory = ContentRecommendViewModelFactory(Constants.TOKEN)
        contentRecommendViewModel =
            ViewModelProvider(this, factory)[ContentRecommendViewModel::class.java]
    }


}