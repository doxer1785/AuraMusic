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
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.FriendActivity
import com.example.model.Track
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraGreen
import com.example.ui.theme.AuraPink
import com.example.ui.theme.AuraViolet

@Composable
fun SocialScreen(
    friends: List<FriendActivity>,
    currentTrack: Track,
    onListenAlong: (FriendActivity) -> Unit,
    onShareTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .testTag("social_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Friends Activity",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "See what your friends are jamming to in real-time",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0A3B1))
                    )
                }

                Button(
                    onClick = onShareTrack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currently Listening Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161822)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AuraGreen, AuraCyan)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(AuraGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Headphones, contentDescription = "Listening", tint = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Listening Status",
                            style = MaterialTheme.typography.labelSmall.copy(color = AuraGreen, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${currentTrack.title} — ${currentTrack.artist}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Broadcasting to 4 friends",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8E92A2), fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Live Stream Activity",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(friends) { friend ->
            FriendActivityCard(
                friend = friend,
                onListenAlong = { onListenAlong(friend) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun FriendActivityCard(
    friend: FriendActivity,
    onListenAlong: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161823)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Friend Avatar with Online Dot
            Box {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(friend.avatarColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.name.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                if (friend.isLive) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(AuraGreen)
                            .border(2.dp, Color(0xFF161823), CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${friend.timeAgo}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (friend.isLive) AuraGreen else Color(0xFF8E92A2)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${friend.listeningTrack.title} • ${friend.listeningTrack.artist}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFD1D5DB)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "From ${friend.playlistName}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF6B7280),
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Listen Along Button
            Button(
                onClick = onListenAlong,
                colors = ButtonDefaults.buttonColors(containerColor = if (friend.isLive) Color(0x331DB954) else Color(0x22FFFFFF)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Listen Along",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (friend.isLive) AuraGreen else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
