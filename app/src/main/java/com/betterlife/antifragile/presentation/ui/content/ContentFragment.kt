package com.betterlife.antifragile.presentation.ui.content

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentContentBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentViewModelFactory
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken
import java.time.LocalDate

class ContentFragment : BaseFragment<FragmentContentBinding>(R.layout.fragment_content) {

    private lateinit var contentViewModel: ContentViewModel
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var navController: NavController
    private var today = DateUtil.getTodayDate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupViewModel()
        setupObservers()
        setupRecyclerView()

        contentViewModel.getContentList(today)
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(emptyList()) { content ->
            val action = ContentFragmentDirections.
            actionContentFragmentToContentDetailFragment(content.id, today)
            navController.navigate(action)
        }
        binding.rvContentList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contentAdapter
        }
    }

    private fun setupViewModel() {
        val factory = ContentViewModelFactory(requireContext())
        contentViewModel = factory.create(ContentViewModel::class.java)
    }

    private fun setupObservers() {
        setupBaseObserver(
            liveData = contentViewModel.contentListResponse,
            onSuccess = { contentListResponse ->
                contentAdapter = ContentAdapter(contentListResponse.contents) { content ->
                    val action = ContentFragmentDirections.
                    actionContentFragmentToContentDetailFragment(content.id, today)
                    navController.navigate(action)
                }
                binding.rvContentList.adapter = contentAdapter
            },
            onError = {
                Log.e("ContentFragment", "Error: ${it.errorMessage}")
            }
        )
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setMainTitle("내 콘텐츠")
            showLine()
        }
    }
}