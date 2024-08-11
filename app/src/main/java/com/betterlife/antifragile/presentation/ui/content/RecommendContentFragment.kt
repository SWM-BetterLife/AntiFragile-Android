package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentRecommendContentBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar

class RecommendContentFragment : BaseFragment<FragmentRecommendContentBinding>(
    R.layout.fragment_recommend_content
) {

    private lateinit var contentRecommendViewModel: ContentRecommendViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()

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