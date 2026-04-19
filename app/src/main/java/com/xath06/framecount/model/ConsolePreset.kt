package com.xath06.framecount.model

data class ConsolePreset(
    val name: String,
    val fps: Double
) {
    companion object {
        val presets = listOf(
            ConsolePreset("NES/SNES", 60.0988139),
            ConsolePreset("Genesis", 59.9220685),
            ConsolePreset("GBA", 59.7275005),
            ConsolePreset("N64", 60.0),
            ConsolePreset("GameCube", 59.9400599),
            ConsolePreset("Wii", 59.9400599),
            ConsolePreset("PS1", 59.9400599),
            ConsolePreset("PS2", 59.9400599),
            ConsolePreset("Xbox", 60.0),
            ConsolePreset("Custom", 60.0)
        )

        val presetMap = mapOf(
            "NES/SNES" to 60.0988139,
            "Genesis" to 59.9220685,
            "GBA" to 59.7275005,
            "N64" to 60.0,
            "GameCube" to 59.9400599,
            "Wii" to 59.9400599,
            "PS1" to 59.9400599,
            "PS2" to 59.9400599,
            "Xbox" to 60.0,
            "Custom" to 60.0
        )
    }
}