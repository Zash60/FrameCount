package com.xath06.framecount.util

import java.util.concurrent.TimeUnit

object TimeFormatter {
    fun formatTime(seconds: Double): String {
        val totalMillis = (seconds * 1000).toLong()
        val hours = TimeUnit.MILLISECONDS.toHours(totalMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60
        val secs = TimeUnit.MILLISECONDS.toSeconds(totalMillis) % 60
        val millis = totalMillis % 1000

        return if (hours > 0) {
            String.format("%d:%02d:%02d.%03d", hours, minutes, secs, millis)
        } else {
            String.format("%02d:%02d.%03d", minutes, secs, millis)
        }
    }

    fun formatTimeDecimal(seconds: Double): String {
        return String.format("%.6f", seconds)
    }

    fun formatFrameTime(fps: Double): String {
        val frameDuration = 1.0 / fps
        return String.format("%.9f", frameDuration)
    }

    fun formatFrameTimeFormatted(fps: Double): String {
        val frameDuration = 1.0 / fps
        return formatTime(frameDuration)
    }

    fun parseTimeToSeconds(timeString: String): Double? {
        return try {
            val parts = timeString.split(":")
            when (parts.size) {
                1 -> parts[0].toDouble()
                2 -> {
                    val min = parts[0].toInt()
                    val sec = parts[1].toDouble()
                    min * 60.0 + sec
                }
                3 -> {
                    val hour = parts[0].toInt()
                    val min = parts[1].toInt()
                    val sec = parts[2].toDouble()
                    hour * 3600.0 + min * 60.0 + sec
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}