package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.databinding.FragmentTermsBinding

class TermsFragment : Fragment() {

    private var _binding: FragmentTermsBinding? = null
    private val binding get() = _binding!!

    private lateinit var email: String
    private lateinit var loginType: LoginType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        setupButton()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}