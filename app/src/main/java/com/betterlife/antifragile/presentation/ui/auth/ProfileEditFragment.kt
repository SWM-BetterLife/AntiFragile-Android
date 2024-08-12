package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.databinding.FragmentProfileEditBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class ProfileEditFragment : BaseFragment<FragmentProfileEditBinding>(
    R.layout.fragment_profile_edit
) {

    private var isNewMember: Boolean = false
    private lateinit var email: String
    private lateinit var loginType: LoginType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        Log.d("ProfileEditFragment", "email: $email, loginType: $loginType, isNewMember: $isNewMember")
    }

    private fun setVariables() {
        email = ProfileEditFragmentArgs.fromBundle(requireArguments()).email
        loginType = ProfileEditFragmentArgs.fromBundle(requireArguments()).loginType
        isNewMember = ProfileEditFragmentArgs.fromBundle(requireArguments()).isNewMember
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            showBackButton(
            )
        }
    }

}