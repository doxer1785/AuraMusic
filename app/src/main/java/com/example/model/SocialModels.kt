package com.example.model

data class FriendActivity(
    val id: String,
    val name: String,
    val username: String,
    val avatarColor: Long,
    val listeningTrack: Track,
    val timeAgo: String,
    val isLive: Boolean = true,
    val playlistName: String = "Discover Weekly"
)

enum class DeviceType {
    PHONE,
    SPEAKER,
    COMPUTER,
    CAR,
    TV
}

data class SyncDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val isCurrent: Boolean = false,
    val batteryPercent: Int? = null,
    val volumePercent: Int = 75
)
