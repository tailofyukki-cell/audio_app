# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Visualizer classes
-keep class android.media.audiofx.Visualizer { *; }
-keep class android.media.audiofx.Visualizer$** { *; }

# Keep MediaPlayer classes
-keep class android.media.MediaPlayer { *; }
-keep class android.media.MediaPlayer$** { *; }
