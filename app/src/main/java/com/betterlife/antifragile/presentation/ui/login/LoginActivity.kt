package com.betterlife.antifragile.presentation.ui.login

import android.os.Bundle
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.ActivityLoginBinding
import com.betterlife.antifragile.presentation.base.BaseActivity
import com.betterlife.antifragile.presentation.ui.login.oauth.GoogleLogin
import java.security.MessageDigest
import java.util.UUID

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private lateinit var googleLogin: GoogleLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleLogin = GoogleLogin(this)

        binding.btnGoogleLogin.setOnClickListener { startGoogleLogin() }
    }

    override fun getLayoutResourceId() = R.layout.activity_login

    override fun setupToolbar() {
        toolbar.apply {
            reset()
        }
    }

    private fun startGoogleLogin() {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        googleLogin.startGoogleLogin(hashedNonce)
    }
}