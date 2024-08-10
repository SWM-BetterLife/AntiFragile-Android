package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentContentViewCompleteBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class ContentViewCompleteFragment : BaseFragment<FragmentContentViewCompleteBinding>(
    R.layout.fragment_content_view_complete
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("시청 완료")
            showBackButton {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupButton() {
        binding.btnReRecommend.setOnClickListener {
            // TODO: 영상 재추천 다이럴로그 띄우기
            // TODO: 영상 재추천 api 호출
            // TODO: 영상 선택 화면으로 이동
        }

        binding.btnMoveToSelectContent.setOnClickListener {
            // TODO: 영상 선택 화면으로 이동
        }

        binding.btnMoveToDiary.setOnClickListener {
            // TODO: Date를 통해 텍스트 또는 질문인지 확인
            // TODO: 일기 상세 보기로 이동
        }
    }
}