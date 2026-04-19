package com.xath06.framecount.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xath06.framecount.model.ConsolePreset
import com.xath06.framecount.model.FrameCountResult
import com.xath06.framecount.model.Segment
import com.xath06.framecount.util.FrameCalculator
import com.xath06.framecount.util.TimeFormatter
import java.util.Collections

class FrameCountViewModel : ViewModel() {

    val videoUri = MutableLiveData<String?>()
    val isPlaying = MutableLiveData(false)
    val currentPositionMs = MutableLiveData(0L)
    val videoDurationMs = MutableLiveData(0L)

    val videoFps = MutableLiveData(60.0)
    val currentFrame = MutableLiveData(0)
    val startFrame = MutableLiveData(0)
    val endFrame = MutableLiveData(0)

    val consolePreset = MutableLiveData(ConsolePreset.presets[0])
    val consoleFps = MutableLiveData(60.0988139)
    val customConsoleFps = MutableLiveData(60.0)

    val loadFrames = MutableLiveData(0)
    val loadSegments = MutableLiveData<List<LoadSegment>>(emptyList())

    val segments = MutableLiveData<List<Segment>>(emptyList())

    val expandDetails = MutableLiveData(false)
    val includeStartFrame = MutableLiveData(false)
    val reverseSegments = MutableLiveData(false)

    val modNoteTemplate = MutableLiveData("Mod Note: Start Time \${startTime}, End Time \${endTime}, Frame Rate: \${videoFps}, Time: \${videoTime}")
    val modNote = MutableLiveData("")

    val computationBreakdown = MutableLiveData("")

    private val _result = MutableLiveData<FrameCountResult>()
    val result: LiveData<FrameCountResult> = _result

    var settingsApiKey: String = ""
    var settingsDefaultFps: Double = 60.0
    var settingsDefaultConsole: String = "NES/SNES"

    fun updateCurrentPosition(positionMs: Long) {
        currentPositionMs.value = positionMs
        val fps = videoFps.value ?: 60.0
        currentFrame.value = (positionMs / 1000.0 * fps).toInt()
    }

    fun calculate() {
        val start = startFrame.value ?: 0
        val end = endFrame.value ?: 0
        val vFps = videoFps.value ?: 60.0
        val cFps = if (consolePreset.value?.name == "Custom") {
            customConsoleFps.value ?: 60.0
        } else {
            consoleFps.value ?: 60.0988139
        }
        val loads = loadFrames.value ?: 0

        val result = FrameCalculator.calculateResults(start, end, vFps, cFps, loads)
        _result.value = result

        updateModNote(start, end, vFps, result.videoTime)
        updateComputationBreakdown(start, end, vFps, cFps, loads, result)
    }

    private fun updateModNote(start: Int, end: Int, fps: Double, videoTime: Double) {
        val template = modNoteTemplate.value ?: ""

        val note = template
            .replace("\${startFrame}", start.toString())
            .replace("\${endFrame}", end.toString())
            .replace("\${startTime}", TimeFormatter.formatTime(start / fps))
            .replace("\${endTime}", TimeFormatter.formatTime(end / fps))
            .replace("\${videoFps}", String.format("%.6f", fps))
            .replace("\${videoTime}", TimeFormatter.formatTime(videoTime))
            .replace("\${consoleFps}", String.format("%.6f", consoleFps.value ?: 60.0))
            .replace("\${consoleTime}", TimeFormatter.formatTime(_result.value?.consoleTime ?: 0.0))

        modNote.value = note
    }

    private fun updateComputationBreakdown(start: Int, end: Int, videoFps: Double, consoleFps: Double, loadFrames: Int, result: FrameCountResult) {
        val frameDuration = 1.0 / videoFps
        val consoleFrameDuration = 1.0 / consoleFps
        val videoFrames = end - start - loadFrames

        val breakdown = buildString {
            appendLine("=== Computation Breakdown ===")
            appendLine("Video FPS: ${String.format("%.6f", videoFps)}")
            appendLine("Frame Duration: ${String.format("%.9f", frameDuration)} seconds")
            appendLine()
            appendLine("Console FPS: ${String.format("%.6f", consoleFps)}")
            appendLine("Console Frame Duration: ${String.format("%.9f", consoleFrameDuration)} seconds")
            appendLine()
            appendLine("Video Frames: $videoFrames")
            appendLine("Video Time: ${String.format("%.6f", result.videoTime)} seconds")
            appendLine()
            appendLine("Console Frames: ${String.format("%.6f", result.consoleFrames)}")
            appendLine("Console Time: ${String.format("%.6f", result.consoleTime)} seconds")
        }
        computationBreakdown.value = breakdown
    }

    fun setConsolePreset(preset: ConsolePreset) {
        consolePreset.value = preset
        consoleFps.value = preset.fps
        calculate()
    }

    fun setCustomConsoleFps(fps: Double) {
        customConsoleFps.value = fps
        if (consolePreset.value?.name == "Custom") {
            consoleFps.value = fps
            calculate()
        }
    }

    fun addSegment(label: String = "") {
        val current = segments.value?.toMutableList() ?: mutableListOf()
        val segment = Segment(
            label = label,
            startFrame = startFrame.value ?: 0,
            endFrame = endFrame.value ?: 0,
            loadFrames = loadFrames.value ?: 0
        )
        current.add(segment)
        segments.value = current
    }

    fun removeLastSegment() {
        val current = segments.value?.toMutableList() ?: return
        if (current.isNotEmpty()) {
            current.removeAt(current.size - 1)
            segments.value = current
        }
    }

    fun clearSegments() {
        segments.value = emptyList()
    }

    fun addLoadSegment(startFrame: Int, endFrame: Int) {
        val current = loadSegments.value?.toMutableList() ?: mutableListOf()
        current.add(LoadSegment(startFrame, endFrame))
        loadSegments.value = current

        val totalLoad = current.sumOf { it.endFrame - it.startFrame }
        loadFrames.value = totalLoad
        calculate()
    }

    fun clearLoadSegments() {
        loadSegments.value = emptyList()
        loadFrames.value = 0
        calculate()
    }

    fun seekToFrame(frame: Int) {
        val fps = videoFps.value ?: 60.0
        val positionMs = (frame / fps * 1000).toLong()
        currentPositionMs.value = positionMs
    }

    fun copyFrames(): String {
        return "${startFrame.value ?: 0}, ${endFrame.value ?: 0}"
    }

    fun copyTimes(): String {
        val fps = videoFps.value ?: 60.0
        val startTime = TimeFormatter.formatTime((startFrame.value ?: 0) / fps)
        val endTime = TimeFormatter.formatTime((endFrame.value ?: 0) / fps)
        return "$startTime, $endTime"
    }

    fun getPlaybackInfo(): PlaybackInfo {
        val fps = videoFps.value ?: 60.0
        val currentMs = currentPositionMs.value ?: 0L
        val videoTimeSeconds = currentMs / 1000.0
        val frameTime = 1.0 / fps

        return PlaybackInfo(
            videoTimeSeconds = videoTimeSeconds,
            videoTimeFormatted = TimeFormatter.formatTime(videoTimeSeconds),
            frameTimeSeconds = frameTime,
            frameTimeFormatted = TimeFormatter.formatTime(frameTime)
        )
    }

    data class LoadSegment(val startFrame: Int, val endFrame: Int)

    data class PlaybackInfo(
        val videoTimeSeconds: Double,
        val videoTimeFormatted: String,
        val frameTimeSeconds: Double,
        val frameTimeFormatted: String
    )
}