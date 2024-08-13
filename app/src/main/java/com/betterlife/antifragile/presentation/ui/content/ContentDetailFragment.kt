package com.betterlife.antifragile.presentation.ui.content

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.betterlife.antifragile.R
import com.betterlife.antifragile.databinding.FragmentContentDetailBinding
import com.betterlife.antifragile.presentation.base.BaseFragment
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentDetailViewModel
import com.betterlife.antifragile.presentation.ui.content.viewmodel.ContentDetailViewModelFactory
import com.betterlife.antifragile.presentation.util.CustomToolbar
import com.betterlife.antifragile.presentation.util.DateUtil
import com.betterlife.antifragile.presentation.util.NumberUtil
import com.betterlife.antifragile.presentation.util.TokenManager.getAccessToken
import java.util.regex.Pattern

class ContentDetailFragment : BaseFragment<FragmentContentDetailBinding>(
    R.layout.fragment_content_detail
) {

    private lateinit var contentDetailViewModel: ContentDetailViewModel
    private lateinit var diaryDate: String
    private lateinit var contentId: String

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUiVisibility: Int = 0
    private var originalOrientation: Int = 0
    private var isVideoEndedHandled = false
    private var currentVideoPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setVariables()
        setupViewModel()
        setupWebView()
        setupObservers()
        loadContentData()
        setupButtons()
    }

    private fun setVariables() {
        diaryDate = ContentDetailFragmentArgs.fromBundle(requireArguments()).diaryDate
        contentId = ContentDetailFragmentArgs.fromBundle(requireArguments()).contentId
    }

    private fun setupViewModel() {
        val factory = ContentDetailViewModelFactory(getAccessToken(requireContext())!!)
        contentDetailViewModel = factory.create(ContentDetailViewModel::class.java)
    }

    private fun setupObservers() {
        setupBaseObserver(
            liveData = contentDetailViewModel.contentDetailResponse,
            onSuccess = {
                binding.apply {
                    val videoId = extractVideoIdFromUrl(it.url)
                    if (videoId != null) {
                        loadYouTubeVideo(videoId)
                        dateInfo = DateUtil.convertDateToFullFormat(diaryDate)
                        contentData = it
                        tvSubscribeCount.text = NumberUtil.formatSubscriberCountKoreanStyle(
                            it.channel.subscribeNumber
                        )
                        btnLikeContent
                    } else {
                        showErrorMessage()
                    }
                }
            },
            onError = {
                Log.e("ContentDetailFragment", "Error: ${it.errorMessage}")
            }
        )

        setupBaseObserver(
            liveData = contentDetailViewModel.contentLikeResponse,
            onSuccess = { },
            onError = {
                Log.e("ContentDetailFragment", "Error: ${it.errorMessage}")
            }
        )

        contentDetailViewModel.isLiked.observe(viewLifecycleOwner) { isLiked ->
            binding.btnLikeContent.backgroundTintList = if (isLiked) {
                resources.getColorStateList(R.color.like_button_color, null)
            } else {
                resources.getColorStateList(R.color.unlike_button_color, null)
            }
        }

        contentDetailViewModel.likeNumber.observe(viewLifecycleOwner) { likeNumber ->
            binding.tvLikeCount.text = likeNumber.toString()
        }
    }

    private fun loadContentData() {
        contentDetailViewModel.getContentDetail(contentId)
    }

    private fun setupButtons() {
        binding.btnLikeContent.setOnClickListener {
            contentDetailViewModel.changeLikeState(contentId)
        }

        binding.btnSaveContent.visibility = View.GONE
        binding.btnSaveContent.setOnClickListener {
            // TODO: Implement save button
        }

        binding.btnViewComplete.setOnClickListener {
            findNavController().navigate(
                ContentDetailFragmentDirections
                    .actionNavContentDetailToNavContentViewComplete(diaryDate)
            )
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
                    diaryDate?.let {
                        findNavController().navigate(
                            ContentDetailFragmentDirections
                                .actionNavContentDetailToNavContentViewComplete(it)
                        )
                    } ?: run {
                        Log.e("ContentDetailFragment", "Diary date is null")
                    }
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
