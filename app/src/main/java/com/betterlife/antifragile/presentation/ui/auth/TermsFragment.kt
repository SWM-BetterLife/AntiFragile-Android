package com.betterlife.antifragile.presentation.ui.auth

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.enums.TermType
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
        updateAgreementStatus()
    }

    override fun onResume() {
        super.onResume()
        updateAgreementStatus()
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
        binding.apply {
            setTermClickListener(loServiceTerm, TermType.SERVICE_TERM)
            setTermClickListener(loPrivacyTerm, TermType.PRIVACY_TERM)
            setMarketingTermClickListener(loMarketingTerm)
            setDiaryTermClickListener(loDiaryTerm)

            btnStart.setOnClickListener {
                if (validateAgreements()) {
                    findNavController().navigate(
                        TermsFragmentDirections.actionNavTermsFragmentToNavProfileEditFragment(
                            email, loginType, true
                        )
                    )
                } else {
                    showCustomToast("모든 필수 약관에 동의해주세요.")
                }
            }
        }
    }

    private fun updateAgreementStatus() {
        binding.apply {
            updateTermView(TermType.SERVICE_TERM, loServiceTerm, ivServiceTerm, tvServiceTerm)
            updateTermView(TermType.PRIVACY_TERM, loPrivacyTerm, ivPrivacyTerm, tvPrivacyTerm)
            updateTermView(TermType.MARKETING_TERM, loMarketingTerm, ivMarketingTerm, tvMarketingTerm)
            updateTermView(TermType.DIARY_TERM, loDiaryTerm, ivDiaryTerm, tvDiaryTerm)

            val allAgreed = validateAgreements()
            val color = if (allAgreed) {
                ContextCompat.getColor(requireContext(), R.color.main_color)
            } else {
                ContextCompat.getColor(requireContext(), R.color.light_gray_2)
            }
            btnStart.apply {
                isEnabled = allAgreed
                backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun updateTermView(termType: TermType, layout: View, icon: ImageView, text: TextView) {
        layout.setBackgroundResource(
            if (termType.isAgreed)
                R.drawable.btn_rectangle_radius_100_transparent
            else
                R.drawable.btn_rectangle_radius_100_transparent_gray
        )
        icon.setImageResource(
            if (termType.isAgreed)
                R.drawable.ic_agreement_selected
            else
                R.drawable.ic_agreement_unselected
        )

        text.setTextColor(
            if (termType.isAgreed)
                ContextCompat.getColor(requireContext(), R.color.main_color)
            else
                ContextCompat.getColor(requireContext(), R.color.black)
        )

    }

    private fun setTermClickListener(layout: View, termType: TermType) {
        if (!termType.isAgreed) {
            layout.setOnClickListener {
                navigateToTermDetail(termType)
            }
        } else {
            layout.setOnClickListener(null)
        }
    }

    private fun setMarketingTermClickListener(layout: View) {
        val termType = TermType.MARKETING_TERM
        layout.setOnClickListener {
            termType.isAgreed = !termType.isAgreed
            if (!termType.isAgreed) {
                updateAgreementStatus()
            } else {
                navigateToTermDetail(termType)
            }
        }
    }

    private fun setDiaryTermClickListener(layout: View) {
        val termType = TermType.DIARY_TERM
        layout.setOnClickListener {
            termType.isAgreed = !termType.isAgreed
            if (!termType.isAgreed) {
                updateAgreementStatus()
            } else {
                navigateToTermDetail(termType)
            }
        }
    }

    private fun validateAgreements(): Boolean {
        return TermType.SERVICE_TERM.isAgreed &&
                TermType.PRIVACY_TERM.isAgreed
    }

    private fun navigateToTermDetail(termType: TermType) {
        findNavController().navigate(
            TermsFragmentDirections.actionNavTermsFragmentToNavTermDetailFragment(termType)
        )
    }
}