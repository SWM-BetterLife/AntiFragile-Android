package com.betterlife.antifragile.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.ActivityLoginBinding
import com.betterlife.antifragile.presentation.base.BaseActivity
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.google.android.gms.common.SignInButton

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setGoogleButtonText(binding.btnGoogleLogin, "Google 로그인")
        binding.btnGoogleLogin.setOnClickListener { startGoogleLogin() }
    }

    override fun getLayoutResourceId() = R.layout.activity_login

    override fun setupToolbar() {
        toolbar.apply {
            reset()
            setSubTitle("로그인")
        }
    }

    private fun setGoogleButtonText(loginButton: SignInButton, buttonText: String) {
        for (i in 0 until loginButton.childCount) {
            (loginButton.getChildAt(i) as? TextView)?.apply {
                text = buttonText
                gravity = Gravity.CENTER
                return
            }
        }
    }

    private fun startGoogleLogin() {
        val url = "https://dev.better-life-api.com/oauth2/authorization/google"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}