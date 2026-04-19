package com.xath06.framecount.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.xath06.framecount.R
import com.xath06.framecount.databinding.FragmentVideoInputBinding
import com.xath06.framecount.viewmodel.FrameCountViewModel

class VideoInputFragment : Fragment() {

    private var _binding: FragmentVideoInputBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FrameCountViewModel by activityViewModels()

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.videoUri.value = it.toString()
            loadVideo(it.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupPlayerControls()
    }

    private fun setupButtons() {
        binding.btnLocalVideo.setOnClickListener {
            filePickerLauncher.launch("video/*")
        }

        binding.btnRemoteVideo.setOnClickListener {
            val url = binding.etRemoteUrl.text.toString()
            if (url.isNotBlank()) {
                loadVideo(url)
            }
        }

        binding.btnTwitchLogin.setOnClickListener {
            loadTwitchLogin()
        }
    }

    private fun setupPlayerControls() {
        binding.playerView.setOnClickListener {
            if (viewModel.isPlaying.value == true) {
                viewModel.isPlaying.value = false
            } else {
                viewModel.isPlaying.value = true
            }
        }

        binding.seekbar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = viewModel.videoDurationMs.value ?: 0L
                    val positionMs = (progress.toLong() * duration / 100)
                    viewModel.updateCurrentPosition(positionMs)
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        setupFrameButtons()
        setupTimeButtons()
        setupJumpButtons()
    }

    private fun setupFrameButtons() {
        binding.btnFrameMinus10.setOnClickListener { stepFrames(-10) }
        binding.btnFrameMinus5.setOnClickListener { stepFrames(-5) }
        binding.btnFrameMinus1.setOnClickListener { stepFrames(-1) }
        binding.btnFramePlus1.setOnClickListener { stepFrames(1) }
        binding.btnFramePlus5.setOnClickListener { stepFrames(5) }
        binding.btnFramePlus10.setOnClickListener { stepFrames(10) }
    }

    private fun setupTimeButtons() {
        binding.btnTimeMinus10.setOnClickListener { stepTime(-10.0) }
        binding.btnTimeMinus5.setOnClickListener { stepTime(-5.0) }
        binding.btnTimeMinus1.setOnClickListener { stepTime(-1.0) }
        binding.btnTimePlus1.setOnClickListener { stepTime(1.0) }
        binding.btnTimePlus5.setOnClickListener { stepTime(5.0) }
        binding.btnTimePlus10.setOnClickListener { stepTime(10.0) }
    }

    private fun setupJumpButtons() {
        binding.btnMinMinus10.setOnClickListener { stepTime(-600.0) }
        binding.btnMinMinus5.setOnClickListener { stepTime(-300.0) }
        binding.btnMinMinus1.setOnClickListener { stepTime(-60.0) }
        binding.btnMinPlus1.setOnClickListener { stepTime(60.0) }
        binding.btnMinPlus5.setOnClickListener { stepTime(300.0) }
        binding.btnMinPlus10.setOnClickListener { stepTime(600.0) }
    }

    private fun stepFrames(frames: Int) {
        val current = viewModel.currentFrame.value ?: 0
        viewModel.currentFrame.value = (current + frames).coerceAtLeast(0)
        viewModel.seekToFrame(viewModel.currentFrame.value!!)
    }

    private fun stepTime(seconds: Double) {
        val fps = viewModel.videoFps.value ?: 60.0
        val currentMs = viewModel.currentPositionMs.value ?: 0L
        val deltaMs = (seconds * 1000).toLong()
        val newMs = (currentMs + deltaMs).coerceAtLeast(0)
        viewModel.updateCurrentPosition(newMs)
    }

    private fun loadVideo(url: String) {
        viewModel.videoUri.value = url
        viewModel.currentPositionMs.value = 0L
    }

    private fun loadTwitchLogin() {
        binding.webView.visibility = View.VISIBLE
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        binding.webView.loadUrl("https://www.twitch.tv/login")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}