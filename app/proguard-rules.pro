# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep model classes
-keep class com.xath06.framecount.model.** { *; }

# Keep ExoPlayer classes
-keep class androidx.media3.** { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }