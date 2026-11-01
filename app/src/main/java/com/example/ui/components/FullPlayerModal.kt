package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimationRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track
import com.example.ui.theme.AuraAmber
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraGreen
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet
import com.example.viewmodel.RepeatMode

@Composable
fun FullPlayerModal(
    track: Track,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    waveformAmplitudes: List<Float>,
    repeatMode: RepeatMode,
    isShuffled: Boolean,
    playbackSpeed: Float,
    audioQuality: String,
    downloadProgress: Float?,
    isLyricsOpen: Boolean,
    isVideoMode: Boolean = false,
    onClose: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDownload: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSpeedClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onShareClick: () -> Unit,
    onToggleLyrics: () -> Unit,
    onToggleVideoMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragSliderValue by remember { mutableFloatStateOf(0f) }

    val trackDuration = durationMs.coerceAtLeast(1L)
    val sliderValue = if (isDraggingSlider) dragSliderValue else (currentPositionMs.toFloat() / trackDuration).coerceIn(0f, 1f)

    val gradientColors = track.coverGradient.map { Color(it) }
    val primaryGradientColor = gradientColors.firstOrNull() ?: AuraGreen
    val secondaryGradientColor = gradientColors.lastOrNull() ?: AuraViolet

    // Rotating vinyl disc effect when playing
    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotate")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = AnimationRepeatMode.Restart
        ),
        label = "rotation"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("full_player_modal"),
        color = Color(0xFF090A0E)
    ) {
        // Fluid Dynamic Gradient Ambient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            primaryGradientColor.copy(alpha = 0.55f),
                            secondaryGradientColor.copy(alpha = 0.35f),
                            Color(0xFF090A0E)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Collapse Player",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PLAYING FROM ALBUM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFB0B3C1),
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = track.album,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Audio Quality Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (track.isYouTube) Color(0x33FF0000) else Color(0x33FFFFFF))
                            .border(1.dp, if (track.isYouTube) Color(0xFFFF0000) else Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (track.isYouTube) "YouTube HD" else "Hi-Res",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (track.isYouTube) Color(0xFFFF4D4D) else AuraGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // YouTube Mode Toggle Pill (Audio vs Video)
                if (track.isYouTube && track.youtubeVideoId != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isVideoMode) Color(0x33FFFFFF) else Color.Transparent)
                                .clickable { if (isVideoMode) onToggleVideoMode() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Vinyl Art",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isVideoMode) Color.White else Color(0xFFA0A3B1)
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isVideoMode) Color(0xFFFF0000) else Color.Transparent)
                                .clickable { if (!isVideoMode) onToggleVideoMode() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "YouTube Video ▶",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Vinyl & Artwork Display OR YouTube Video Player
                if (track.isYouTube && track.youtubeVideoId != null && isVideoMode && !isLyricsOpen) {
                    YouTubePlayerView(
                        videoId = track.youtubeVideoId,
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .aspectRatio(16f / 9f)
                    )
                } else if (!isLyricsOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(gradientColors)
                            )
                            .border(2.dp, Color(0x44FFFFFF), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient disc ring
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .rotate(if (isPlaying) discRotation else 0f)
                                .clip(CircleShape)
                                .background(Color(0x22000000))
                                .border(2.dp, Color(0x33FFFFFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = track.coverEmoji,
                                fontSize = 80.sp
                            )
                        }
                    }
                } else {
                    // Synchronized Interactive Lyrics View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x55000000))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "LYRICS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AuraGreen,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            track.lyrics.forEachIndexed { index, line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (index == 2) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (index == 2) Color.White else Color(0x99FFFFFF),
                                        fontSize = if (index == 2) 20.sp else 16.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Track Title, Artist & Action Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${track.artist} • ${track.genre}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFC0C4D6)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Download Offline Button
                    IconButton(
                        onClick = onToggleDownload,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_download_btn")
                    ) {
                        if (downloadProgress != null) {
                            CircularProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.size(24.dp),
                                color = AuraCyan,
                                strokeWidth = 2.5.dp
                            )
                        } else if (track.isDownloaded) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Downloaded Offline",
                                tint = AuraCyan
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Download Offline",
                                tint = Color.White
                            )
                        }
                    }

                    // Favorite Like Button
                    IconButton(
                        onClick = onToggleLike,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_like_btn")
                    ) {
                        Icon(
                            imageVector = if (track.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (track.isLiked) "Unlike" else "Like",
                            tint = if (track.isLiked) AuraPink else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Waveform Spectrum
                WaveformVisualizer(
                    amplitudes = waveformAmplitudes,
                    isPlaying = isPlaying,
                    primaryColor = primaryGradientColor,
                    secondaryColor = secondaryGradientColor,
                    height = 36.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Scrubbing Slider & Timestamps
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = sliderValue,
                        onValueChange = {
                            isDraggingSlider = true
                            dragSliderValue = it
                        },
                        onValueChangeFinished = {
                            isDraggingSlider = false
                            val targetMs = (dragSliderValue * trackDuration).toLong()
                            onSeek(targetMs)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = AuraGreen,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_player_scrub_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val posSeconds = (if (isDraggingSlider) (dragSliderValue * trackDuration).toLong() else currentPositionMs) / 1000
                        val durSeconds = trackDuration / 1000

                        Text(
                            text = "%d:%02d".format(posSeconds / 60, posSeconds % 60),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA0A3B1))
                        )
                        Text(
                            text = "%d:%02d".format(durSeconds / 60, durSeconds % 60),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA0A3B1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_shuffle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffled) AuraGreen else Color(0x88FFFFFF)
                        )
                    }

                    // Skip Previous
                    IconButton(
                        onClick = onSkipPrevious,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_prev_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Play / Pause Primary Button
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(AuraGreen)
                            .clickable(onClick = onTogglePlay)
                            .testTag("full_player_play_pause_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color.Black,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    // Skip Next
                    IconButton(
                        onClick = onSkipNext,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_next_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Repeat
                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("full_player_repeat_btn")
                    ) {
                        val icon = if (repeatMode == RepeatMode.ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat
                        val tint = if (repeatMode != RepeatMode.OFF) AuraGreen else Color(0x88FFFFFF)
                        Icon(
                            imageVector = icon,
                            contentDescription = "Repeat",
                            tint = tint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Utilities Row: Device Cast, Lyrics, Speed, Share
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Device Sync Button
                    IconButton(
                        onClick = onDeviceClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cast,
                            contentDescription = "Connect to Device",
                            tint = AuraCyan
                        )
                    }

                    // Lyrics Toggle Button
                    IconButton(
                        onClick = onToggleLyrics,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Lyrics",
                            tint = if (isLyricsOpen) AuraGreen else Color.White
                        )
                    }

                    // Playback Speed Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onSpeedClick)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${playbackSpeed}x",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (playbackSpeed != 1.0f) AuraAmber else Color.White
                            )
                        )
                    }

                    // Share Track
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share Track",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
