package com.xath06.framecount.util

import com.xath06.framecount.model.FrameCountResult

object FrameCalculator {
    fun calculateResults(
        startFrame: Int,
        endFrame: Int,
        videoFps: Double,
        consoleFps: Double,
        loadFrames: Int = 0
    ): FrameCountResult {
        return FrameCountResult.calculate(startFrame, endFrame, videoFps, consoleFps, loadFrames)
    }

    fun frameToTime(frame: Int, fps: Double): Double {
        return frame / fps
    }

    fun timeToFrame(timeSeconds: Double, fps: Double): Int {
        return (timeSeconds * fps).toInt()
    }

    fun formatVideoTime(seconds: Double): String {
        return TimeFormatter.formatTime(seconds)
    }

    fun formatConsoleTime(seconds: Double): String {
        return TimeFormatter.formatTime(seconds)
    }

    fun detectFpsFromDuration(frameDuration: Double): Double {
        return if (frameDuration > 0) 1.0 / frameDuration else 60.0
    }

    val commonFps = listOf(
        24.0, 25.0, 29.9700000762939, 30.0, 50.0,
        59.72265625, 59.9400599, 60.0, 60.0988139, 120.0
    )

    fun detectClosestFps(calculatedFps: Double): Double {
        return commonFps.minByOrNull { kotlin.math.abs(it - calculatedFps) } ?: calculatedFps
    }
}