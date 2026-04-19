package com.xath06.framecount.model

data class FrameCountResult(
    val videoFrames: Int,
    val videoTime: Double,
    val consoleFrames: Double,
    val consoleTime: Double
) {
    companion object {
        fun calculate(
            startFrame: Int,
            endFrame: Int,
            videoFps: Double,
            consoleFps: Double,
            loadFrames: Int = 0
        ): FrameCountResult {
            val videoFrames = endFrame - startFrame - loadFrames
            val frameDuration = 1.0 / videoFps
            val videoTime = videoFrames * frameDuration
            val consoleFrameDuration = 1.0 / consoleFps
            val consoleFrames = videoTime / consoleFrameDuration
            val consoleTime = Math.floor(consoleFrames) * consoleFrameDuration
            return FrameCountResult(videoFrames, videoTime, consoleFrames, consoleTime)
        }
    }
}