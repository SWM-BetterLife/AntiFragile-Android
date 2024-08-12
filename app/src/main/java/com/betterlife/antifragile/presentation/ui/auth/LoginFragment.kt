package com.betterlife.antifragile.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import com.betterlife.antifragile.BuildConfig
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.enums.LoginType.GOOGLE
import com.betterlife.antifragile.data.repository.AuthRepository
import com.betterlife.antifragile.data.repository.MemberRepository
import com.betterlife.antifragile.databinding.FragmentLoginBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.oauth.GoogleLogin
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.LoginViewModel
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.LoginViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.UUID

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    R.layout.fragment_login
) {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var googleLogin: GoogleLogin
    private var email: String? = null
    private var loginType: LoginType? = null

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
        val factory = LoginViewModelFactory(Constants.TOKEN)
        loginViewModel = factory.create(LoginViewModel::class.java)
    }

    private fun setupObserver() {
        setStatusAuthLogin()
        setStatusMemberExistence()
    }

    private fun setStatusAuthLogin() {
        setupBaseObserver(
            liveData = loginViewModel.authLoginResponse,
            onSuccess = {
                Log.d("AuthFragment", "SUCCESS: ${it.tokenIssue.accessToken}")
            },
            onError = {
                if (it.errorMessage == CustomErrorMessage.AUTH_LOGIN_NOT_AUTHENTICATED.message) {
                    showCustomToast(it.errorMessage)
                } else if (it.errorMessage == CustomErrorMessage.MEMBER_NOT_FOUND.message) {
                    showCustomToast("해당 계정은 회원가입이 필요합니다.")
                } else {
                    showCustomToast(it.errorMessage ?: "로그인 실패.")
                }
            }
        )
    }

    private fun setStatusMemberExistence() {
        setupBaseObserver(
            liveData = loginViewModel.memberExistenceResponse,
            onSuccess = {
                if (it.isExist == true) {
                    // 이미 회원가입되어 있는 경우 -> 로그인 처리
                    email?.let { email ->
                        loginType?.let { loginType ->
                            login(email, BuildConfig.GOOGLE_LOGIN_PASSWORD, loginType)
                        }
                    }
                } else {
                    // 회원가입이 안되어 있는 경우 -> 회원가입 처리
                }
            },
            onError = {
                showCustomToast(it.errorMessage ?: "로그인 타입이 일치하지 않습니다.")
                Log.d("AuthFragment", "ERROR: ${it.errorMessage}")
            }
        )
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

        email = runBlocking {
            googleLogin.startGoogleLogin(hashedNonce)
        }
        loginType = GOOGLE

        if (email != null) {
            Log.d("TAG", "Google ID: $email")
            //todo: 예외 처리
        } else {
            Log.d("TAG", "Google Login failed")
        }

        email?.let {
            checkMemberExistence(it, loginType!!)
        }
    }

    private fun checkMemberExistence(email: String, loginType: LoginType) {
        loginViewModel.checkMemberExistence(email, loginType)
    }

    private fun login(email: String, password: String, loginType: LoginType) {
        val request = createAuthLoginRequest(email, password, loginType)
        loginViewModel.login(request)
    }

    private fun createAuthLoginRequest(
        email: String,
        password: String,
        loginType: LoginType
    ): AuthLoginRequest {
        return AuthLoginRequest(email, password, loginType)
    }

}