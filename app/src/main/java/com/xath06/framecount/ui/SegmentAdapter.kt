package com.xath06.framecount.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xath06.framecount.databinding.ItemSegmentBinding
import com.xath06.framecount.model.Segment
import com.xath06.framecount.util.TimeFormatter
import com.xath06.framecount.viewmodel.FrameCountViewModel

class SegmentAdapter(
    private val viewModel: FrameCountViewModel
) : ListAdapter<Segment, SegmentAdapter.SegmentViewHolder>(SegmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentViewHolder {
        val binding = ItemSegmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SegmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SegmentViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class SegmentViewHolder(
        private val binding: ItemSegmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(segment: Segment, position: Int) {
            val fps = viewModel.videoFps.value ?: 60.0
            val consoleFps = viewModel.consoleFps.value ?: 60.0988139
            val includeStart = viewModel.includeStartFrame.value ?: false

            val startFrame = if (includeStart) segment.startFrame else segment.startFrame
            val videoTime = segment.frameCount / fps
            val consoleTime = videoTime / (1.0 / consoleFps)

            binding.tvSegmentLabel.text = segment.label.ifEmpty { "Segment ${position + 1}" }
            binding.tvSegmentFrames.text = "$startFrame - ${segment.endFrame} (${segment.frameCount})"
            binding.tvSegmentVideoTime.text = TimeFormatter.formatTime(videoTime)
            binding.tvSegmentConsoleTime.text = TimeFormatter.formatTime(consoleTime)

            val showDetails = viewModel.expandDetails.value ?: false
            binding.layoutDetails.visibility = if (showDetails) View.VISIBLE else View.GONE
        }
    }

    class SegmentDiffCallback : DiffUtil.ItemCallback<Segment>() {
        override fun areItemsTheSame(oldItem: Segment, newItem: Segment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Segment, newItem: Segment): Boolean {
            return oldItem == newItem
        }
    }
}