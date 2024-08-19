package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.navigation.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.enums.TermType
import com.betterlife.antifragile.databinding.FragmentTermDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.util.CustomToolbar

class TermDetailFragment : BaseFragment<FragmentTermDetailBinding>(
    R.layout.fragment_term_detail
) {

    private lateinit var termType: TermType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        loadTermInWebView()
        termType.isAgreed = true
    }

    private fun setVariables() {
        termType = TermDetailFragmentArgs.fromBundle(requireArguments()).termType
    }

    private fun loadTermInWebView() {
        binding.webviewTerm.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadUrl(termType.content)
        }
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(getString(termType.titleResId))
            showBackButton {
                requireView().findNavController().popBackStack()
            }
        }
    }
}