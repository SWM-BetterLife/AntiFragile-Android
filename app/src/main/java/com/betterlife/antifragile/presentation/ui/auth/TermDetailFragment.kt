package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.view.View
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
        setTermData()
        termType.isAgreed = true
    }

    private fun setVariables() {
        termType = TermDetailFragmentArgs.fromBundle(requireArguments()).termType
    }

    private fun setTermData() {
        binding.termDetailContent.text = termType.content
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