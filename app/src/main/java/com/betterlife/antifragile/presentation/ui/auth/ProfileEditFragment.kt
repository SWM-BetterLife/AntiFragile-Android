package com.betterlife.antifragile.presentation.ui.auth

import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentProfileEditBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class ProfileEditFragment : BaseFragment<FragmentProfileEditBinding>(
    R.layout.fragment_profile_edit
) {

    private var isNewMember: Boolean = false

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            showBackButton(
            )
        }
    }

}