package com.example.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val genre: String,
    val audioUrl: String,
    val coverGradient: List<Long>,
    val coverEmoji: String = "🎵",
    val bitrate: String = "320 kbps (Lossless)",
    val bpm: Int = 120,
    val lyrics: List<String> = emptyList(),
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null,
    val playCount: Int = 0,
    val youtubeVideoId: String? = null,
    val isYouTube: Boolean = false
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
