package com.betterlife.antifragile.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.BuildConfig
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.auth.request.AuthLoginRequest
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.model.enums.LoginType.GOOGLE
import com.betterlife.antifragile.data.model.enums.LoginType.NORMAL
import com.betterlife.antifragile.data.model.enums.MemberStatus
import com.betterlife.antifragile.databinding.FragmentLoginBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.oauth.GoogleLogin
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.LoginViewModel
import com.betterlife.antifragile.presentation.ui.auth.viewmodel.LoginViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.TokenManager.saveTokens
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.UUID

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    R.layout.fragment_login
) {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var googleLogin: GoogleLogin
    private var email: String? = null
    private var password: String? = null
    private var loginType: LoginType? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        setupViewModel()
        setupObserver()
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
        }
    }

    private fun setVariables() {
        googleLogin = GoogleLogin(requireContext())
    }

    private fun setupViewModel() {
        val factory = LoginViewModelFactory()
        loginViewModel = factory.create(LoginViewModel::class.java)
    }

    private fun setupObserver() {
        setStatusAuthLogin()
        setStatusMemberExistence()
    }

    private fun setStatusMemberExistence() {
        setupBaseObserver(
            liveData = loginViewModel.memberExistenceResponse,
            onSuccess = {
                when(it.status) {
                    MemberStatus.EXISTENCE -> {
                        login(email!!, password!!, loginType!!)
                    }
                    MemberStatus.NOT_EXISTENCE -> {
                        navigateToTermsFragment(email!!, loginType!!)
                    }
                    MemberStatus.HUMAN -> {
                        showSelectDialog(
                            requireContext(),
                            title = "휴먼 계정 해제",
                            description = "이 계정은 탈퇴된 상태입니다. 해제하지 않으면 일정 기간 후 완전히 삭제됩니다. 휴먼 상태를 해제하시겠습니까?",
                            leftButtonText = "취소하기",
                            rightButtonText = "해제하기",
                            rightButtonListener = {
                                login(email!!, password!!, loginType!!)
                            }
                        )
                    }
                }
            },
            onError = {
                showCustomToast(it.errorMessage ?: "로그인 타입이 일치하지 않습니다.")
                Log.d("AuthFragment", "ERROR: ${it.errorMessage}")
            }

        )
    }

    private fun setStatusAuthLogin() {
        setupBaseObserver(
            liveData = loginViewModel.authLoginResponse,
            onSuccess = {
                saveTokens(requireContext(), it.tokenIssue.accessToken, it.tokenIssue.refreshToken)
                navigateToMainActivity()            },
            onError = {
                when (it.errorMessage) {
                    CustomErrorMessage.AUTH_LOGIN_NOT_AUTHENTICATED.message -> {
                        showCustomToast(it.errorMessage)
                    }
                    CustomErrorMessage.MEMBER_NOT_FOUND.message -> {
                        showCustomToast("해당 계정은 회원가입이 필요합니다.")
                    }
                    else -> {
                        showCustomToast(it.errorMessage ?: "로그인 실패.")
                    }
                }
            }
        )
    }

    private fun setupButton() {
        binding.apply {
            btnGoogleLogin.setOnClickListener {
                startGoogleLogin()
            }

            btnLogin.setOnClickListener {
                email = etEmail.text.toString()
                password = etPassword.text.toString()
                loginType = NORMAL

                if (validateInput(email, password)) {
                    checkMemberExistence(email!!, loginType!!)
                }
            }

            tvSignup.setOnClickListener {
                navigateToTermsFragment("", NORMAL)
            }
        }
    }


    private fun validateInput(email: String?, password: String?): Boolean {
        when {
            email.isNullOrEmpty() -> {
                showCustomToast("아이디를 입력해주세요.")
                return false
            }
            password.isNullOrEmpty() -> {
                showCustomToast("비밀번호를 입력해주세요.")
                return false
            }
            else -> return true
        }
    }

    private fun startGoogleLogin() {
        val hashedNonce = generateNonce()

        email = runBlocking {
            googleLogin.startGoogleLogin(hashedNonce)
        }

        password = BuildConfig.GOOGLE_LOGIN_PASSWORD

        loginType = GOOGLE

        if (email != null) {
            checkMemberExistence(email!!, loginType!!)
        } else {
            Log.d("LoginFragment", "Google Login failed")
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

    private fun navigateToTermsFragment(email: String, loginType: LoginType) {
        findNavController().navigate(
            LoginFragmentDirections.actionNavLoginFragmentToNavTermsFragment(email, loginType)
        )
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes).fold("") { str, it -> str + "%02x".format(it) }
    }
}