package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentContentRecommendBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModelFactory
import com.betterlife.antifragile.presentation.ui.main.MainActivity
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.RecommendDialogUtil

class ContentRecommendFragment : BaseFragment<FragmentContentRecommendBinding>(
    R.layout.fragment_content_recommend
) {

    private lateinit var contentRecommendViewModel: ContentRecommendViewModel
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var diaryDate: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupRecyclerView()

        diaryDate = arguments?.getString("diaryDate").toString()
        contentRecommendViewModel.getContentList(diaryDate)

        (activity as MainActivity).showBottomNavigation()
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(emptyList()) { content ->
            val action = ContentRecommendFragmentDirections.
                actionContentRecommendFragmentToContentDetailFragment(content.id, diaryDate)
            findNavController().navigate(action)
        }
        binding.rvContentList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contentAdapter
        }
    }

    private fun setupObservers() {
        setupBaseObserver(
            liveData = contentRecommendViewModel.contentResponse,
            onSuccess = { contentListResponse ->
                contentAdapter = ContentAdapter(contentListResponse.contents) { content ->
                    val action = ContentRecommendFragmentDirections.
                        actionContentRecommendFragmentToContentDetailFragment(content.id, diaryDate)
                    findNavController().navigate(action)
                }
                binding.rvContentList.adapter = contentAdapter
                binding.tvDate.text = DateUtil.convertDateToFullFormat(diaryDate)
            },
            onError = {
                Log.e("ContentFragment", "Error: ${it.errorMessage}")
            }
        )
        setupBaseObserver(
            liveData = contentRecommendViewModel.remainRecommendNumber,
            onSuccess = { response ->
                RecommendDialogUtil.showRecommendDialogs(
                    fragment = this,
                    remainNumber = response.remainNumber,
                    onLeftButtonClicked = { },
                    onRightButtonFeedbackProvided = { feedback ->
                        contentRecommendViewModel.getReRecommendContents(diaryDate, feedback)
                    },
                    onExcessRemainNumber = { }
                )
            },
            onError = {
                showCustomToast(it.errorMessage ?: "남은 추천 횟수 조회에 실패했습니다.")
            }
        )
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("맞춤형 콘텐츠")

            if (arguments?.getBoolean("isBackActive") == true) {
                showBackButton {
                    findNavController().popBackStack()
                }
            }

            showCustomButton(R.drawable.btn_re) {
                contentRecommendViewModel.getRemainRecommendNumber()
            }
        }
    }

    private fun setupViewModel() {
        val factory = ContentRecommendViewModelFactory(requireContext())
        contentRecommendViewModel = factory.create(ContentRecommendViewModel::class.java)
    }
}