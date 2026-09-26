package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WinGoViewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGoCyan
import com.example.ui.theme.WinGoGold
import com.example.ui.theme.WinGoGreen
import com.example.ui.theme.WinGoRed

enum class WinGoScreen(val title: String) {
    DASHBOARD("Signal Center"),
    HISTORY("History & Trends"),
    SETTINGS("Bot Settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WinGoMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WinGoMainApp(viewModel: WinGoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val botConfig by viewModel.botConfig.collectAsStateWithLifecycle()
    val signals by viewModel.historySignals.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(WinGoScreen.DASHBOARD) }

    BackHandler(enabled = currentScreen != WinGoScreen.DASHBOARD) {
        currentScreen = WinGoScreen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = WinGoGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WIN GO 30s",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                text = "#${uiState.currentPeriod}",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = WinGoGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (uiState.isRunning) viewModel.stopSignalLoop() else viewModel.startSignalLoop()
                        },
                        modifier = Modifier.testTag("appbar_start_stop_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isRunning) Color(0x33EF4444) else Color(0x3310B981))
                                .border(
                                    1.dp,
                                    if (uiState.isRunning) WinGoRed else WinGoGreen,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (uiState.isRunning) "Stop" else "Start",
                                tint = if (uiState.isRunning) WinGoRed else WinGoGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == WinGoScreen.DASHBOARD,
                    onClick = { currentScreen = WinGoScreen.DASHBOARD },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == WinGoScreen.DASHBOARD) Icons.Default.Bolt else Icons.Outlined.Bolt,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Signal", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = WinGoGreen,
                        indicatorColor = WinGoGreen,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = currentScreen == WinGoScreen.HISTORY,
                    onClick = { currentScreen = WinGoScreen.HISTORY },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == WinGoScreen.HISTORY) Icons.Default.Assessment else Icons.Outlined.Assessment,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = WinGoCyan,
                        indicatorColor = WinGoCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_history")
                )

                NavigationBarItem(
                    selected = currentScreen == WinGoScreen.SETTINGS,
                    onClick = { currentScreen = WinGoScreen.SETTINGS },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == WinGoScreen.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = WinGoGold,
                        indicatorColor = WinGoGold,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                WinGoScreen.DASHBOARD -> DashboardScreen(
                    uiState = uiState,
                    botConfig = botConfig,
                    onStartLoop = { viewModel.startSignalLoop() },
                    onStopLoop = { viewModel.stopSignalLoop() },
                    onOpenSetPeriod = { viewModel.openSetPeriodDialog() },
                    onCloseSetPeriod = { viewModel.closeSetPeriodDialog() },
                    onSetPeriod = { viewModel.setPeriodNumber(it) },
                    onRegenerate = { viewModel.regeneratePrediction() },
                    onBroadcastSignalManually = { viewModel.broadcastCurrentSignalManually() },
                    onSubmitDirectNumberWin = { viewModel.submitDirectNumberWin() },
                    onSubmitBigSmallWin = { viewModel.submitBigSmallWin() },
                    onSubmitDirectLoss = { viewModel.submitDirectLoss() },
                    onSubmitNumber = { viewModel.submitResultNumber(it) }
                )

                WinGoScreen.HISTORY -> HistoryScreen(
                    signals = signals,
                    stats = stats,
                    onClearHistory = { viewModel.clearHistory() }
                )

                WinGoScreen.SETTINGS -> SettingsScreen(
                    config = botConfig,
                    isTestingConnection = uiState.isTestingConnection,
                    connectionDialogMessage = uiState.connectionDialogMessage,
                    onTestConnection = { viewModel.testTelegramConnection() },
                    onDismissConnectionDialog = { viewModel.dismissConnectionDialog() },
                    onSaveConfig = { viewModel.updateConfig(it) }
                )
            }
        }
    }
}
