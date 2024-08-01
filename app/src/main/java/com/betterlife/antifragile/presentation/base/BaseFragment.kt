package com.betterlife.antifragile.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.betterlife.antifragile.presentation.customview.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : Fragment(), ToolbarConfigurable {
    private var _binding: B? = null
    protected val binding get() = _binding!!

    private lateinit var loadingDialog: LoadingDialog
    private var loadingState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? BaseActivity<*>)?.toolbar?.let { configureToolbar(it) }
    }

    // STARTED 상태에서 코루틴을 반복 실행하는 확장 함수
    fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
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
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    // 스낵바를 표시하는 함수
    fun showSnackBar(text: String, action: String? = null) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        ).apply {
            action?.let {
                setAction(it) {}
            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (loadingState) {
            loadingDialog.dismiss()
        }
        _binding = null
    }

}