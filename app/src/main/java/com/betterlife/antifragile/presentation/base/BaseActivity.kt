package com.betterlife.antifragile.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.betterlife.antifragile.R
import com.betterlife.antifragile.presentation.customview.LoadingDialog
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseActivity<B : ViewDataBinding>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {
    protected lateinit var binding: B
    lateinit var toolbar: CustomToolbar
    private lateinit var loadingDialog: LoadingDialog
    private var loadingState = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        toolbar = findViewById(R.id.lo_toolbar)
        setupToolbar()
    }

    // STARTED 상태에서 코루틴을 반복 실행하는 확장 함수
    fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }

    // 로딩 다이얼로그를 표시하는 함수
    fun showLoading(context: Context) {
        if (!loadingState) {
            loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            loadingState = true
        }
    }

    // 로딩 다이얼로그를 해제하는 함수
    fun dismissLoading() {
        if (loadingState) {
            loadingDialog.dismiss()
            loadingState = false
        }
    }

    // 사용자 정의 토스트 메시지를 표시하는 함수
    fun showCustomToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    // 스낵바를 표시하는 함수
    fun showSnackBar(text: String, action: String? = null) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            action?.let {
                setAction(it) {}
            }
            show()
        }
    }

    abstract fun getLayoutResourceId(): Int
    abstract fun setupToolbar()

    override fun onDestroy() {
        super.onDestroy()
        if (loadingState) {
            loadingDialog.dismiss()
        }
    }

}