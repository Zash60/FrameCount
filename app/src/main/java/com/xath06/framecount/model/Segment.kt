package com.xath06.framecount.model

data class Segment(
    val id: Long = System.currentTimeMillis(),
    val label: String = "",
    val startFrame: Int = 0,
    val endFrame: Int = 0,
    val loadFrames: Int = 0
) {
    val frameCount: Int
        get() = endFrame - startFrame - loadFrames
}