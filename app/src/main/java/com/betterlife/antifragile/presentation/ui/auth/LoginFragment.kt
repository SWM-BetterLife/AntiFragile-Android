package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import com.betterlife.antifragile.BuildConfig
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.auth.AuthSignUpRequest
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.databinding.FragmentLoginBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.oauth.GoogleLogin
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.LoginViewModel
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.LoginViewModelFactory
import com.betterlife.antifragile.presentation.util.CustomToolbar
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.UUID

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var googleLogin: GoogleLogin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()
        setupButton()

        googleLogin = GoogleLogin(requireContext())
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
        }
    }

    private fun setupViewModel() {
        val authApiService = RetrofitInterface.createAuthApiService()
        val authRepository = AuthRepository(authApiService)
        val factory = LoginViewModelFactory(authRepository)
        loginViewModel = factory.create(LoginViewModel::class.java)
    }

    private fun setupObserver() {
        loginViewModel.authSignUpResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                }
                Status.FAIL -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "회원가입 실패.")
                    Log.d("AuthFragment", "FAIL: ${response.errorMessage}")
                    //todo: 회원가입 실패시 처리
                }
                Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "회원가입 실패.")
                }
                else -> {
                    Log.d("AuthFragment", "Unknown status: ${response.status}")
                }
            }
        }
    }

    private fun setupButton() {
        binding.apply {
            btnGoogleLogin.setOnClickListener {
                startGoogleLogin()
            }
        }
    }

    private fun startGoogleLogin() {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleId = runBlocking {
            googleLogin.startGoogleLogin(hashedNonce)
        }

        if (googleId != null) {
            Log.d("TAG", "Google ID: $googleId")
        } else {
            Log.d("TAG", "Google Login failed")
        }

        googleId?.let {
            signUp(it, LoginType.GOOGLE)
        }
    }

    private fun signUp(email: String, loginType: LoginType) {
        val signUpRequest = createAuthSignUpRequest(email, loginType)
        loginViewModel.signUp(signUpRequest)
    }

    private fun createAuthSignUpRequest(
        email: String,
        loginType: LoginType
    ): AuthSignUpRequest {
        return AuthSignUpRequest(email, BuildConfig.GOOGLE_LOGIN_PASSWORD, loginType)
    }

}