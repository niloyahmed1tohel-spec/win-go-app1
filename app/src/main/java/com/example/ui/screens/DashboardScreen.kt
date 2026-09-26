package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BotConfig
import com.example.ui.WinGoUiState
import com.example.ui.components.CountdownRing
import com.example.ui.components.NumberBall
import com.example.ui.components.SetPeriodDialog
import com.example.ui.components.TelegramPreviewCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGoCyan
import com.example.ui.theme.WinGoCyanLight
import com.example.ui.theme.WinGoGold
import com.example.ui.theme.WinGoGreen
import com.example.ui.theme.WinGoGreenDark
import com.example.ui.theme.WinGoRed
import com.example.ui.theme.WinGoRedDark
import com.example.ui.theme.WinGoViolet

@Composable
fun DashboardScreen(
    uiState: WinGoUiState,
    botConfig: BotConfig,
    onStartLoop: () -> Unit,
    onStopLoop: () -> Unit,
    onOpenSetPeriod: () -> Unit,
    onCloseSetPeriod: () -> Unit,
    onSetPeriod: (Long) -> Unit,
    onRegenerate: () -> Unit,
    onBroadcastSignalManually: () -> Unit,
    onSubmitDirectNumberWin: () -> Unit,
    onSubmitBigSmallWin: () -> Unit,
    onSubmitDirectLoss: () -> Unit,
    onSubmitNumber: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.showSetPeriodDialog) {
        SetPeriodDialog(
            currentPeriod = uiState.currentPeriod,
            onConfirm = onSetPeriod,
            onDismiss = onCloseSetPeriod
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card with Banner Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Brush.horizontalGradient(listOf(WinGoCyan, WinGoViolet)), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wingo_banner_1790440832167),
                    contentDescription = "Win Go 30s Header Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC090D16))
                            )
                        )
                )

                // Overlay Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (uiState.isRunning) WinGoGreenDark else Color(0xFF374151)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (uiState.isRunning) WinGoGreen else WinGoRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (uiState.isRunning) "LIVE 30s RUNNING" else "STANDBY PAUSED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x66000000)
                        ) {
                            Text(
                                text = botConfig.officialHandle,
                                fontSize = 11.sp,
                                color = WinGoCyanLight,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "WIN GO 30s",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "SIGNAL & RESULT CONTROLLER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WinGoGold
                            )
                        }

                        // Period pill with edit icon
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.clickable { onOpenSetPeriod() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Period #${uiState.currentPeriod}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WinGoGold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Period",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Status Toast / Alert if present
        if (uiState.lastActionStatus != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (uiState.isActionSuccess) Color(0xFF064E3B) else Color(0xFF450A0A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.isActionSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (uiState.isActionSuccess) WinGoGreen else WinGoRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.lastActionStatus,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active Prediction Showcase Card & 30s Countdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if (uiState.currentPrediction.contains("BIG")) WinGoCyan else WinGoGold,
                            WinGoViolet
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CURRENT PREDICTION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = WinGoCyan
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Broadcasting...",
                                    fontSize = 11.sp,
                                    color = WinGoCyan
                                )
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = if (botConfig.autoSendTelegram) "Telegram Auto-On" else "Local Only",
                                        fontSize = 10.sp,
                                        color = if (botConfig.autoSendTelegram) WinGoGreen else TextMuted,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row with Countdown and Prediction Details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Countdown Ring
                        CountdownRing(
                            secondsRemaining = uiState.secondsRemaining,
                            totalSeconds = botConfig.timerIntervalSeconds,
                            isRunning = uiState.isRunning,
                            size = 125.dp
                        )

                        // Prediction Info Box
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Big / Small badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (uiState.currentPrediction.contains("BIG")) Color(0xFF1E3A8A) else Color(0xFF312E81),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (uiState.currentPrediction.contains("BIG")) WinGoCyan else WinGoViolet
                                )
                            ) {
                                Text(
                                    text = uiState.currentPrediction,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }

                            // Lucky Number and Color Ball
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NumberBall(
                                    number = uiState.currentLuckyNumber,
                                    size = 46.dp
                                )

                                Column {
                                    Text(
                                        text = "LUCKY NO.",
                                        fontSize = 9.sp,
                                        color = TextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "#${uiState.currentLuckyNumber}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = uiState.currentColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when {
                                            uiState.currentColor.contains("GREEN") -> WinGoGreen
                                            uiState.currentColor.contains("RED") -> WinGoRed
                                            else -> WinGoViolet
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Signal control buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (uiState.isRunning) onStopLoop() else onStartLoop()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isRunning) WinGoRed else WinGoGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("start_stop_signal_btn")
                        ) {
                            Icon(
                                imageVector = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isRunning) "Stop 30s Loop" else "Start 30s Loop",
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onRegenerate,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("regenerate_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = "Regenerate",
                                tint = WinGoCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = onBroadcastSignalManually,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("send_manual_signal_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Broadcast Now",
                                tint = WinGoGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Post", color = WinGoGold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Result Decision & Broadcasting Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(Color(0xFF374151), Color(0xFF1E293B)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "POST RESULT (ADMIN PANEL)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = WinGoGold,
                        letterSpacing = 1.sp
                    )

                    // 3 Direct Result Buttons: Number Win, Big/Small Win, Loss
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSubmitDirectNumberWin,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_win_num")
                        ) {
                            Text(
                                "🎯 Number Win",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onSubmitBigSmallWin,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_win_bs")
                        ) {
                            Text(
                                "🔮 Big/Small Win",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onSubmitDirectLoss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_loss")
                        ) {
                            Text(
                                "❌ Loss",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "0 - 9 KEYPAD (AUTO-CALCULATES WIN/LOSS):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    // Keypad Row 1: 0, 1, 2, 3, 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (0..4).forEach { num ->
                            NumberBall(
                                number = num,
                                size = 48.dp,
                                isSelected = num == uiState.currentLuckyNumber,
                                onClick = { onSubmitNumber(num) },
                                modifier = Modifier.testTag("num_pad_$num")
                            )
                        }
                    }

                    // Keypad Row 2: 5, 6, 7, 8, 9
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (5..9).forEach { num ->
                            NumberBall(
                                number = num,
                                size = 48.dp,
                                isSelected = num == uiState.currentLuckyNumber,
                                onClick = { onSubmitNumber(num) },
                                modifier = Modifier.testTag("num_pad_$num")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "0: Red+Violet | 5: Green+Violet",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            "Even: Red | Odd: Green",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Live Telegram Post Preview Card
        item {
            val previewText = uiState.lastBroadcastHtml ?: com.example.data.model.WinGoLogic.formatSignalMessage(
                period = uiState.currentPeriod,
                luckyNumber = uiState.currentLuckyNumber,
                prediction = uiState.currentPrediction,
                color = uiState.currentColor,
                officialHandle = botConfig.officialHandle
            )

            TelegramPreviewCard(
                htmlMessage = previewText,
                title = if (uiState.lastBroadcastHtml != null) "Latest Broadcast Feed" else "Live Signal Template"
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
