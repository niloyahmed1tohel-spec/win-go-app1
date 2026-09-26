package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BotConfig
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGoCyan
import com.example.ui.theme.WinGoGold
import com.example.ui.theme.WinGoGreen
import com.example.ui.theme.WinGoRed
import com.example.ui.theme.WinGoViolet

@Composable
fun SettingsScreen(
    config: BotConfig,
    isTestingConnection: Boolean,
    connectionDialogMessage: String?,
    onTestConnection: () -> Unit,
    onDismissConnectionDialog: () -> Unit,
    onSaveConfig: (BotConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var botToken by remember(config.botToken) { mutableStateOf(config.botToken) }
    var chatId by remember(config.chatId) { mutableStateOf(config.chatId) }
    var officialHandle by remember(config.officialHandle) { mutableStateOf(config.officialHandle) }
    var autoSend by remember(config.autoSendTelegram) { mutableStateOf(config.autoSendTelegram) }
    var timerInterval by remember(config.timerIntervalSeconds) { mutableStateOf(config.timerIntervalSeconds) }
    var vibrationEnabled by remember(config.vibrationEnabled) { mutableStateOf(config.vibrationEnabled) }
    var autoAdvance by remember(config.autoAdvancePeriod) { mutableStateOf(config.autoAdvancePeriod) }

    if (connectionDialogMessage != null) {
        AlertDialog(
            onDismissRequest = onDismissConnectionDialog,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (connectionDialogMessage.contains("Success")) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (connectionDialogMessage.contains("Success")) WinGoGreen else WinGoRed,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Connection Status", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Text(connectionDialogMessage, color = TextSecondary, fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = onDismissConnectionDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = WinGoCyan)
                ) {
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Telegram API Credentials
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(WinGoCyan, Color(0xFF1E293B)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TELEGRAM BOT INTEGRATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = WinGoCyan,
                            letterSpacing = 1.sp
                        )

                        OutlinedButton(
                            onClick = onTestConnection,
                            enabled = !isTestingConnection,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("test_telegram_conn_btn")
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = WinGoCyan
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Testing...", fontSize = 11.sp, color = WinGoCyan)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Cable,
                                    contentDescription = null,
                                    tint = WinGoCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Bot", fontSize = 11.sp, color = WinGoCyan)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = botToken,
                        onValueChange = {
                            botToken = it
                            onSaveConfig(
                                config.copy(
                                    botToken = botToken,
                                    chatId = chatId,
                                    officialHandle = officialHandle,
                                    autoSendTelegram = autoSend,
                                    timerIntervalSeconds = timerInterval,
                                    vibrationEnabled = vibrationEnabled,
                                    autoAdvancePeriod = autoAdvance
                                )
                            )
                        },
                        label = { Text("Telegram Bot Token") },
                        modifier = Modifier.fillMaxWidth().testTag("token_input_field"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = chatId,
                        onValueChange = {
                            chatId = it
                            onSaveConfig(
                                config.copy(
                                    botToken = botToken,
                                    chatId = chatId,
                                    officialHandle = officialHandle,
                                    autoSendTelegram = autoSend,
                                    timerIntervalSeconds = timerInterval,
                                    vibrationEnabled = vibrationEnabled,
                                    autoAdvancePeriod = autoAdvance
                                )
                            )
                        },
                        label = { Text("Channel / Chat ID (e.g. -100...)") },
                        modifier = Modifier.fillMaxWidth().testTag("chat_id_input_field"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = officialHandle,
                        onValueChange = {
                            officialHandle = it
                            onSaveConfig(
                                config.copy(
                                    botToken = botToken,
                                    chatId = chatId,
                                    officialHandle = officialHandle,
                                    autoSendTelegram = autoSend,
                                    timerIntervalSeconds = timerInterval,
                                    vibrationEnabled = vibrationEnabled,
                                    autoAdvancePeriod = autoAdvance
                                )
                            )
                        },
                        label = { Text("Official Channel Handle") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Send to Telegram", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            Text("Broadcast messages immediately via API", fontSize = 11.sp, color = TextSecondary)
                        }

                        Switch(
                            checked = autoSend,
                            onCheckedChange = {
                                autoSend = it
                                onSaveConfig(
                                    config.copy(
                                        botToken = botToken,
                                        chatId = chatId,
                                        officialHandle = officialHandle,
                                        autoSendTelegram = autoSend,
                                        timerIntervalSeconds = timerInterval,
                                        vibrationEnabled = vibrationEnabled,
                                        autoAdvancePeriod = autoAdvance
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = WinGoGreen
                            ),
                            modifier = Modifier.testTag("auto_send_switch")
                        )
                    }
                }
            }
        }

        // Loop & Game Timer Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "GAMEPLAY & AUTOMATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = WinGoGold,
                        letterSpacing = 1.sp
                    )

                    Text("Signal Loop Interval:", fontSize = 12.sp, color = TextSecondary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10 to "10s Turbo", 15 to "15s Fast", 30 to "30s Win Go", 60 to "60s Normal").forEach { (seconds, label) ->
                            FilterChip(
                                selected = timerInterval == seconds,
                                onClick = {
                                    timerInterval = seconds
                                    onSaveConfig(
                                        config.copy(
                                            botToken = botToken,
                                            chatId = chatId,
                                            officialHandle = officialHandle,
                                            autoSendTelegram = autoSend,
                                            timerIntervalSeconds = timerInterval,
                                            vibrationEnabled = vibrationEnabled,
                                            autoAdvancePeriod = autoAdvance
                                        )
                                    )
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = WinGoGold,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Advance Period", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            Text("Increment to next period upon result input", fontSize = 11.sp, color = TextSecondary)
                        }

                        Switch(
                            checked = autoAdvance,
                            onCheckedChange = {
                                autoAdvance = it
                                onSaveConfig(
                                    config.copy(
                                        botToken = botToken,
                                        chatId = chatId,
                                        officialHandle = officialHandle,
                                        autoSendTelegram = autoSend,
                                        timerIntervalSeconds = timerInterval,
                                        vibrationEnabled = vibrationEnabled,
                                        autoAdvancePeriod = autoAdvance
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = WinGoGreen
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Haptic Feedback & Vibrate", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            Text("Vibrate on signal broadcast and win results", fontSize = 11.sp, color = TextSecondary)
                        }

                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                vibrationEnabled = it
                                onSaveConfig(
                                    config.copy(
                                        botToken = botToken,
                                        chatId = chatId,
                                        officialHandle = officialHandle,
                                        autoSendTelegram = autoSend,
                                        timerIntervalSeconds = timerInterval,
                                        vibrationEnabled = vibrationEnabled,
                                        autoAdvancePeriod = autoAdvance
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = WinGoGreen
                            )
                        )
                    }
                }
            }
        }

        // Rules & Reference Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = WinGoCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WIN GO 30s NUMBER & COLOR RULES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WinGoCyan
                        )
                    }

                    Text("• BIG: Numbers 5, 6, 7, 8, 9", fontSize = 12.sp, color = TextSecondary)
                    Text("• SMALL: Numbers 0, 1, 2, 3, 4", fontSize = 12.sp, color = TextSecondary)
                    Text("• Number 0: RED 🔴 + VIOLET 🟣", fontSize = 12.sp, color = WinGoViolet)
                    Text("• Number 5: GREEN 🟢 + VIOLET 🟣", fontSize = 12.sp, color = WinGoGreen)
                    Text("• Even Numbers (2, 4, 6, 8): RED 🔴", fontSize = 12.sp, color = WinGoRed)
                    Text("• Odd Numbers (1, 3, 7, 9): GREEN 🟢", fontSize = 12.sp, color = WinGoGreen)
                }
            }
        }

        // Reset to Default button
        item {
            OutlinedButton(
                onClick = {
                    val defaultCfg = BotConfig()
                    botToken = defaultCfg.botToken
                    chatId = defaultCfg.chatId
                    officialHandle = defaultCfg.officialHandle
                    autoSend = defaultCfg.autoSendTelegram
                    timerInterval = defaultCfg.timerIntervalSeconds
                    vibrationEnabled = defaultCfg.vibrationEnabled
                    autoAdvance = defaultCfg.autoAdvancePeriod
                    onSaveConfig(defaultCfg)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = WinGoRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset to Script Defaults", color = WinGoRed, fontSize = 12.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
