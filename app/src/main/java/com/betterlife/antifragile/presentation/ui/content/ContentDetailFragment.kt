package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentContentDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar


class ContentDetailFragment : BaseFragment<FragmentContentDetailBinding>(
    R.layout.fragment_content_detail
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("콘텐츠 시청")
            showBackButton {
                findNavController().popBackStack()
            }
        }
    }

}