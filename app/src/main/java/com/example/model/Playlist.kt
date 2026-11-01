package com.example.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val curator: String,
    val gradient: List<Long>,
    val coverEmoji: String = "🎧",
    val tracks: List<Track> = emptyList(),
    val isDailyDiscovery: Boolean = false,
    val category: String = "For You",
    val followerCount: Int = 12400
) {
    val totalDurationString: String
        get() {
            val totalSeconds = tracks.sumOf { it.durationMs } / 1000
            val minutes = totalSeconds / 60
            return "$minutes min"
        }
}
