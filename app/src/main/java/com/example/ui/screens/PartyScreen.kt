package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PartyGuest
import com.example.model.PartyRoom
import com.example.model.Track
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraGreen
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet

@Composable
fun PartyScreen(
    partyRoom: PartyRoom?,
    isHost: Boolean,
    currentTrack: Track,
    isPlaying: Boolean,
    availableTracks: List<Track>,
    onCreateParty: (roomName: String) -> Unit,
    onJoinParty: (pin: String) -> Unit,
    onLeaveParty: () -> Unit,
    onTogglePlay: () -> Unit,
    onSkipNext: () -> Unit,
    onSendReaction: (String) -> Unit,
    onAddToQueue: (Track) -> Unit,
    onSimulateGuestJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var inputPin by remember { mutableStateOf("") }
    var inputRoomName by remember { mutableStateOf("Friday Neon Jam") }

    if (partyRoom == null) {
        // Party Mode Splash & Creation / Join Lobby
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .testTag("party_screen_lobby"),
            contentPadding = PaddingValues(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Party Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aura Party Mode",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sync playback across 10+ devices simultaneously. Host controls the party vibes while friends join with PIN or QR.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xEEFFFFFF),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Host a Party Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161823)),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AuraGreen, AuraCyan)))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Host a New Party",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You broadcast audio, manage queue, and invite up to 10+ phones",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = inputRoomName,
                            onValueChange = { inputRoomName = it },
                            label = { Text("Party Room Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraGreen,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onCreateParty(inputRoomName.ifBlank { "Aura Jam" }) },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("create_party_btn")
                        ) {
                            Text(
                                text = "Start Party as Host 🎧",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Join with PIN Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161823)),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AuraViolet, AuraPink)))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Join Friend's Party",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter 6-digit Room PIN provided by host or shown on QR code",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = inputPin,
                            onValueChange = { if (it.length <= 6) inputPin = it },
                            placeholder = { Text("Enter 6-digit PIN (e.g. 842915)", color = Color(0xFF6B7280)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraViolet,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (inputPin.isNotBlank()) {
                                    onJoinParty(inputPin)
                                } else {
                                    onJoinParty("834192")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("join_party_btn")
                        ) {
                            Text(
                                text = "Join Party Session",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Active Party Room View
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .testTag("active_party_screen"),
            contentPadding = PaddingValues(bottom = 130.dp)
        ) {
            // Room Title Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = partyRoom.roomName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isHost) "👑 You are the Host" else "Joined • Host: ${partyRoom.hostName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isHost) AuraGreen else AuraCyan
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onLeaveParty,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("leave_party_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Leave", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Leave")
                    }
                }
            }

            // PIN & QR Code Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171924)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0x661DB954), Color(0x6600E5FF))))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PARTY ROOM PIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFA0A3B1),
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = partyRoom.roomCode,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AuraGreen,
                                    letterSpacing = 2.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(partyRoom.roomCode))
                                    Toast.makeText(context, "Room PIN copied!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy PIN", color = Color.White, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // QR Code
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            QrCodeView(
                                data = "auramusic://party/${partyRoom.roomCode}",
                                size = 110.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scan to Join",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E92A2), fontSize = 10.sp)
                            )
                        }
                    }
                }
            }

            // Connected Devices Roster (Supports 10+ Phones)
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AuraGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${partyRoom.totalDevicesCount} Phones Connected & Synced",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    // Quick button to add simulated phone guests (to easily reach 10+ phones)
                    Button(
                        onClick = onSimulateGuestJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300E5FF)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("simulate_add_phone_btn")
                    ) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Add Guest", tint = AuraCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Phone", color = AuraCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guest avatars horizontal list
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        // Host chip
                        GuestAvatarChip(
                            name = if (isHost) "You (Host)" else partyRoom.hostName,
                            device = "Host Device",
                            color = AuraGreen.value.toLong(),
                            isHost = true
                        )
                    }
                    items(partyRoom.guests) { guest ->
                        GuestAvatarChip(
                            name = guest.name,
                            device = guest.deviceModel,
                            color = guest.avatarColor,
                            isHost = false
                        )
                    }
                }
            }

            // Synced Now Playing Box
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Live Synced Audio Stream",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                val playingTrack = partyRoom.currentTrack ?: currentTrack
                val gradientColors = playingTrack.coverGradient.map { Color(it) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E212E)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(gradientColors))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(gradientColors)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(playingTrack.coverEmoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playingTrack.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${playingTrack.artist} • ${playingTrack.genre}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFA0A3B1)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Host Playback Controls
                            if (isHost) {
                                IconButton(
                                    onClick = onTogglePlay,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(AuraGreen)
                                        .testTag("party_host_play_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = "Host Play/Pause",
                                        tint = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = onSkipNext,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Filled.SkipNext, contentDescription = "Skip", tint = Color.White)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x331DB954))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "SYNCED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AuraGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Live Reaction Dock
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Send Live Reaction",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val emojis = listOf("🔥", "💃", "🎧", "❤️", "⚡", "🎉")
                    emojis.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .clickable { onSendReaction(emoji) }
                                .testTag("reaction_btn_$emoji"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 22.sp)
                        }
                    }
                }
            }

            // Live Reaction Activity Ticker
            item {
                if (partyRoom.recentReactions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(partyRoom.recentReactions) { reaction ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x33FFFFFF))
                                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${reaction.guestName}: ${reaction.emoji}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }
            }

            // Collaborative Party Queue
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Collaborative Queue (${partyRoom.queue.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (partyRoom.queue.isEmpty()) {
                    Text(
                        text = "No songs in queue yet. Add songs below!",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E92A2))
                    )
                } else {
                    partyRoom.queue.forEachIndexed { idx, qTrack ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x15FFFFFF))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${idx + 1}.", color = AuraGreen, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(qTrack.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text(qTrack.artist, color = Color(0xFFA0A3B1), fontSize = 12.sp, maxLines = 1)
                            }
                            Text(qTrack.formattedDuration, color = Color(0xFF6B7280), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Song Recommendation to Add to Queue
                Text(
                    text = "Request Song to Queue",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                availableTracks.take(4).forEach { candidate ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x11FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(candidate.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(candidate.artist, color = Color(0xFFA0A3B1), fontSize = 12.sp, maxLines = 1)
                        }

                        Button(
                            onClick = { onAddToQueue(candidate) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x331DB954)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = AuraGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", color = AuraGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestAvatarChip(
    name: String,
    device: String,
    color: Long,
    isHost: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(color))
                .border(2.dp, if (isHost) AuraGreen else Color(0x33FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isHost) "👑" else "📱",
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 10.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = device,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF8E92A2),
                fontSize = 9.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
