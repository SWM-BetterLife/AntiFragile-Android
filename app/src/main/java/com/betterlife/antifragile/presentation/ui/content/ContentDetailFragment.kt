package com.betterlife.antifragile.presentation.ui.content

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.config.RetrofitInterface
import com.betterlife.antifragile.data.model.base.Status
import com.betterlife.antifragile.data.repository.ContentRepository
import com.betterlife.antifragile.databinding.FragmentContentDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentDetailViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentDetailViewModelFactory
import com.betterlife.antifragile.presentation.util.Constants
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.ImageUtil.loadImageCircle
import java.util.regex.Pattern

class ContentDetailFragment : BaseFragment<FragmentContentDetailBinding>(
    R.layout.fragment_content_detail
) {

    private lateinit var contentDetailViewModel: ContentDetailViewModel

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUiVisibility: Int = 0
    private var originalOrientation: Int = 0
    private var isVideoEndedHandled = false
    private var currentVideoPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupWebView()
        setupObservers()

        val contentId = arguments?.getString("contentId")
        val diaryDate = arguments?.getString("diaryDate")
        if (contentId != null && diaryDate != null) {
            contentDetailViewModel.getContentDetail(contentId)
        } else {
            // contentId나 date가 없을 경우 처리할 로직 추가
            Log.e("ContentDetailFragment", "Missing contentId or date")
        }
    }

    private fun setupViewModel() {
        // TODO: 로그인 구현 후, preference나 다른 방법으로 token을 받아와야 함
        val token = Constants.TOKEN
        val contentApiService = RetrofitInterface.createContentApiService(token)
        val contentRepository = ContentRepository(contentApiService)
        val viewModelFactory = ContentDetailViewModelFactory(contentRepository)
        contentDetailViewModel = ViewModelProvider(
            this, viewModelFactory
        )[ContentDetailViewModel::class.java]
    }

    private fun setupObservers() {
        contentDetailViewModel.contentDetailResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    showLoading(requireContext())
                }
                Status.SUCCESS -> {
                    dismissLoading()
                    response.data?.let { contentDetailResponse ->
                        val videoId = extractVideoIdFromUrl(contentDetailResponse.url)
                        if (videoId != null) {
                            loadYouTubeVideo(videoId)
                            binding.apply {
                                tvTitle.text = contentDetailResponse.title
                                tvContentDescription.text = contentDetailResponse.description
                                tvLikeCount.text = contentDetailResponse.likeNumber.toString()
                                tvChannelName.text = contentDetailResponse.channel.name
                                tvSubscribeCount.text = contentDetailResponse.channel.subscribeNumber.toString()
                                ivChannelProfile.loadImageCircle(contentDetailResponse.channel.img)
                            }
                        } else {
                            showErrorMessage()
                        }
                    }
                }
                Status.FAIL, Status.ERROR -> {
                    dismissLoading()
                    Log.e("ContentDetailFragment", "Error: ${response.errorMessage}")
                }
                else -> {
                    Log.d("ContentDetailFragment", "Unknown status: ${response.status}")
                }
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.wvContent.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        binding.wvContent.webViewClient = WebViewClient()
        binding.wvContent.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customView = view
                customViewCallback = callback

                originalSystemUiVisibility = activity?.window?.decorView?.systemUiVisibility ?: 0
                originalOrientation = activity?.requestedOrientation ?: 0

                (activity?.window?.decorView as? FrameLayout)?.addView(
                    customView,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )

                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }

            override fun onHideCustomView() {
                if (customView != null) {
                    (activity?.window?.decorView as? FrameLayout)?.removeView(customView)
                    customView = null
                    customViewCallback?.onCustomViewHidden()

                    activity?.window?.decorView?.systemUiVisibility = originalSystemUiVisibility
                    activity?.requestedOrientation = originalOrientation
                }
            }
        }

        binding.wvContent.addJavascriptInterface(WebAppInterface(), "Android")
    }

    private fun loadYouTubeVideo(videoId: String) {
        val html = """
            <!DOCTYPE html>
            <html>
            <body style="margin:0;padding:0;">
                <div id="player"></div>
                <script>
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                    var player;
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$videoId',
                            events: {
                                'onReady': onPlayerReady,
                                'onStateChange': onPlayerStateChange
                            },
                            playerVars: {
                                'playsinline': 1,
                                'controls': 1,
                                'fs': 1
                            }
                        });
                    }

                    function onPlayerReady(event) {
                        event.target.playVideo();
                    }

                    function onPlayerStateChange(event) {
                        if (event.data == YT.PlayerState.ENDED) {
                            Android.onVideoEnded();
                            Android.exitFullScreen();
                        }
                        Android.onVideoTimeUpdate(player.getCurrentTime());
                    }

                    setInterval(function() {
                        if (player && player.getCurrentTime) {
                            Android.onVideoTimeUpdate(player.getCurrentTime());
                        }
                    }, 1000);
                </script>
            </body>
            </html>
        """.trimIndent()

        binding.wvContent.loadData(html, "text/html", "utf-8")
    }

    private inner class WebAppInterface {
        @JavascriptInterface
        fun onVideoEnded() {
            if (!isVideoEndedHandled) {
                isVideoEndedHandled = true
                activity?.runOnUiThread {
                    findNavController().navigate(
                        // TODO: 전달받은 일기 날짜 넣기
                        ContentDetailFragmentDirections
                            .actionNavContentDetailToNavContentViewComplete("2024-08-10")
                    )
                }
            }
        }

        @JavascriptInterface
        fun onVideoTimeUpdate(currentTime: Float) {
            currentVideoPosition = currentTime.toInt()
        }

        @JavascriptInterface
        fun exitFullScreen() {
            activity?.runOnUiThread {
                (activity?.window?.decorView as? FrameLayout)?.removeView(customView)
                customView = null
                customViewCallback?.onCustomViewHidden()
            }
        }
    }

    private fun extractVideoIdFromUrl(url: String?): String? {
        if (url == null) return null
        val regex = "(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/.+/|(?:v|e(?:mbed)?)|.*[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(url)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }

    private fun showErrorMessage() {
        binding.wvContent.visibility = View.GONE
        binding.tvErrorMessage.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("videoPosition", currentVideoPosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            currentVideoPosition = it.getInt("videoPosition", 0)
            binding.wvContent.evaluateJavascript(
                "if(player && player.seekTo) player.seekTo($currentVideoPosition, true);",
                null
            )
        }
    }

    override fun onPause() {
        super.onPause()
        binding.wvContent.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.wvContent.onResume()
    }

    override fun configureToolbar(toolbar: CustomToolbar) {
        toolbar.apply {
            reset()
            setSubTitle("콘텐츠 시청")
            showBackButton {
                findNavController().popBackStack()
            }
        }
    }
}
