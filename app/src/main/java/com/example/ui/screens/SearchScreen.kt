package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.theme.AuraGreen

data class GenreCardData(
    val name: String,
    val emoji: String,
    val gradient: List<Long>
)

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    allTracks: List<Track>,
    allPlaylists: List<Playlist>,
    currentTrackId: String?,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onImportYouTube: (title: String, artist: String, url: String) -> Boolean,
    youTubeSearchResults: List<Track> = emptyList(),
    isSearchingYouTube: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var isImportCardOpen by remember { mutableStateOf(false) }
    var youtubeUrlInput by remember { mutableStateOf("") }
    var youtubeTitleInput by remember { mutableStateOf("") }
    var youtubeArtistInput by remember { mutableStateOf("") }

    val genreCards = listOf(
        GenreCardData("YouTube Top Hits", "▶️", listOf(0xFFFF0000, 0xFF790000, 0xFF121212)),
        GenreCardData("Synthwave & Cyber", "🌆", listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)),
        GenreCardData("Lofi & Study Beats", "☕", listOf(0xFF00B09B, 0xFF96C93D)),
        GenreCardData("Indie Dream Pop", "✨", listOf(0xFF654EA3, 0xFFEAAFC8)),
        GenreCardData("Electronic Club", "⚡", listOf(0xFF7F00FF, 0xFFE100FF)),
        GenreCardData("Acoustic Sunset", "🌊", listOf(0xFF2193B0, 0xFF6DD5ED)),
        GenreCardData("Party Anthems", "🔥", listOf(0xFFFF0844, 0xFFFFB199)),
        GenreCardData("R&B / Late Night", "🌅", listOf(0xFFFA709A, 0xFFFEE140))
    )

    val filteredTracks = if (query.isBlank()) {
        emptyList()
    } else {
        val local = allTracks.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.album.contains(query, ignoreCase = true) ||
            it.genre.contains(query, ignoreCase = true)
        }
        (local + youTubeSearchResults).distinctBy { it.id }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .testTag("search_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search & Discover",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            // YouTube Import Toggle Button
            Button(
                onClick = { isImportCardOpen = !isImportCardOpen },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isImportCardOpen) Color(0xFFFF0000) else Color(0x33FF0000)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isImportCardOpen) "Close" else "+ YouTube",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input Bar
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search songs, artists, or YouTube music...", color = Color(0xFF8E92A2)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = Color.White
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1B1D27),
                unfocusedContainerColor = Color(0xFF161822),
                focusedBorderColor = AuraGreen,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input_field")
        )

        // Expandable YouTube Import Card
        if (isImportCardOpen) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1215)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFFF0000), Color(0xFFFF5252)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("▶️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Any YouTube Music Track",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Paste any YouTube video/song link (e.g. youtu.be/... or ID)",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = youtubeUrlInput,
                            onValueChange = { youtubeUrlInput = it },
                            placeholder = { Text("Paste YouTube URL or ID", fontSize = 12.sp, color = Color(0xFF757575)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFF0000),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    youtubeUrlInput = clip
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x33FFFFFF))
                        ) {
                            Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = youtubeTitleInput,
                            onValueChange = { youtubeTitleInput = it },
                            placeholder = { Text("Title (Optional)", fontSize = 12.sp, color = Color(0xFF757575)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0x66FFFFFF),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = youtubeArtistInput,
                            onValueChange = { youtubeArtistInput = it },
                            placeholder = { Text("Artist (Optional)", fontSize = 12.sp, color = Color(0xFF757575)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0x66FFFFFF),
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (youtubeUrlInput.isBlank()) {
                                Toast.makeText(context, "Please paste a YouTube URL", Toast.LENGTH_SHORT).show()
                            } else {
                                val success = onImportYouTube(youtubeTitleInput, youtubeArtistInput, youtubeUrlInput)
                                if (success) {
                                    Toast.makeText(context, "Added track from YouTube!", Toast.LENGTH_SHORT).show()
                                    youtubeUrlInput = ""
                                    youtubeTitleInput = ""
                                    youtubeArtistInput = ""
                                    isImportCardOpen = false
                                } else {
                                    Toast.makeText(context, "Invalid YouTube URL or ID", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import & Play from YouTube", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Results (${filteredTracks.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                if (isSearchingYouTube) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Searching YouTube...",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF5252))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color(0xFFFF0000),
                            strokeWidth = 1.5.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No tracks found for \"$query\"",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFA0A3B1))
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredTracks) { track ->
                        TrackRowItem(
                            index = filteredTracks.indexOf(track) + 1,
                            track = track,
                            isPlaying = track.id == currentTrackId,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Browse All Genres & YouTube Music",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(genreCards) { card ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.6f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(card.gradient.map { Color(it) }))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .clickable {
                                if (card.name == "YouTube Top Hits") {
                                    val ytPlaylist = allPlaylists.firstOrNull { it.id == "youtube_top_hits" }
                                    if (ytPlaylist != null) {
                                        onPlaylistClick(ytPlaylist)
                                    } else {
                                        onQueryChange("YouTube")
                                    }
                                } else {
                                    onQueryChange(card.name.substringBefore(" "))
                                }
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = card.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = card.emoji,
                                fontSize = 28.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
