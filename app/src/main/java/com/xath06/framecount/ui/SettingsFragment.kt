package com.xath06.framecount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.xath06.framecount.databinding.FragmentSettingsBinding
import com.xath06.framecount.model.ConsolePreset
import com.xath06.framecount.viewmodel.FrameCountViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FrameCountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupApiKeyInput()
        setupDefaultConsole()
        setupDefaultFps()
        setupModNoteTemplate()
        loadSettings()
    }

    private fun setupApiKeyInput() {
        binding.etSpeedrunApiKey.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.settingsApiKey = binding.etSpeedrunApiKey.text.toString()
            }
        }
    }

    private fun setupDefaultConsole() {
        val presetNames = ConsolePreset.presets.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            presetNames
        )
        binding.spinnerDefaultConsole.setAdapter(adapter)

        binding.spinnerDefaultConsole.setOnItemClickListener { _, _, position, _ ->
            val preset = ConsolePreset.presets[position]
            viewModel.settingsDefaultConsole = preset.name
        }
    }

    private fun setupDefaultFps() {
        binding.etDefaultVideoFps.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val fps = binding.etDefaultVideoFps.text.toString().toDoubleOrNull()
                if (fps != null) {
                    viewModel.settingsDefaultFps = fps
                }
            }
        }
    }

    private fun setupModNoteTemplate() {
        binding.btnSaveTemplate.setOnClickListener {
            viewModel.modNoteTemplate.value = binding.etModNoteTemplate.text.toString()
        }

        binding.btnRestoreTemplate.setOnClickListener {
            val defaultTemplate = "Mod Note: Start Time \${startTime}, End Time \${endTime}, Frame Rate: \${videoFps}, Time: \${videoTime}"
            binding.etModNoteTemplate.setText(defaultTemplate)
            viewModel.modNoteTemplate.value = defaultTemplate
        }
    }

    private fun loadSettings() {
        binding.etSpeedrunApiKey.setText(viewModel.settingsApiKey)
        binding.etDefaultVideoFps.setText(String.format("%.2f", viewModel.settingsDefaultFps))

        val presetIndex = ConsolePreset.presets.indexOfFirst { it.name == viewModel.settingsDefaultConsole }
        if (presetIndex >= 0) {
            binding.spinnerDefaultConsole.setText(ConsolePreset.presets[presetIndex].name, false)
        }

        binding.etModNoteTemplate.setText(viewModel.modNoteTemplate.value)
    }

    fun saveSettings() {
        viewModel.settingsApiKey = binding.etSpeedrunApiKey.text.toString()
        viewModel.settingsDefaultFps = binding.etDefaultVideoFps.text.toString().toDoubleOrNull() ?: 60.0
        viewModel.settingsDefaultConsole = binding.spinnerDefaultConsole.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}