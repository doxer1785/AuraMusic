package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SpeakerGroup
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Playlist
import com.example.ui.components.DeviceSyncSheet
import com.example.ui.components.FullPlayerModal
import com.example.ui.components.MiniPlayer
import com.example.ui.components.ShareTrackDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PartyScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SocialScreen
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraGreen
import com.example.ui.theme.AuraMusicTheme
import com.example.ui.theme.AuraPink
import com.example.viewmodel.AuraTab
import com.example.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraMusicTheme {
                val viewModel: MusicViewModel = viewModel()
                AuraMusicApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AuraMusicApp(viewModel: MusicViewModel) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isPlayerExpanded by viewModel.isPlayerExpanded.collectAsStateWithLifecycle()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsStateWithLifecycle()
    val isDevicePickerOpen by viewModel.isDevicePickerOpen.collectAsStateWithLifecycle()
    val isShareDialogOpen by viewModel.isShareDialogOpen.collectAsStateWithLifecycle()
    val isLyricsOpen by viewModel.isLyricsOpen.collectAsStateWithLifecycle()

    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val playbackProgress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val isShuffled by viewModel.isShuffled.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val audioQuality by viewModel.audioQuality.collectAsStateWithLifecycle()
    val isVideoMode by viewModel.isVideoMode.collectAsStateWithLifecycle()

    val allTracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val youTubeSearchResults by viewModel.youTubeSearchResults.collectAsStateWithLifecycle()
    val isSearchingYouTube by viewModel.isSearchingYouTube.collectAsStateWithLifecycle()
    val selectedGenreFilter by viewModel.selectedGenreFilter.collectAsStateWithLifecycle()

    val isOfflineOnly by viewModel.isOfflineOnlyMode.collectAsStateWithLifecycle()
    val downloadedTrackIds by viewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadingProgress by viewModel.downloadingProgress.collectAsStateWithLifecycle()
    val cacheSizeMb by viewModel.offlineCacheSizeMb.collectAsStateWithLifecycle()

    val partyRoom by viewModel.partyRoom.collectAsStateWithLifecycle()
    val isHost by viewModel.isHost.collectAsStateWithLifecycle()

    val friendsActivity by viewModel.friendsActivity.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    // Global Back navigation handler
    BackHandler(enabled = isPlayerExpanded || selectedPlaylist != null || activeTab != AuraTab.HOME) {
        when {
            isPlayerExpanded -> viewModel.setPlayerExpanded(false)
            selectedPlaylist != null -> viewModel.selectPlaylist(null)
            activeTab != AuraTab.HOME -> viewModel.setActiveTab(AuraTab.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackground)
    ) {
        Scaffold(
            containerColor = AuraBackground,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // MiniPlayer docked above the bottom bar
                    if (!isPlayerExpanded) {
                        val progressFraction = if (playbackProgress.durationMs > 0) {
                            playbackProgress.currentPositionMs.toFloat() / playbackProgress.durationMs
                        } else 0f

                        MiniPlayer(
                            track = currentTrack,
                            isPlaying = playbackProgress.isPlaying,
                            isBuffering = playbackProgress.isBuffering,
                            progressFraction = progressFraction,
                            onTogglePlay = { viewModel.togglePlayPause() },
                            onToggleLike = { viewModel.toggleLike(currentTrack.id) },
                            onExpand = { viewModel.setPlayerExpanded(true) }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = Color(0xF00F1118),
                        contentColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        // Home
                        NavigationBarItem(
                            selected = activeTab == AuraTab.HOME,
                            onClick = {
                                viewModel.selectPlaylist(null)
                                viewModel.setActiveTab(AuraTab.HOME)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == AuraTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuraGreen,
                                selectedTextColor = AuraGreen,
                                unselectedIconColor = Color(0xFF8E92A2),
                                unselectedTextColor = Color(0xFF8E92A2),
                                indicatorColor = Color(0x221DB954)
                            )
                        )

                        // Search
                        NavigationBarItem(
                            selected = activeTab == AuraTab.SEARCH,
                            onClick = {
                                viewModel.selectPlaylist(null)
                                viewModel.setActiveTab(AuraTab.SEARCH)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == AuraTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            },
                            label = { Text("Search", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuraGreen,
                                selectedTextColor = AuraGreen,
                                unselectedIconColor = Color(0xFF8E92A2),
                                unselectedTextColor = Color(0xFF8E92A2),
                                indicatorColor = Color(0x221DB954)
                            )
                        )

                        // Party Mode (with active party badge indicator)
                        NavigationBarItem(
                            selected = activeTab == AuraTab.PARTY,
                            onClick = {
                                viewModel.selectPlaylist(null)
                                viewModel.setActiveTab(AuraTab.PARTY)
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (partyRoom != null) {
                                            Badge(containerColor = AuraGreen) {
                                                Text("${partyRoom?.totalDevicesCount}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (activeTab == AuraTab.PARTY) Icons.Filled.SpeakerGroup else Icons.Outlined.SpeakerGroup,
                                        contentDescription = "Party Mode"
                                    )
                                }
                            },
                            label = { Text("Party", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuraGreen,
                                selectedTextColor = AuraGreen,
                                unselectedIconColor = Color(0xFF8E92A2),
                                unselectedTextColor = Color(0xFF8E92A2),
                                indicatorColor = Color(0x221DB954)
                            )
                        )

                        // Social
                        NavigationBarItem(
                            selected = activeTab == AuraTab.SOCIAL,
                            onClick = {
                                viewModel.selectPlaylist(null)
                                viewModel.setActiveTab(AuraTab.SOCIAL)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == AuraTab.SOCIAL) Icons.Filled.People else Icons.Outlined.People,
                                    contentDescription = "Friends"
                                )
                            },
                            label = { Text("Friends", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuraGreen,
                                selectedTextColor = AuraGreen,
                                unselectedIconColor = Color(0xFF8E92A2),
                                unselectedTextColor = Color(0xFF8E92A2),
                                indicatorColor = Color(0x221DB954)
                            )
                        )

                        // Library
                        NavigationBarItem(
                            selected = activeTab == AuraTab.LIBRARY,
                            onClick = {
                                viewModel.selectPlaylist(null)
                                viewModel.setActiveTab(AuraTab.LIBRARY)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == AuraTab.LIBRARY) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                                    contentDescription = "Library"
                                )
                            },
                            label = { Text("Library", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuraGreen,
                                selectedTextColor = AuraGreen,
                                unselectedIconColor = Color(0xFF8E92A2),
                                unselectedTextColor = Color(0xFF8E92A2),
                                indicatorColor = Color(0x221DB954)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (selectedPlaylist != null) {
                    PlaylistDetailScreen(
                        playlist = selectedPlaylist!!,
                        currentTrackId = currentTrack.id,
                        onBack = { viewModel.selectPlaylist(null) },
                        onTrackClick = { track -> viewModel.playTrack(track) },
                        onPlayAll = {
                            selectedPlaylist!!.tracks.firstOrNull()?.let {
                                viewModel.playTrack(it)
                            }
                        },
                        onShufflePlay = {
                            viewModel.toggleShuffle()
                            selectedPlaylist!!.tracks.randomOrNull()?.let {
                                viewModel.playTrack(it)
                            }
                        }
                    )
                } else {
                    when (activeTab) {
                        AuraTab.HOME -> {
                            HomeScreen(
                                playlists = allPlaylists,
                                tracks = allTracks,
                                currentTrackId = currentTrack.id,
                                selectedGenre = selectedGenreFilter,
                                onSelectGenre = { viewModel.setGenreFilter(it) },
                                onTrackClick = { viewModel.playTrack(it) },
                                onPlaylistClick = { viewModel.selectPlaylist(it) },
                                onOpenPartyTab = { viewModel.setActiveTab(AuraTab.PARTY) }
                            )
                        }

                        AuraTab.SEARCH -> {
                            SearchScreen(
                                query = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                allTracks = allTracks,
                                allPlaylists = allPlaylists,
                                currentTrackId = currentTrack.id,
                                onTrackClick = { viewModel.playTrack(it) },
                                onPlaylistClick = { viewModel.selectPlaylist(it) },
                                onImportYouTube = { title, artist, url ->
                                    viewModel.importYouTubeTrack(title, artist, url)
                                },
                                youTubeSearchResults = youTubeSearchResults,
                                isSearchingYouTube = isSearchingYouTube
                            )
                        }

                        AuraTab.PARTY -> {
                            PartyScreen(
                                partyRoom = partyRoom,
                                isHost = isHost,
                                currentTrack = currentTrack,
                                isPlaying = playbackProgress.isPlaying,
                                availableTracks = allTracks,
                                onCreateParty = { name -> viewModel.createPartyRoom(name) },
                                onJoinParty = { pin -> viewModel.joinPartyRoom(pin) },
                                onLeaveParty = { viewModel.leaveParty() },
                                onTogglePlay = { viewModel.togglePlayPause() },
                                onSkipNext = { viewModel.skipNext() },
                                onSendReaction = { emoji -> viewModel.sendPartyReaction(emoji) },
                                onAddToQueue = { track -> viewModel.addTrackToPartyQueue(track) },
                                onSimulateGuestJoin = { viewModel.addSimulatedPartyGuest() }
                            )
                        }

                        AuraTab.SOCIAL -> {
                            SocialScreen(
                                friends = friendsActivity,
                                currentTrack = currentTrack,
                                onListenAlong = { friend -> viewModel.listenAlongWithFriend(friend) },
                                onShareTrack = { viewModel.setShareDialogOpen(true) }
                            )
                        }

                        AuraTab.LIBRARY -> {
                            LibraryScreen(
                                isOfflineOnly = isOfflineOnly,
                                onToggleOfflineOnly = { viewModel.toggleOfflineOnlyMode(it) },
                                cacheSizeMb = cacheSizeMb,
                                onClearCache = { viewModel.clearOfflineCache() },
                                allTracks = allTracks,
                                downloadedTrackIds = downloadedTrackIds,
                                currentTrackId = currentTrack.id,
                                onTrackClick = { viewModel.playTrack(it) },
                                onToggleDownload = { viewModel.toggleDownloadTrack(it) },
                                onPlaylistClick = { viewModel.selectPlaylist(it) },
                                playlists = allPlaylists
                            )
                        }
                    }
                }
            }
        }

        // Full Screen Dynamic Gradient Player Modal
        AnimatedVisibility(
            visible = isPlayerExpanded,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            val downloadProg = downloadingProgress[currentTrack.id]
            FullPlayerModal(
                track = currentTrack,
                isPlaying = playbackProgress.isPlaying,
                isBuffering = playbackProgress.isBuffering,
                currentPositionMs = playbackProgress.currentPositionMs,
                durationMs = playbackProgress.durationMs,
                waveformAmplitudes = playbackProgress.waveformAmplitudes,
                repeatMode = repeatMode,
                isShuffled = isShuffled,
                playbackSpeed = playbackSpeed,
                audioQuality = audioQuality,
                downloadProgress = downloadProg,
                isLyricsOpen = isLyricsOpen,
                isVideoMode = isVideoMode,
                onClose = { viewModel.setPlayerExpanded(false) },
                onTogglePlay = { viewModel.togglePlayPause() },
                onSeek = { viewModel.seekTo(it) },
                onSkipNext = { viewModel.skipNext() },
                onSkipPrevious = { viewModel.skipPrevious() },
                onToggleLike = { viewModel.toggleLike(currentTrack.id) },
                onToggleDownload = { viewModel.toggleDownloadTrack(currentTrack) },
                onToggleShuffle = { viewModel.toggleShuffle() },
                onToggleRepeat = { viewModel.toggleRepeat() },
                onSpeedClick = {
                    val nextSpeed = when (playbackSpeed) {
                        0.75f -> 1.0f
                        1.0f -> 1.25f
                        1.25f -> 1.5f
                        else -> 0.75f
                    }
                    viewModel.setPlaybackSpeed(nextSpeed)
                },
                onDeviceClick = { viewModel.setDevicePickerOpen(true) },
                onShareClick = { viewModel.setShareDialogOpen(true) },
                onToggleLyrics = { viewModel.setLyricsOpen(!isLyricsOpen) },
                onToggleVideoMode = { viewModel.toggleVideoMode() }
            )
        }

        // Cross-platform Device Cast Sheet
        if (isDevicePickerOpen) {
            DeviceSyncSheet(
                devices = devices,
                onSelectDevice = {
                    viewModel.switchDevice(it)
                    viewModel.setDevicePickerOpen(false)
                },
                onDismiss = { viewModel.setDevicePickerOpen(false) }
            )
        }

        // Social Track Share Dialog
        if (isShareDialogOpen) {
            ShareTrackDialog(
                track = currentTrack,
                onDismiss = { viewModel.setShareDialogOpen(false) }
            )
        }
    }
}
