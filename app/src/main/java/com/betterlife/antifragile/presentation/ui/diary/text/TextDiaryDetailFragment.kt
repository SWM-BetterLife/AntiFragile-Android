package com.betterlife.antifragile.presentation.ui.diary.text

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.model.common.Emotion
import com.betterlife.antifragile.data.model.diary.TextDiaryDetail
import com.betterlife.antifragile.databinding.FragmentTextDiaryDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.TextDiaryViewModel
import com.betterlife.antifragile.presentation.ui.diary.viewmodel.TextDiaryViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.bumptech.glide.Glide

class TextDiaryDetailFragment: BaseFragment<FragmentTextDiaryDetailBinding>(
    R.layout.fragment_text_diary_detail
) {

    private lateinit var textDiaryViewModel: TextDiaryViewModel
    private var diaryDate: String? = null
    private var textDiaryDetail: TextDiaryDetail? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryDate = getDiaryDateFromArguments()
        val diaryId = getDiaryIdFromArguments()

        setupViewModel()
        setupObservers()
        loadDiaryData(diaryId, diaryDate!!)
        setupButton()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle(DateUtil.convertDateFormat(diaryDate!!))
            showBackButton() {
                findNavController().popBackStack()
            }
            showCustomButton(R.drawable.btn_edit) {
                val action =
                    TextDiaryDetailFragmentDirections.actionNavTextDiaryDetailToNavTextDiaryCreate(
                    diaryDate!!, textDiaryDetail
                )
                findNavController().navigate(action)
            }
            showLine()
        }
    }

    private fun setupViewModel() {
        val factory = TextDiaryViewModelFactory(requireContext(), Constants.TOKEN)
        textDiaryViewModel = ViewModelProvider(this, factory)[TextDiaryViewModel::class.java]
    }

    private fun loadDiaryData(id: Int, date: String) {
        textDiaryViewModel.getTextDiaryDetail(id, date)
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        textDiaryViewModel.textDiaryDetail.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    response.data?.let { textDiaryDetail ->
                        this.textDiaryDetail = textDiaryDetail
                        binding.apply {
                            tvDiaryContent.text = textDiaryDetail.content
                            tvEmotion.text = Emotion.fromString(
                                textDiaryDetail.emoticonInfo?.emotion
                            ).toKorean
                            textDiaryDetail.emoticonInfo?.let { emoticon ->
                                Glide.with(requireContext())
                                    .load(emoticon.imgUrl)
                                    .into(ivEmoticon)
                            }
                            setEmotionBackground(
                                loEmoticon, textDiaryDetail.emoticonInfo?.emotion ?: "오류"
                            )
                        }
                    }
                }
                Status.FAIL, Status.ERROR -> {
                    binding.apply {
                        tvDiaryContent.text = "일기를 불러오는 데 실패했습니다."
                        ivEmoticon.setImageResource(R.drawable.emoticon_blank)
                        setEmotionBackground(
                            loEmoticon, textDiaryDetail?.emoticonInfo?.emotion ?: "오류"
                        )
                    }
                    showCustomToast(response.errorMessage ?: "일기를 불러오는 데 실패했습니다.")
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().popBackStack()
                    }, 1000)
                }
                else -> {
                    Log.d("TextDiaryDetailFragment", "Unknown status: ${response.status}")
                }
            }
        }
    }

    private fun setEmotionBackground(layout: ConstraintLayout, emotion: String) {
        layout.setBackgroundResource(Emotion.fromString(emotion).getBackgroundResource())
    }

    private fun setupButton() {
        binding.btnMoveContent.setOnClickListener {
            val action =
                TextDiaryDetailFragmentDirections.actionNavTextDiaryDetailToNavRecommendContent(
                diaryDate!!, false
            )
            findNavController().navigate(action)
        }
    }

    private fun getDiaryDateFromArguments(): String {
        return TextDiaryDetailFragmentArgs.fromBundle(requireArguments()).diaryDate
    }

    private fun getDiaryIdFromArguments(): Int {
        return TextDiaryDetailFragmentArgs.fromBundle(requireArguments()).diaryId
    }
}