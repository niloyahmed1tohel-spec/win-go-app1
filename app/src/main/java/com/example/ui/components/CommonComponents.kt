package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun CountdownRing(
    secondsRemaining: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp
) {
    val progress = if (totalSeconds > 0) secondsRemaining.toFloat() / totalSeconds.toFloat() else 0f
    val isCritical = secondsRemaining in 1..5

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCritical && isRunning) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val activeColor = when {
        !isRunning -> TextMuted
        isCritical -> WinGoRed
        secondsRemaining <= 10 -> WinGoGold
        else -> WinGoGreen
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val diameter = this.size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)

            // Background track
            drawArc(
                color = Color(0xFF1E293B),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            // Active progress arc
            if (progress > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to activeColor,
                        0.5f to WinGoCyan,
                        1.0f to activeColor
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${secondsRemaining}s",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = if (isRunning) activeColor else TextSecondary,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.testTag("countdown_text")
            )
            Text(
                text = if (isRunning) "COUNTDOWN" else "STANDBY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isRunning) WinGoCyan else TextMuted,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun NumberBall(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val backgroundBrush = when (number) {
        0 -> Brush.horizontalGradient(listOf(WinGoRed, WinGoViolet))
        5 -> Brush.horizontalGradient(listOf(WinGoGreen, WinGoViolet))
        2, 4, 6, 8 -> Brush.radialGradient(listOf(WinGoRed, Color(0xFF991B1B)))
        else -> Brush.radialGradient(listOf(WinGoGreen, Color(0xFF065F46)))
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.White, CircleShape)
                } else {
                    Modifier.border(1.dp, Color(0x44FFFFFF), CircleShape)
                }
            )
            .background(backgroundBrush)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.45f).sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val isWin = status.contains("WIN")
    val isLoss = status.contains("LOSS")

    val (bgColor, textColor, borderColor) = when {
        isWin -> Triple(Color(0xFF064E3B), WinGoGreen, Color(0xFF10B981))
        isLoss -> Triple(Color(0xFF450A0A), WinGoRed, Color(0xFFEF4444))
        else -> Triple(Color(0xFF1E293B), WinGoCyan, Color(0xFF38BDF8))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TelegramPreviewCard(
    htmlMessage: String,
    modifier: Modifier = Modifier,
    title: String = "Telegram Post Preview"
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0x3338BDF8), Color(0x1110B981))))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WinGoCyan
                )

                IconButton(
                    onClick = {
                        val cleanText = htmlMessage
                            .replace("<b>", "")
                            .replace("</b>", "")
                        clipboardManager.setText(AnnotatedString(cleanText))
                        copied = true
                    },
                    modifier = Modifier.size(36.dp).testTag("copy_telegram_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = if (copied) WinGoGreen else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val displayLines = htmlMessage.split("\n")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D1424))
                    .padding(10.dp)
            ) {
                displayLines.forEach { line ->
                    val plain = line.replace("<b>", "").replace("</b>", "")
                    Text(
                        text = plain,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = when {
                            plain.contains("WIN GO") -> WinGoGold
                            plain.contains("WIN") -> WinGoGreen
                            plain.contains("LOSS") -> WinGoRed
                            plain.contains("PREDICTION") -> WinGoCyan
                            plain.contains("OFFICIAL") -> Color(0xFF60A5FA)
                            else -> TextPrimary
                        },
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SetPeriodDialog(
    currentPeriod: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var periodInput by remember { mutableStateOf(currentPeriod.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = WinGoGold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Set Period Number", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column {
                Text(
                    "Enter the official Win Go 30s period number:",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = periodInput,
                    onValueChange = {
                        periodInput = it.filter { char -> char.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("Period #") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val parsed = periodInput.toLongOrNull()
                            if (parsed != null && parsed > 0) {
                                onConfirm(parsed)
                            } else {
                                errorMessage = "Please enter a valid number"
                            }
                        }
                    ),
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = WinGoRed) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("period_input_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Increment buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 5, 10, 50).forEach { inc ->
                        OutlinedButton(
                            onClick = {
                                val current = periodInput.toLongOrNull() ?: currentPeriod
                                periodInput = (current + inc).toString()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("+$inc", fontSize = 12.sp, color = WinGoCyan)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = periodInput.toLongOrNull()
                    if (parsed != null && parsed > 0) {
                        onConfirm(parsed)
                    } else {
                        errorMessage = "Invalid period number"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WinGoGreen),
                modifier = Modifier.testTag("confirm_period_btn")
            ) {
                Text("Update Period", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurfaceVariant
    )
}
