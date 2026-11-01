package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraGreen
import com.example.ui.theme.AuraPink

@Composable
fun LibraryScreen(
    isOfflineOnly: Boolean,
    onToggleOfflineOnly: (Boolean) -> Unit,
    cacheSizeMb: Float,
    onClearCache: () -> Unit,
    allTracks: List<Track>,
    downloadedTrackIds: Set<String>,
    currentTrackId: String?,
    onTrackClick: (Track) -> Unit,
    onToggleDownload: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    playlists: List<Playlist>,
    modifier: Modifier = Modifier
) {
    val likedTracks = allTracks.filter { it.isLiked }
    val downloadedTracks = allTracks.filter { downloadedTrackIds.contains(it.id) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .testTag("library_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Offline Mode Toggle Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOfflineOnly) Color(0xFF1E2836) else Color(0xFF151722)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        if (isOfflineOnly) listOf(AuraCyan, Color(0xFF2193B0)) else listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isOfflineOnly) AuraCyan else Color(0x22FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudOff,
                                contentDescription = "Offline Mode",
                                tint = if (isOfflineOnly) Color.Black else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Offline Mode Only",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (isOfflineOnly) "Listening to downloaded songs without internet" else "Stream online or listen offline",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1))
                            )
                        }
                    }

                    Switch(
                        checked = isOfflineOnly,
                        onCheckedChange = onToggleOfflineOnly,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AuraCyan,
                            uncheckedThumbColor = Color(0xFFA0A3B1),
                            uncheckedTrackColor = Color(0x22FFFFFF)
                        ),
                        modifier = Modifier.testTag("offline_mode_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Offline Storage Cache Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141620))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Offline Music Storage",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFA0A3B1))
                        )
                        Text(
                            text = "${"%.1f".format(cacheSizeMb)} MB Used • ${downloadedTracks.size} Songs Cached",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraGreen
                            )
                        )
                    }

                    if (downloadedTracks.isNotEmpty()) {
                        Button(
                            onClick = onClearCache,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FF5252)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", color = Color(0xFFFF5252), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Liked Songs Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (likedTracks.isNotEmpty()) onTrackClick(likedTracks.first())
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181A25)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AuraPink, Color(0xFF8A2387))))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Liked", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Liked Songs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${likedTracks.size} liked songs",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AuraPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Downloaded For Offline Play (${downloadedTracks.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (downloadedTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📥", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tracks downloaded yet",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFA0A3B1))
                        )
                        Text(
                            text = "Tap the download icon on any song to save offline",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280))
                        )
                    }
                }
            }
        } else {
            items(downloadedTracks) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (track.id == currentTrackId) Color(0x221DB954) else Color(0x11FFFFFF))
                        .clickable { onTrackClick(track) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(track.coverGradient.map { Color(it) })),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(track.coverEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (track.id == currentTrackId) AuraGreen else Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.DownloadDone,
                                contentDescription = "Downloaded",
                                tint = AuraCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Downloaded • ${track.artist}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = { onToggleDownload(track) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove download",
                            tint = Color(0xFF8E92A2)
                        )
                    }
                }
            }
        }
    }
}
