package com.xath06.framecount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.xath06.framecount.R
import com.xath06.framecount.databinding.FragmentResultsBinding
import com.xath06.framecount.model.ConsolePreset
import com.xath06.framecount.util.TimeFormatter
import com.xath06.framecount.viewmodel.FrameCountViewModel

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FrameCountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupConsolePreset()
        observeResults()
    }

    private fun setupConsolePreset() {
        val presetNames = ConsolePreset.presets.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            presetNames
        )
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
                    viewModel.setCustomConsoleFps(value)
                }
            }
        }
    }

    private fun observeResults() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.tvFrameCount.text = result.videoFrames.toString()
            binding.tvVideoTime.text = TimeFormatter.formatTime(result.videoTime)
            binding.tvConsoleTime.text = TimeFormatter.formatTime(result.consoleTime)
            binding.tvConsoleFrames.text = String.format("%.6f", result.consoleFrames)
        }

        viewModel.startFrame.observe(viewLifecycleOwner) { start ->
            binding.tvStartFrame.text = start.toString()
        }

        viewModel.endFrame.observe(viewLifecycleOwner) { end ->
            binding.tvEndFrame.text = end.toString()
        }

        viewModel.videoFps.observe(viewLifecycleOwner) { fps ->
            binding.tvVideoFps.text = String.format("%.2f", fps)
        }

        viewModel.consoleFps.observe(viewLifecycleOwner) { fps ->
            binding.tvConsoleFps.text = String.format("%.6f", fps)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}