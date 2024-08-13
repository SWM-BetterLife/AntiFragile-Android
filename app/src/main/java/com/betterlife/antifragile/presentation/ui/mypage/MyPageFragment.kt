package com.betterlife.antifragile.presentation.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.enums.LoginType
import com.betterlife.antifragile.data.repository.MemberRepository
import com.betterlife.antifragile.databinding.FragmentMyPageBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.auth.AuthActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.ImageUtil.setImage
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken


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
        val memberApiService =
            RetrofitInterface.createMemberApiService(getAccessToken(requireContext())!!)
        val memberRepository = MemberRepository(memberApiService)
        val factory = MyPageViewModelFactory(memberRepository)
        myPageViewModel = factory.create(MyPageViewModel::class.java)
    }

    private fun setupObserver() {
        myPageViewModel.memberDetailResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    setupMemberVisibility(true)
                    binding.apply {
                        tvNickname.text = response.data?.nickname ?: "-"
                        tvEmail.text = response.data?.email ?: "-"
                        tvDiaryTotalNum.text = response.data?.diaryTotalNum.toString() + " 일"
                        if (response.data?.profileImgUrl.isNullOrEmpty()) {
                            ivProfileImg.setImageResource(R.drawable.ic_member_default_profile)
                        } else {
                            ivProfileImg.setImage(response.data?.profileImgUrl)
                        }
                    }
                }
                Status.FAIL -> {
                    dismissLoading()
                    if (
                        response.errorMessage == CustomErrorMessage.MEMBER_NOT_FOUND.message
                    ) {
                        setupMemberVisibility(false)
                    } else {
                        showCustomToast(response.errorMessage ?: "내 정보를 불러오는데 실패했습니다.")
                    }
                }
                Status.ERROR -> {
                    dismissLoading()
                    showCustomToast(response.errorMessage ?: "내 정보를 불러오는데 실패했습니다.")
                }
                else -> {
                    Log.d("MyPageFragment", "Unknown status: ${response.status}")
                }
            }
        }
    }

    private fun setupMemberVisibility(isActive: Boolean) {
        binding.apply {
            val activeVisibility = if (isActive) View.VISIBLE else View.GONE

            tvNickname.visibility = activeVisibility
            tvEmail.visibility = activeVisibility
            tvDiaryTotalNum.visibility = activeVisibility
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
                //  TODO: 로그아웃
                handleLogout()
            }

            btnWithdraw.setOnClickListener {
                // TODO: 회원 탈퇴
            }
        }
    }

    private fun handleLogout() {
        logout()

        val intent = Intent(requireActivity(), AuthActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
    }

    private fun logout() {
        // TODO: 로그아웃 구현
    }
}