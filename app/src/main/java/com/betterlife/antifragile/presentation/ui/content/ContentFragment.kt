package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.ContentRepository
import com.betterlife.antifragile.databinding.FragmentContentBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import java.time.LocalDate

class ContentFragment : BaseFragment<FragmentContentBinding>(R.layout.fragment_content) {

    private lateinit var contentViewModel: ContentViewModel
    private lateinit var contentAdapter: ContentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()
        setupRecyclerView()

        val today = LocalDate.now()
        contentViewModel.getContentList(today)
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(emptyList())
        binding.rvContentList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contentAdapter
        }
    }

    private fun setupViewModel() {
        // TODO: 로그인 구현 후 preference에서 토큰 가져오기
        val token = Constants.TOKEN
        val contentApiService = RetrofitInterface.createContentApiService(token)
        val contentRepository = ContentRepository(contentApiService)
        val viewModelFactory = ContentViewModelFactory(contentRepository)
        contentViewModel = ViewModelProvider(
            this, viewModelFactory
        )[ContentViewModel::class.java]

    }

    private fun setupObserver() {
        contentViewModel.contentListResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    response.data?.let { contentListResponse ->
                        contentAdapter = ContentAdapter(contentListResponse.contents)
                        binding.rvContentList.adapter = contentAdapter
                    }
                }
                Status.FAIL, Status.ERROR -> {
                    dismissLoading()
                    Log.e("ContentFragment", "Error: ${response.errorMessage}")
                }
                else -> {
                    Log.d("ContentFragment", "Unknown status: ${response.status}")
                }
            }
        }
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("내 콘텐츠")
            showLine()
        }
    }
}