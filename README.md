# FrameCount

A speedrun timing Android app that replicates the functionality of somewes.com/frame-count/

## Features

- **Video Input**: Load local videos (mp4, mkv, webm) or remote URLs (YouTube, Twitch, Vimeo)
- **Frame-Precise Controls**: Step frames (-10f to +10f), time jumps (-10s to +10s), minute jumps (-10m to +10m)
- **Timing Details**: Set start/end frames, detect FPS, console presets
- **Results**: Calculate frame count, video time, and console time with proper timing calculations
- **Segments**: Add, remove, copy segments with keyboard shortcuts
- **Load Segments**: Define load periods to subtract from total time
- **Mod Note**: Auto-generated notes for speedrun submissions
- **Settings**: Configure default console, FPS, and API keys

## Console Presets

| Console | FPS |
|---------|-----|
| NES/SNES | 60.0988139 |
| Genesis | 59.9220685 |
| GBA | 59.7275005 |
| N64 | 60.0 |
| GameCube | 59.9400599 |
| Wii | 59.9400599 |
| PS1 | 59.9400599 |
| PS2 | 59.9400599 |
| Xbox | 60.0 |

## Building

The project uses Gradle with Kotlin DSL. Build with:

```bash
./gradlew assembleDebug
```

Or use Android Studio to open the project.

## License

MIT