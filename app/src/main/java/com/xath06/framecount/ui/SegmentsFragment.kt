package com.xath06.framecount.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xath06.framecount.databinding.FragmentSegmentsBinding
import com.xath06.framecount.model.Segment
import com.xath06.framecount.util.TimeFormatter
import com.xath06.framecount.viewmodel.FrameCountViewModel

class SegmentsFragment : Fragment() {

    private var _binding: FragmentSegmentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FrameCountViewModel by activityViewModels()
    private lateinit var adapter: SegmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSegmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtons()
        setupCheckboxes()
        observeSegments()
    }

    private fun setupRecyclerView() {
        adapter = SegmentAdapter(viewModel)
        binding.recyclerSegments.layoutManager = LinearLayoutManager(context)
        binding.recyclerSegments.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnAddSegment.setOnClickListener {
            viewModel.addSegment()
            Toast.makeText(context, "Segment added", Toast.LENGTH_SHORT).show()
        }

        binding.btnRemoveLast.setOnClickListener {
            viewModel.removeLastSegment()
        }

        binding.btnClearAll.setOnClickListener {
            viewModel.clearSegments()
        }

        binding.btnCopyFrames.setOnClickListener {
            val frames = viewModel.copyFrames()
            copyToClipboard(frames)
            Toast.makeText(context, "Frames copied: $frames", Toast.LENGTH_SHORT).show()
        }

        binding.btnCopyTimes.setOnClickListener {
            val times = viewModel.copyTimes()
            copyToClipboard(times)
            Toast.makeText(context, "Times copied: $times", Toast.LENGTH_SHORT).show()
        }

        binding.btnLabelsFrames.setOnClickListener {
            val text = generateLabelsFrames()
            copyToClipboard(text)
        }

        binding.btnLabelsTimes.setOnClickListener {
            val text = generateLabelsTimes()
            copyToClipboard(text)
        }
    }

    private fun setupCheckboxes() {
        binding.cbExpandDetails.setOnCheckedChangeListener { _, isChecked ->
            viewModel.expandDetails.value = isChecked
            adapter.notifyDataSetChanged()
        }

        binding.cbIncludeStart.setOnCheckedChangeListener { _, isChecked ->
            viewModel.includeStartFrame.value = isChecked
            adapter.notifyDataSetChanged()
        }

        binding.cbReverse.setOnCheckedChangeListener { _, isChecked ->
            viewModel.reverseSegments.value = isChecked
            adapter.notifyDataSetChanged()
        }
    }

    private fun observeSegments() {
        viewModel.segments.observe(viewLifecycleOwner) { segments ->
            adapter.submitList(segments)
            updateTotals(segments)
        }
    }

    private fun updateTotals(segments: List<Segment>) {
        val totalFrames = segments.sumOf { it.frameCount }
        val fps = viewModel.videoFps.value ?: 60.0
        val consoleFps = viewModel.consoleFps.value ?: 60.0988139

        val videoTime = totalFrames / fps
        val consoleTime = (totalFrames / fps) / (1.0 / consoleFps)

        binding.tvTotalFrames.text = totalFrames.toString()
        binding.tvTotalVideoTime.text = TimeFormatter.formatTime(videoTime)
        binding.tvTotalConsoleTime.text = TimeFormatter.formatTime(consoleTime)
    }

    private fun generateLabelsFrames(): String {
        val segments = viewModel.segments.value ?: return ""
        val includeStart = viewModel.includeStartFrame.value ?: false

        return segments.mapIndexed { index, segment ->
            val label = segment.label.ifEmpty { "Segment ${index + 1}" }
            val frames = if (includeStart) {
                "${segment.startFrame}-${segment.endFrame}"
            } else {
                "${segment.startFrame + 1}-${segment.endFrame}"
            }
            "$label: $frames"
        }.joinToString("\n")
    }

    private fun generateLabelsTimes(): String {
        val segments = viewModel.segments.value ?: return ""
        val fps = viewModel.videoFps.value ?: 60.0

        return segments.mapIndexed { index, segment ->
            val label = segment.label.ifEmpty { "Segment ${index + 1}" }
            val startTime = TimeFormatter.formatTime(segment.startFrame / fps)
            val endTime = TimeFormatter.formatTime(segment.endFrame / fps)
            "$label: $startTime - $endTime"
        }.joinToString("\n")
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("FrameCount", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun pasteSegments() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: return
            parseAndAddSegments(text)
        }
    }

    private fun parseAndAddSegments(text: String) {
        // Simple parsing: "label: start-end" or just "start-end"
        val lines = text.split("\n")
        for (line in lines) {
            val parts = line.split(":")
            val label = if (parts.size > 1) parts[0].trim() else ""
            val range = parts.lastOrNull() ?: continue
            val frameParts = range.split("-")
            if (frameParts.size == 2) {
                val start = frameParts[0].trim().toIntOrNull() ?: continue
                val end = frameParts[1].trim().toIntOrNull() ?: continue
                viewModel.addSegment(label)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}