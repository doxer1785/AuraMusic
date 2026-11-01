package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.audio.OfflineCacheManager
import com.example.data.MusicCatalog
import com.example.model.FriendActivity
import com.example.model.PartyGuest
import com.example.model.PartyReaction
import com.example.model.PartyRoom
import com.example.model.Playlist
import com.example.model.SyncDevice
import com.example.model.Track
import com.example.network.youtube.YouTubeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class AuraTab {
    HOME,
    SEARCH,
    PARTY,
    SOCIAL,
    LIBRARY
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val offlineCacheManager = OfflineCacheManager(application.applicationContext)
    private val audioEngine = AudioEngine(application.applicationContext, viewModelScope)
    private val youTubeRepository = YouTubeRepository()

    private val _youTubeSearchResults = MutableStateFlow<List<Track>>(emptyList())
    val youTubeSearchResults: StateFlow<List<Track>> = _youTubeSearchResults.asStateFlow()

    private val _isSearchingYouTube = MutableStateFlow(false)
    val isSearchingYouTube: StateFlow<Boolean> = _isSearchingYouTube.asStateFlow()

    private var youTubeSearchJob: Job? = null

    // Navigation and sheets
    private val _activeTab = MutableStateFlow(AuraTab.HOME)
    val activeTab: StateFlow<AuraTab> = _activeTab.asStateFlow()

    private val _isPlayerExpanded = MutableStateFlow(false)
    val isPlayerExpanded: StateFlow<Boolean> = _isPlayerExpanded.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    private val _isDevicePickerOpen = MutableStateFlow(false)
    val isDevicePickerOpen: StateFlow<Boolean> = _isDevicePickerOpen.asStateFlow()

    private val _isShareDialogOpen = MutableStateFlow(false)
    val isShareDialogOpen: StateFlow<Boolean> = _isShareDialogOpen.asStateFlow()

    private val _isLyricsOpen = MutableStateFlow(false)
    val isLyricsOpen: StateFlow<Boolean> = _isLyricsOpen.asStateFlow()

    // Search and Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenreFilter = MutableStateFlow("All")
    val selectedGenreFilter: StateFlow<String> = _selectedGenreFilter.asStateFlow()

    // Playback state
    private val _allTracks = MutableStateFlow(MusicCatalog.allCatalogTracks)
    val allTracks: StateFlow<List<Track>> = _allTracks.asStateFlow()

    private val _isVideoMode = MutableStateFlow(false)
    val isVideoMode: StateFlow<Boolean> = _isVideoMode.asStateFlow()

    private val _allPlaylists = MutableStateFlow(MusicCatalog.samplePlaylists)
    val allPlaylists: StateFlow<List<Playlist>> = _allPlaylists.asStateFlow()

    private val _currentTrack = MutableStateFlow(MusicCatalog.sampleTracks[0])
    val currentTrack: StateFlow<Track> = _currentTrack.asStateFlow()

    val playbackProgress = audioEngine.playbackProgress

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean> = _isShuffled.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _audioQuality = MutableStateFlow("Lossless Hi-Fi (320kbps)")
    val audioQuality: StateFlow<String> = _audioQuality.asStateFlow()

    // Offline state
    private val _isOfflineOnlyMode = MutableStateFlow(false)
    val isOfflineOnlyMode: StateFlow<Boolean> = _isOfflineOnlyMode.asStateFlow()

    private val _downloadedTrackIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadedTrackIds: StateFlow<Set<String>> = _downloadedTrackIds.asStateFlow()

    private val _downloadingProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadingProgress: StateFlow<Map<String, Float>> = _downloadingProgress.asStateFlow()

    private val _offlineCacheSizeMb = MutableStateFlow(0f)
    val offlineCacheSizeMb: StateFlow<Float> = _offlineCacheSizeMb.asStateFlow()

    // Party Mode state
    private val _partyRoom = MutableStateFlow<PartyRoom?>(null)
    val partyRoom: StateFlow<PartyRoom?> = _partyRoom.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    val isHost: StateFlow<Boolean> = _isHost.asStateFlow()

    private var partySimulationJob: Job? = null

    // Social and Devices
    private val _friendsActivity = MutableStateFlow(MusicCatalog.sampleFriends)
    val friendsActivity: StateFlow<List<FriendActivity>> = _friendsActivity.asStateFlow()

    private val _devices = MutableStateFlow(MusicCatalog.sampleDevices)
    val devices: StateFlow<List<SyncDevice>> = _devices.asStateFlow()

    init {
        audioEngine.setOnTrackCompletedListener {
            handleTrackCompleted()
        }
        refreshDownloadedStatus()
    }

    private fun refreshDownloadedStatus() {
        val downloaded = _allTracks.value
            .filter { offlineCacheManager.isTrackDownloaded(it.id) }
            .map { it.id }
            .toSet()
        _downloadedTrackIds.value = downloaded
        _offlineCacheSizeMb.value = offlineCacheManager.getTotalCacheSizeMb()

        // Update tracks with downloaded state
        _allTracks.value = _allTracks.value.map { track ->
            val isDl = downloaded.contains(track.id)
            track.copy(
                isDownloaded = isDl,
                localFilePath = if (isDl) offlineCacheManager.getLocalFilePath(track.id) else null
            )
        }
    }

    // Playback Controls
    fun playTrack(track: Track, startPositionMs: Long = 0L) {
        _currentTrack.value = track
        _isVideoMode.value = track.isYouTube

        val localPath = if (track.isDownloaded) {
            offlineCacheManager.getLocalFilePath(track.id)
        } else null

        audioEngine.playTrack(track, localPath, startPositionMs)

        // If in Party Mode and user is host, sync to room
        if (_partyRoom.value != null && _isHost.value) {
            _partyRoom.value = _partyRoom.value?.copy(
                currentTrack = track,
                currentPositionMs = startPositionMs,
                isPlaying = true
            )
        }
    }

    fun toggleVideoMode() {
        _isVideoMode.value = !_isVideoMode.value
    }

    fun importYouTubeTrack(title: String, artist: String, urlOrId: String): Boolean {
        val videoId = MusicCatalog.extractYouTubeVideoId(urlOrId) ?: return false
        val cleanTitle = title.ifBlank { "YouTube Track ($videoId)" }
        val cleanArtist = artist.ifBlank { "YouTube Music" }
        val newTrack = Track(
            id = "yt_custom_${System.currentTimeMillis()}",
            title = cleanTitle,
            artist = cleanArtist,
            album = "Imported from YouTube",
            durationMs = 210000,
            genre = "YouTube Music",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            coverGradient = listOf(0xFFFF0000, 0xFF790000, 0xFF121212),
            coverEmoji = "▶️",
            bitrate = "YouTube 1080p HD",
            bpm = 120,
            lyrics = listOf("Imported directly from YouTube", "Playing via official YouTube embed"),
            isLiked = true,
            youtubeVideoId = videoId,
            isYouTube = true,
            playCount = 1
        )
        _allTracks.value = listOf(newTrack) + _allTracks.value
        playTrack(newTrack)
        _isVideoMode.value = true
        _isPlayerExpanded.value = true
        return true
    }

    fun togglePlayPause() {
        audioEngine.togglePlayPause()
        if (_partyRoom.value != null && _isHost.value) {
            _partyRoom.value = _partyRoom.value?.copy(
                isPlaying = playbackProgress.value.isPlaying
            )
        }
    }

    fun seekTo(positionMs: Long) {
        audioEngine.seekTo(positionMs)
        if (_partyRoom.value != null && _isHost.value) {
            _partyRoom.value = _partyRoom.value?.copy(
                currentPositionMs = positionMs
            )
        }
    }

    fun skipNext() {
        val currentList = getActivePlaybackList()
        if (currentList.isEmpty()) return

        val currentIndex = currentList.indexOfFirst { it.id == _currentTrack.value.id }
        val nextIndex = if (_isShuffled.value) {
            Random.nextInt(currentList.size)
        } else {
            (currentIndex + 1) % currentList.size
        }
        playTrack(currentList[nextIndex])
    }

    fun skipPrevious() {
        val currentList = getActivePlaybackList()
        if (currentList.isEmpty()) return

        val currentIndex = currentList.indexOfFirst { it.id == _currentTrack.value.id }
        val prevIndex = if (currentIndex <= 0) currentList.size - 1 else currentIndex - 1
        playTrack(currentList[prevIndex])
    }

    private fun handleTrackCompleted() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> playTrack(_currentTrack.value)
            RepeatMode.ALL -> skipNext()
            RepeatMode.OFF -> {
                val currentList = getActivePlaybackList()
                val currentIndex = currentList.indexOfFirst { it.id == _currentTrack.value.id }
                if (currentIndex < currentList.size - 1) {
                    skipNext()
                }
            }
        }
    }

    private fun getActivePlaybackList(): List<Track> {
        val tracks = if (_isOfflineOnlyMode.value) {
            _allTracks.value.filter { _downloadedTrackIds.value.contains(it.id) }
        } else {
            _selectedPlaylist.value?.tracks ?: _allTracks.value
        }
        return if (tracks.isEmpty()) _allTracks.value else tracks
    }

    fun toggleLike(trackId: String) {
        _allTracks.value = _allTracks.value.map {
            if (it.id == trackId) it.copy(isLiked = !it.isLiked) else it
        }
        if (_currentTrack.value.id == trackId) {
            _currentTrack.value = _currentTrack.value.copy(isLiked = !_currentTrack.value.isLiked)
        }
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _isShuffled.value = !_isShuffled.value
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        audioEngine.setSpeed(speed)
    }

    fun setAudioQuality(quality: String) {
        _audioQuality.value = quality
    }

    // Offline Mode & Downloads
    fun toggleDownloadTrack(track: Track) {
        val isAlreadyDownloaded = _downloadedTrackIds.value.contains(track.id)
        if (isAlreadyDownloaded) {
            offlineCacheManager.removeDownloadedTrack(track.id)
            refreshDownloadedStatus()
        } else {
            viewModelScope.launch {
                _downloadingProgress.value = _downloadingProgress.value + (track.id to 0.05f)
                offlineCacheManager.downloadTrack(
                    track = track,
                    onProgress = { progress ->
                        _downloadingProgress.value = _downloadingProgress.value + (track.id to progress)
                    }
                ).onSuccess {
                    _downloadingProgress.value = _downloadingProgress.value - track.id
                    refreshDownloadedStatus()
                }.onFailure {
                    _downloadingProgress.value = _downloadingProgress.value - track.id
                }
            }
        }
    }

    fun toggleOfflineOnlyMode(enabled: Boolean) {
        _isOfflineOnlyMode.value = enabled
        if (enabled && !_downloadedTrackIds.value.contains(_currentTrack.value.id)) {
            val firstDownloaded = _allTracks.value.firstOrNull { _downloadedTrackIds.value.contains(it.id) }
            if (firstDownloaded != null) {
                playTrack(firstDownloaded)
            }
        }
    }

    fun clearOfflineCache() {
        offlineCacheManager.clearAllOfflineTracks()
        refreshDownloadedStatus()
    }

    // Party Mode Sync Engine
    fun createPartyRoom(roomName: String = "Neon Vibes Jam", hostName: String = "You (Host)") {
        val pin = "%06d".format(Random.nextInt(100000, 999999))
        val initialGuests = listOf(
            PartyGuest("g_1", "Sophie (Pixel 8)", "Pixel 8", isHost = false, avatarColor = 0xFF00E5FF),
            PartyGuest("g_2", "Liam (Galaxy S24)", "Galaxy S24", isHost = false, avatarColor = 0xFFFF4081),
            PartyGuest("g_3", "Elena (iPhone 15)", "iPhone 15 Pro", isHost = false, avatarColor = 0xFFFFAB00),
            PartyGuest("g_4", "Marcus (OnePlus 12)", "OnePlus 12", isHost = false, avatarColor = 0xFF7C4DFF),
            PartyGuest("g_5", "Chloe (Nothing Phone)", "Nothing Phone 2", isHost = false, avatarColor = 0xFF1DB954)
        )
        val room = PartyRoom(
            roomCode = pin,
            roomName = roomName,
            hostName = hostName,
            currentTrack = _currentTrack.value,
            currentPositionMs = playbackProgress.value.currentPositionMs,
            isPlaying = playbackProgress.value.isPlaying,
            guests = initialGuests,
            queue = listOf(MusicCatalog.sampleTracks[7], MusicCatalog.sampleTracks[3])
        )
        _partyRoom.value = room
        _isHost.value = true
        startPartySimulation()
    }

    fun joinPartyRoom(pin: String, guestName: String = "Guest") {
        val dummyRoom = PartyRoom(
            roomCode = pin.ifBlank { "834192" },
            roomName = "Aura Synced Jam Room",
            hostName = "Jordan DJ (Host)",
            currentTrack = _currentTrack.value,
            currentPositionMs = playbackProgress.value.currentPositionMs,
            isPlaying = true,
            guests = listOf(
                PartyGuest("g_me", guestName, "My Device", isHost = false, avatarColor = 0xFF1DB954),
                PartyGuest("g_1", "David (Pixel 7)", "Pixel 7", isHost = false, avatarColor = 0xFF00E5FF),
                PartyGuest("g_2", "Sarah (iPhone 14)", "iPhone 14", isHost = false, avatarColor = 0xFFFF4081),
                PartyGuest("g_3", "Noah (Galaxy Z Flip)", "Galaxy Z Flip", isHost = false, avatarColor = 0xFFFFAB00)
            )
        )
        _partyRoom.value = dummyRoom
        _isHost.value = false
        startPartySimulation()
    }

    fun leaveParty() {
        partySimulationJob?.cancel()
        partySimulationJob = null
        _partyRoom.value = null
        _isHost.value = false
    }

    fun sendPartyReaction(emoji: String, senderName: String = "You") {
        val room = _partyRoom.value ?: return
        val newReaction = PartyReaction(
            id = UUID.randomUUID().toString(),
            guestName = senderName,
            emoji = emoji
        )
        val updatedReactions = (listOf(newReaction) + room.recentReactions).take(8)
        _partyRoom.value = room.copy(recentReactions = updatedReactions)
    }

    fun addTrackToPartyQueue(track: Track) {
        val room = _partyRoom.value ?: return
        _partyRoom.value = room.copy(queue = room.queue + track)
        sendPartyReaction("🎵", "Queue Added")
    }

    fun addSimulatedPartyGuest() {
        val room = _partyRoom.value ?: return
        val names = listOf(
            "Sam (Xiaomi 14)", "Kai (Sony Xperia)", "Aria (Pixel 9 Pro)",
            "Lucas (iPhone 15)", "Nina (Motorola Razr)", "Zack (Galaxy S23 Ultra)",
            "Talia (Asus ROG)", "Leo (Honor Magic)", "Mira (Pixel Fold)", "Finn (OnePlus Open)"
        )
        val nextName = names.getOrNull(room.guests.size) ?: "Friend ${room.guests.size + 1}"
        val colors = listOf(0xFF00E5FF, 0xFFFF4081, 0xFFFFAB00, 0xFF7C4DFF, 0xFF1DB954, 0xFFE040FB)
        val newGuest = PartyGuest(
            id = UUID.randomUUID().toString(),
            name = nextName,
            deviceModel = nextName.substringAfter("(").substringBefore(")"),
            avatarColor = colors[Random.nextInt(colors.size)]
        )
        _partyRoom.value = room.copy(guests = room.guests + newGuest)
        sendPartyReaction("🎉", newGuest.name)
    }

    private fun startPartySimulation() {
        partySimulationJob?.cancel()
        partySimulationJob = viewModelScope.launch {
            val emojis = listOf("🔥", "💃", "🎧", "❤️", "⚡", "🙌")
            while (isActive) {
                delay(6000)
                val room = _partyRoom.value
                if (room != null && room.guests.isNotEmpty()) {
                    val randomGuest = room.guests[Random.nextInt(room.guests.size)]
                    val randomEmoji = emojis[Random.nextInt(emojis.size)]
                    sendPartyReaction(randomEmoji, randomGuest.name)
                }
            }
        }
    }

    // Social Listen Along
    fun listenAlongWithFriend(friend: FriendActivity) {
        playTrack(friend.listeningTrack)
        _isPlayerExpanded.value = true
    }

    // Device Switching & Handoff
    fun switchDevice(deviceId: String) {
        _devices.value = _devices.value.map {
            it.copy(isCurrent = it.id == deviceId)
        }
    }

    // UI Navigation Actions
    fun setActiveTab(tab: AuraTab) {
        _activeTab.value = tab
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _isPlayerExpanded.value = expanded
    }

    fun selectPlaylist(playlist: Playlist?) {
        _selectedPlaylist.value = playlist
    }

    fun setDevicePickerOpen(open: Boolean) {
        _isDevicePickerOpen.value = open
    }

    fun setShareDialogOpen(open: Boolean) {
        _isShareDialogOpen.value = open
    }

    fun setLyricsOpen(open: Boolean) {
        _isLyricsOpen.value = open
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        youTubeSearchJob?.cancel()

        if (query.isBlank()) {
            _youTubeSearchResults.value = emptyList()
            _isSearchingYouTube.value = false
            return
        }

        youTubeSearchJob = viewModelScope.launch {
            delay(350)
            _isSearchingYouTube.value = true
            val results = youTubeRepository.searchMusic(query)
            _youTubeSearchResults.value = results
            _isSearchingYouTube.value = false
        }
    }

    fun setGenreFilter(genre: String) {
        _selectedGenreFilter.value = genre
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
        partySimulationJob?.cancel()
    }
}
