package com.betterlife.antifragile.presentation.ui.diary.handler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class EmoticonRecommendViewPagerHandler(
    private val viewPager: ViewPager2,
    private val onUpdateNavigationButtons: () -> Unit,
    viewPagerPadding: Int
) {
    var selectedPosition = 0
        private set

    private lateinit var emoticonAdapter: RecyclerView.Adapter<*>

    init {
        setupViewPager(viewPagerPadding)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        emoticonAdapter = adapter
        viewPager.adapter = emoticonAdapter
    }

    private fun setupViewPager(viewPagerPadding: Int) {
        viewPager.offscreenPageLimit = 1
        viewPager.setPageTransformer { page, position ->
            page.apply {
                val scaleFactor = calculateScaleFactor(position)
                scaleX = scaleFactor
                scaleY = scaleFactor
                translationX = calculateTranslationX(page, position, scaleFactor)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedPosition = position
                onUpdateNavigationButtons()
            }
        })

        (viewPager.getChildAt(0) as? RecyclerView)?.apply {
            setPadding(viewPagerPadding, 0, viewPagerPadding, 0)
            clipToPadding = false
        }
    }

    fun onPageSelected(position: Int) {
        selectedPosition = position
        viewPager.setCurrentItem(position, true)
        onUpdateNavigationButtons()
    }

    fun moveToPreviousPage() {
        if (selectedPosition > 0) {
            viewPager.setCurrentItem(selectedPosition - 1, true)
        }
    }

    fun moveToNextPage() {
        if (selectedPosition < emoticonAdapter.itemCount - 1) {
            viewPager.setCurrentItem(selectedPosition + 1, true)
        }
    }

    private fun calculateScaleFactor(position: Float): Float {
        return when {
            position < -1 || position > 1 -> 0.7f
            position == 0f -> 1f
            else -> 0.7f + (1 - 0.7f) * (1 - abs(position))
        }
    }

    private fun calculateTranslationX(page: View, position: Float, scaleFactor: Float): Float {
        val verticalMargin = page.height * (1 - scaleFactor) / 2
        val horizontalMargin = page.width * (1 - scaleFactor) / 2
        return if (position < 0)
            horizontalMargin - verticalMargin / 2 else -horizontalMargin + verticalMargin / 2
    }

    fun updateNavigationButtons() {
        onUpdateNavigationButtons()
    }
}
