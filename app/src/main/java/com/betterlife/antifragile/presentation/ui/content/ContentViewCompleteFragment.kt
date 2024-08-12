package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.diary.DiaryType
import com.betterlife.antifragile.databinding.FragmentContentViewCompleteBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentViewCompleteViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentViewCompleteViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.RecommendDialogUtil

class ContentViewCompleteFragment : BaseFragment<FragmentContentViewCompleteBinding>(
    R.layout.fragment_content_view_complete
) {

    private lateinit var contentViewCompleteViewModel: ContentViewCompleteViewModel
    private lateinit var diaryDate: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        setupViewModel()
        setupObservers()
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("시청 완료")
            showBackButton {
                findNavController().popBackStack()
            }
        }
    }

    private fun setVariables() {
        diaryDate = ContentViewCompleteFragmentArgs.fromBundle(requireArguments()).diaryDate
    }

    private fun setupViewModel() {
        val factory = ContentViewCompleteViewModelFactory(requireContext(), Constants.TOKEN)
        contentViewCompleteViewModel =
            ViewModelProvider(this, factory)[ContentViewCompleteViewModel::class.java]
    }

    private fun setupObservers() {
        setupBaseObserver(
            liveData = contentViewCompleteViewModel.remainRecommendNumber,
            onSuccess = { response ->
                RecommendDialogUtil.showRecommendDialogs(
                    fragment = this,
                    remainNumber = response.remainNumber,
                    onLeftButtonClicked = { },
                    onRightButtonFeedbackProvided = { feedback ->
                        findNavController().navigate(
                            ContentViewCompleteFragmentDirections
                                .actionNavContentViewCompleteToNavRecommendContent(
                                    diaryDate, false ,feedback
                                )
                        )
                    },
                    onExcessRemainNumber = {
                        navigateToContentList()
                    }
                )
            },
            onError = {
                showCustomToast(it.errorMessage ?: "남은 추천 횟수 조회에 실패했습니다.")
            }
        )

        setupBaseObserver(
            liveData = contentViewCompleteViewModel.diaryInfo,
            onSuccess = {
                when(it?.diaryType) {
                    DiaryType.TEXT -> findNavController().navigate(
                        ContentViewCompleteFragmentDirections
                            .actionNavContentViewCompleteToNavTextDiaryDetail(
                                it.id, diaryDate
                            )
                    )
                    DiaryType.QUESTION -> findNavController().navigate(
                        ContentViewCompleteFragmentDirections
                            .actionNavContentViewCompleteToNavQuestionDiaryDetail(
                                it.id, diaryDate
                            )
                    )
                    else -> showCustomToast("일기 정보를 불러오는데 실패했습니다.")
                }
            },
            onError = {
                showCustomToast("일기 정보를 불러오는데 실패했습니다.")
            }
        )
    }

    private fun setupButton() {
        binding.btnReRecommend.setOnClickListener {
            contentViewCompleteViewModel.getRemainRecommendNumber()
        }

        binding.btnMoveToSelectContent.setOnClickListener {
            navigateToContentList()
        }

        binding.btnMoveToDiary.setOnClickListener {
            contentViewCompleteViewModel.getDiaryInfoByDate(diaryDate)
        }
    }

    private fun navigateToContentList() {
        val navController = findNavController()
        if (!navController.popBackStack(R.id.nav_content, false)) {
            navController.popBackStack(R.id.nav_recommend_content, false)
        }
    }
}