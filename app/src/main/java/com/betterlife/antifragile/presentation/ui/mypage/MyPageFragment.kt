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
import com.betterlife.antifragile.presentation.ui.mypage.viewmodel.MyPageViewModel
import com.betterlife.antifragile.presentation.ui.mypage.viewmodel.MyPageViewModelFactory
import com.betterlife.antifragile.presentation.ui.splash.SplashActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.ImageUtil.setImage
import com.betterlife.antifragile.presentation.util.TokenManager

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
                    if (memberDetailResponse.profileImgUrl == null) {
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

        setupNullObserver(
            liveData = myPageViewModel.memberLogoutResponse,
            onSuccess = {
                handleLogout()
            },
            onError = {
                Log.e("MyPageFragment", "Logout Error: ${it.errorMessage ?: "로그아웃에 실패했습니다."}")
                showCustomToast("로그아웃에 실패했습니다.")
            }
        )

        setupNullObserver(
            liveData = myPageViewModel.memberDeleteResponse,
            onSuccess = {
                handleWithdraw()
            },
            onError = {
                Log.e("MyPageFragment", "Withdraw Error: ${it.errorMessage ?: "회원탈퇴에 실패했습니다."}")
                showCustomToast("회원탈퇴에 실패했습니다.")
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

            btnModifyPassword.setOnClickListener {
                findNavController().navigate(
                    MyPageFragmentDirections.actionNavMyPageToNavPasswordEditFragment()
                )
            }

            btnLogout.setOnClickListener {
                showSelectDialog(
                    requireContext(),
                    title = "로그아웃",
                    description = "로그아웃하시겠습니까?",
                    leftButtonText = "취소하기",
                    rightButtonText = "로그아웃",
                    rightButtonListener = {
                        val refreshToken = TokenManager.getRefreshToken(requireContext())
                        if (refreshToken == null) {
                            handleLogout()
                            return@showSelectDialog
                        } else {
                            myPageViewModel.logout(refreshToken)
                        }
                    }
                )
            }

            btnWithdraw.setOnClickListener {
                showSelectDialog(
                    requireContext(),
                    title = "회원탈퇴",
                    description = "계정을 탈퇴하시겠습니까? 2주 후 데이터가 영구 삭제됩니다. 이 기간 동안 복구 가능합니다.",
                    leftButtonText = "취소하기",
                    rightButtonText = "회원탈퇴",
                    rightButtonListener = {
                        myPageViewModel.delete()
                    }
                )
            }
        }
    }

    private fun handleLogout() {
        val intent = Intent(requireActivity(), SplashActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
        TokenManager.clearTokens(requireContext())
    }

    private fun handleWithdraw() {
        val intent = Intent(requireActivity(), SplashActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
        TokenManager.clearTokens(requireContext())
    }
}