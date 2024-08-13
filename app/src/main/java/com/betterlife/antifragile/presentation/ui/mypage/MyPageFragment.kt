package com.betterlife.antifragile.presentation.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.databinding.FragmentMyPageBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.AuthActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.ImageUtil.setImage

class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {

    private lateinit var myPageViewModel: MyPageViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()
        loadMemberData()
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("마이페이지")
            showLine()
        }
    }

    private fun setupViewModel() {
        val factory = MyPageViewModelFactory(requireContext())
        myPageViewModel =
            ViewModelProvider(this, factory)[MyPageViewModel::class.java]
    }

    private fun setupObserver() {
        setupBaseObserver(
            liveData = myPageViewModel.memberDetailResponse,
            onSuccess = { memberDetailResponse ->
                setupMemberVisibility()
                binding.apply {
                    tvNickname.text = memberDetailResponse.nickname
                    tvEmail.text = memberDetailResponse.email
                    tvDiaryTotalNum.text = getString(
                        R.string.diary_total_num_format, memberDetailResponse.diaryTotalNum
                    )
                    if (memberDetailResponse.profileImgUrl.isEmpty()) {
                        ivProfileImg.setImageResource(R.drawable.ic_member_default_profile)
                    } else {
                        ivProfileImg.setImage(memberDetailResponse.profileImgUrl)
                    }
                }
            },
            onError = {
                Log.e("MyPageFragment", "Error: ${it.errorMessage}")
            }
        )
    }

    private fun setupMemberVisibility() {
        binding.apply {
            tvNickname.visibility = View.VISIBLE
            tvEmail.visibility = View.VISIBLE
            tvDiaryTotalNum.visibility = View.VISIBLE
        }
    }

    private fun loadMemberData() {
        myPageViewModel.getMemberDetail()
    }

    private fun setupButton() {
        binding.apply {
            btnUpdate.setOnClickListener {
                findNavController().navigate(
                    MyPageFragmentDirections.actionNavMyPageToNavProfileEditFragment(
                        "",
                        LoginType.GOOGLE,
                        false
                    )
                )
            }

            btnLogout.setOnClickListener {
                myPageViewModel.logout(
                    onSuccess = {
                        handleLogout()
                    },
                    onError = { errorMessage ->
                        Log.e("MyPageFragment", "Logout Error: $errorMessage")
                        showCustomToast(errorMessage)
                    }
                )
            }

            btnWithdraw.setOnClickListener {
                myPageViewModel.delete(
                    onSuccess = {
                        handleWithdraw()
                    },
                    onError = { errorMessage ->
                        Log.e("MyPageFragment", "Withdraw Error: $errorMessage")
                        showCustomToast(errorMessage)
                    }
                )
            }
        }
    }

    private fun handleLogout() {
        deleteToken()

        val intent = Intent(requireActivity(), AuthActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleWithdraw() {
        deleteToken()

        val intent = Intent(requireActivity(), AuthActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
    }

    private fun deleteToken() {
        // TODO: 토큰 삭제 등의 로그아웃 후처리 작업 수행
    }
}