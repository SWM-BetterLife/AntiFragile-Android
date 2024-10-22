package com.betterlife.antifragile.presentation.ui.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.member.request.MemberPasswordModifyRequest
import com.betterlife.antifragile.databinding.FragmentPasswordEditBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.mypage.viewmodel.PasswordEditViewModel
import com.betterlife.antifragile.presentation.ui.mypage.viewmodel.PasswordEditViewModelFactory
import com.betterlife.antifragile.presentation.ui.splash.SplashActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.TokenManager

class PasswordEditFragment : BaseFragment<FragmentPasswordEditBinding>(
    R.layout.fragment_password_edit
) {
    private lateinit var passwordEditViewModel: PasswordEditViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()
        setupButton()
        setupTextWatchers()
    }

    private fun setupViewModel() {
        val factory = PasswordEditViewModelFactory(requireContext())
        passwordEditViewModel =
            ViewModelProvider(this, factory)[PasswordEditViewModel::class.java]
    }

    private fun setupObserver() {
        setupNullObserver(
            liveData = passwordEditViewModel.memberModifyPasswordResponse,
            onSuccess = {
                handleModifyPassword()
            },
            onError = {
                Log.e("MyPageFragment", "ModifyPassword Error: ${it.errorMessage ?: "비밀번호 변경에 실패했습니다."}")
                showCustomToast("비밀번호 변경에 실패했습니다.")
            }
        )
    }

    private fun setupTextWatchers() {
        binding.apply {
            etCurPassword.addTextChangedListener { validateInputs() }
            etNewPassword.addTextChangedListener { validateInputs() }
        }
    }

    private fun validateInputs() {
        binding.apply {
            val isCurPasswordValid = etCurPassword.text.toString().isNotBlank()
            val isNewPasswordValid = etNewPassword.text.toString().isNotBlank()

            val isFormValid = isCurPasswordValid && isNewPasswordValid
            val color = if (isFormValid) {
                ContextCompat.getColor(requireContext(), R.color.main_color)
            } else {
                ContextCompat.getColor(requireContext(), R.color.light_gray_2)
            }

            btnSave.apply {
                isEnabled = isFormValid
                backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

    private fun setupButton() {
        binding.apply {
            btnSave.setOnClickListener {
                passwordEditViewModel.modifyPassword(convertToPasswordModifyRequest())
            }
        }
    }

    private fun convertToPasswordModifyRequest(): MemberPasswordModifyRequest {
        return MemberPasswordModifyRequest(
            curPassword = binding.etCurPassword.text.toString(),
            newPassword = binding.etNewPassword.text.toString()
        )
    }

    private fun handleModifyPassword() {
        val intent = Intent(requireActivity(), SplashActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
        TokenManager.clearTokens(requireContext())
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("비밀번호 변경")
            showBackButton {
                requireView().findNavController().popBackStack()
            }
        }
    }
}