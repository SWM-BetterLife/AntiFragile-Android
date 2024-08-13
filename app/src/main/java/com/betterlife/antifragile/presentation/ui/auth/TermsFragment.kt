package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.databinding.FragmentTermsBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class TermsFragment : BaseFragment<FragmentTermsBinding>(
    R.layout.fragment_terms
) {

    private lateinit var email: String
    private lateinit var loginType: LoginType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("약관 동의")
            showBackButton {
                requireView().findNavController().popBackStack()
            }
        }
    }

    private fun setVariables() {
        email = TermsFragmentArgs.fromBundle(requireArguments()).email
        loginType = TermsFragmentArgs.fromBundle(requireArguments()).loginType
    }

    private fun setupButton() {
        binding.btnStart.setOnClickListener {
            findNavController().navigate(
                TermsFragmentDirections.actionNavTermsFragmentToNavProfileEditFragment(
                    email, loginType, true
                )
            )
        }
    }
}