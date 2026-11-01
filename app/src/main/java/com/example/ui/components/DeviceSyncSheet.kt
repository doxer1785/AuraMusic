package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceType
import com.example.model.SyncDevice
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSyncSheet(
    devices: List<SyncDevice>,
    onSelectDevice: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var deviceVolume by remember { mutableFloatStateOf(0.7f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF14161F),
        modifier = Modifier.testTag("device_sync_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Connect to a Device",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Seamless cross-platform streaming & party handoff",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFA0A3B1)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            devices.forEach { device ->
                val icon = when (device.type) {
                    DeviceType.PHONE -> Icons.Filled.PhoneAndroid
                    DeviceType.SPEAKER -> Icons.Filled.Speaker
                    DeviceType.COMPUTER -> Icons.Filled.Computer
                    DeviceType.CAR -> Icons.Filled.DirectionsCar
                    DeviceType.TV -> Icons.Filled.Tv
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (device.isCurrent) Color(0x221DB954) else Color(0x11FFFFFF))
                        .border(
                            1.dp,
                            if (device.isCurrent) AuraGreen else Color(0x22FFFFFF),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectDevice(device.id) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (device.isCurrent) AuraGreen else Color(0x22FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = device.name,
                            tint = if (device.isCurrent) Color.Black else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (device.isCurrent) AuraGreen else Color.White
                            )
                        )
                        val subtitle = if (device.isCurrent) {
                            "Currently Playing Here"
                        } else if (device.batteryPercent != null) {
                            "Ready • Battery ${device.batteryPercent}%"
                        } else {
                            "AuraCast Synced Device"
                        }
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (device.isCurrent) AuraGreen.copy(alpha = 0.8f) else Color(0xFF8E92A2)
                            )
                        )
                    }

                    if (device.isCurrent) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Active",
                            tint = AuraGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Volume Control
            Text(
                text = "Device Volume",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            Slider(
                value = deviceVolume,
                onValueChange = { deviceVolume = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = AuraCyan,
                    inactiveTrackColor = Color(0x33FFFFFF)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
