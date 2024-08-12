package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentRecommendContentBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentRecommendViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.RecommendDialogUtil
import java.time.LocalDate

class RecommendContentFragment : BaseFragment<FragmentRecommendContentBinding>(
    R.layout.fragment_recommend_content
) {

    private lateinit var contentRecommendViewModel: ContentRecommendViewModel
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var diaryDateString: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupRecyclerView()

        diaryDateString = arguments?.getString("diaryDate").toString()
        val isNewDiary = arguments?.getBoolean("isNewDiary") ?: false
        val feedback = arguments?.getString("feedback")

        val diaryDate = diaryDateString.let { LocalDate.parse(it) }

        // 로직 구성
        diaryDate?.let { date ->
            when {
                isNewDiary -> {
                    // 신규 일기 생성 -> 추천 API 호출
                    contentRecommendViewModel.getRecommendContents(null, date)
                }
                !isNewDiary && feedback == null -> {
                    // 기존 일기 -> 조회 API 호출
                    contentRecommendViewModel.getContentList(date)
                }
                !isNewDiary && feedback != null -> {
                    // 재추천 -> 재추천 API 호출
                    contentRecommendViewModel.getRecommendContents(feedback, date)
                }
                else -> {
                    Log.e("RecommendContentFragment", "Unhandled case for isNewDiary and feedback")
                }
            }
        } ?: run {
            Log.e("RecommendContentFragment", "Invalid or missing diaryDate")
        }
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(emptyList()) { content ->
            val action = RecommendContentFragmentDirections.
                actionContentRecommendFragmentToContentDetailFragment(content.id, diaryDateString)
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
                    val action = RecommendContentFragmentDirections.
                        actionContentRecommendFragmentToContentDetailFragment(content.id, diaryDateString)
                    findNavController().navigate(action)
                }
                binding.rvContentList.adapter = contentAdapter
                binding.tvDate.text = DateUtil.convertDateToFullFormat(diaryDateString)
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
                        // TODO: 콘텐츠 재추천 받기 API 호출
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
            showBackButton {
                findNavController().popBackStack()
            }
            showCustomButton(R.drawable.btn_re) {
                contentRecommendViewModel.getRemainRecommendNumber()
            }
        }
    }

    private fun setupViewModel() {
        val factory = ContentRecommendViewModelFactory(Constants.TOKEN)
        contentRecommendViewModel =
            ViewModelProvider(this, factory)[ContentRecommendViewModel::class.java]
    }

}