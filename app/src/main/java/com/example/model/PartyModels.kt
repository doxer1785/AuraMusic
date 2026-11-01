package com.example.model

data class PartyGuest(
    val id: String,
    val name: String,
    val deviceModel: String,
    val isHost: Boolean = false,
    val avatarColor: Long = 0xFF7C4DFF,
    val joinedAt: Long = System.currentTimeMillis()
)

data class PartyReaction(
    val id: String,
    val guestName: String,
    val emoji: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PartyRoom(
    val roomCode: String,
    val roomName: String,
    val hostName: String,
    val currentTrack: Track?,
    val currentPositionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val guests: List<PartyGuest> = emptyList(),
    val queue: List<Track> = emptyList(),
    val recentReactions: List<PartyReaction> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalDevicesCount: Int
        get() = guests.size + 1 // guests + host
}
