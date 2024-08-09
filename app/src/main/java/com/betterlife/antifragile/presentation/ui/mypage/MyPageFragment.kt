package com.betterlife.antifragile.presentation.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Button
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.CustomErrorMessage
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.MemberRepository
import com.betterlife.antifragile.databinding.FragmentMyPageBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.login.LoginActivity
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar


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
            showCustomButton {
                // TODO: 알림 버튼 클릭 처리
            }
            showLine()
        }
    }

    private fun setupViewModel() {
        // TODO: 로그인 구현 후 preference에서 토큰 가져오기
        val token = Constants.TOKEN
        val memberApiService = RetrofitInterface.createMemberApiService(token)
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
                        tvNickname.text = response.data?.nickname
                        tvEmail.text = response.data?.email
                        tvDiaryTotalNum.text = response.data?.diaryTotalNum.toString()
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
        customLogoutBtn()
        customWithdrawBtn()

        binding.apply {
            btnUpdate.setOnClickListener {
                // TODO: 나의 정보 수정
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

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        requireActivity().supportFragmentManager.popBackStack()
        startActivity(intent)
        requireActivity().finish()
    }

    private fun logout() {
        // TODO: 로그아웃 구현
    }

    private fun customWithdrawBtn() {
        val btnWithdraw = view?.findViewById<Button>(R.id.btn_withdraw) ?: return
        val text = btnWithdraw.text.toString()
        val spannableString = SpannableString(text).apply {
            setSpan(UnderlineSpan(), 0, text.length, 0)
        }
        btnWithdraw.text = spannableString
    }

    private fun customLogoutBtn() {
        val btnLogout= view?.findViewById<Button>(R.id.btn_logout) ?: return
        val text = btnLogout.text.toString()
        val spannableString = SpannableString(text).apply {
            setSpan(UnderlineSpan(), 0, text.length, 0)
        }
        btnLogout.text = spannableString
    }
}