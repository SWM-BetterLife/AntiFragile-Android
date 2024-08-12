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
import com.betterlife.antifragile.presentation.util.CustomToolbar
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.UUID

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

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
        val authApiService = RetrofitInterface.createAuthApiService()
        val memberApiService = RetrofitInterface.createMemberApiService()
        val authRepository = AuthRepository(authApiService)
        val memberRepository = MemberRepository(memberApiService)
        val factory = LoginViewModelFactory(authRepository, memberRepository)
        loginViewModel = factory.create(LoginViewModel::class.java)
    }

    private fun setupObserver() {
        setStatusAuthLogin()
        setStatusMemberExistence()
    }

    private fun setStatusAuthLogin() {
        loginViewModel.authLoginResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    Log.d("AuthFragment", "SUCCESS: ${response.data?.tokenIssue?.accessToken}")
                }
                Status.FAIL -> {
                    dismissLoading()
                    if (
                        response.errorMessage == CustomErrorMessage.AUTH_LOGIN_NOT_AUTHENTICATED.message
                    ) {
                        showCustomToast(response.errorMessage)
                    } else if (
                        response.errorMessage == CustomErrorMessage.MEMBER_NOT_FOUND.message
                    ) {
                        showCustomToast("해당 계정은 회원가입이 필요합니다.")
                    }
                    Log.d("AuthFragment", "FAIL: ${response.errorMessage}")

                }
                Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "로그인 실패.")
                    Log.d("AuthFragment", "ERROR: ${response.errorMessage}")
                }
                else -> {
                    Log.d("AuthFragment", "Unknown status: ${response.status}")
                }
            }
        }
    }

    private fun setStatusMemberExistence() {
        loginViewModel.memberExistenceResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    Log.d("AuthFragment", "SUCCESS: ${response.data?.isExist}")
                    if (response.data?.isExist == true) {
                        /* 이미 회원가입 되어 있는 경우 */
                        email?.let { email ->
                            loginType?.let { loginType ->
                                login(email, BuildConfig.GOOGLE_LOGIN_PASSWORD, loginType)
                            }
                        }
                    } else {
                        /* 회원 가입이 안되어 있는 경우 */

                    }
                }
                Status.FAIL -> {
                    dismissLoading()
                    Log.d("AuthFragment", "FAIL: ${response.errorMessage}")
                }
                Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "로그인 타입이 일치하지 않습니다.")
                    Log.d("AuthFragment", "ERROR: ${response.errorMessage}")
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