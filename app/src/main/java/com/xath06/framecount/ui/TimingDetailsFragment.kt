package com.xath06.framecount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.xath06.framecount.R
import com.xath06.framecount.databinding.FragmentTimingDetailsBinding
import com.xath06.framecount.model.ConsolePreset
import com.xath06.framecount.viewmodel.FrameCountViewModel

class TimingDetailsFragment : Fragment() {

    private var _binding: FragmentTimingDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FrameCountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFpsInput()
        setupFrameInputs()
        setupConsolePreset()
        setupLoadSegments()
        setupPlaybackInfo()
        observeViewModel()
    }

    private fun setupFpsInput() {
        binding.etVideoFps.setText("60")
        binding.btnDetectFps.setOnClickListener {
            detectFps()
        }
    }

    private fun setupFrameInputs() {
        binding.btnSetStart.setOnClickListener {
            viewModel.startFrame.value = viewModel.currentFrame.value ?: 0
            viewModel.calculate()
        }

        binding.btnSetEnd.setOnClickListener {
            viewModel.endFrame.value = viewModel.currentFrame.value ?: 0
            viewModel.calculate()
        }

        binding.btnGoStart.setOnClickListener {
            viewModel.seekToFrame(viewModel.startFrame.value ?: 0)
        }

        binding.btnGoEnd.setOnClickListener {
            viewModel.seekToFrame(viewModel.endFrame.value ?: 0)
        }

        binding.etStartFrame.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val value = binding.etStartFrame.text.toString().toIntOrNull()
                if (value != null) {
                    viewModel.startFrame.value = value
                    viewModel.calculate()
                }
            }
        }

        binding.etEndFrame.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val value = binding.etEndFrame.text.toString().toIntOrNull()
                if (value != null) {
                    viewModel.endFrame.value = value
                    viewModel.calculate()
                }
            }
        }
    }

    private fun setupConsolePreset() {
        val presetNames = ConsolePreset.presets.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, presetNames)
        binding.spinnerConsolePreset.setAdapter(adapter)

        binding.spinnerConsolePreset.setOnItemClickListener { _, _, position, _ ->
            val preset = ConsolePreset.presets[position]
            viewModel.setConsolePreset(preset)
            binding.etConsoleFps.setText(String.format("%.6f", preset.fps))
        }

        binding.etConsoleFps.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val value = binding.etConsoleFps.text.toString().toDoubleOrNull()
                if (value != null) {
                    viewModel.setConsolePreset(ConsolePreset("Custom", value))
                    viewModel.setCustomConsoleFps(value)
                }
            }
        }
    }

    private fun setupLoadSegments() {
        binding.btnAddLoadSegment.setOnClickListener {
            val start = binding.etLoadStart.text.toString().toIntOrNull() ?: 0
            val end = binding.etLoadEnd.text.toString().toIntOrNull() ?: 0
            if (start < end) {
                viewModel.addLoadSegment(start, end)
            }
        }

        binding.btnClearLoads.setOnClickListener {
            viewModel.clearLoadSegments()
        }
    }

    private fun setupPlaybackInfo() {
        updatePlaybackInfo()
    }

    private fun detectFps() {
        viewModel.videoFps.value = 60.0
        binding.etVideoFps.setText("60")
    }

    private fun observeViewModel() {
        viewModel.currentFrame.observe(viewLifecycleOwner) { frame ->
            binding.tvCurrentFrame.text = frame.toString()
        }

        viewModel.startFrame.observe(viewLifecycleOwner) { frame ->
            binding.etStartFrame.setText(frame.toString())
        }

        viewModel.endFrame.observe(viewLifecycleOwner) { frame ->
            binding.etEndFrame.setText(frame.toString())
        }

        viewModel.loadFrames.observe(viewLifecycleOwner) { frames ->
            binding.tvLoadFrames.text = frames.toString()
        }

        viewModel.videoFps.observe(viewLifecycleOwner) { fps ->
            binding.etVideoFps.setText(String.format("%.2f", fps))
        }

        viewModel.consoleFps.observe(viewLifecycleOwner) { fps ->
            binding.tvConsoleFps.text = String.format("%.6f", fps)
        }
    }

    private fun updatePlaybackInfo() {
        val info = viewModel.getPlaybackInfo()
        binding.tvVideoTime.text = String.format("%.6f", info.videoTimeSeconds)
        binding.tvVideoTimeFormatted.text = info.videoTimeFormatted
        binding.tvFrameTime.text = String.format("%.9f", info.frameTimeSeconds)
        binding.tvFrameTimeFormatted.text = info.frameTimeFormatted
    }

    fun updateFpsFromVideo(fps: Double) {
        viewModel.videoFps.value = fps
        binding.etVideoFps.setText(String.format("%.2f", fps))
        viewModel.calculate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}