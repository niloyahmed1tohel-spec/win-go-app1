package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SignalEntity
import com.example.data.model.BotConfig
import com.example.data.model.WinGoLogic
import com.example.data.repository.WinGoRepository
import com.example.data.telegram.TelegramBotService
import com.example.data.telegram.TelegramResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WinGoUiState(
    val currentPeriod: Long = 100,
    val currentLuckyNumber: Int = 7,
    val currentPrediction: String = "BIG 🔼",
    val currentColor: String = "GREEN 🟢",
    val isRunning: Boolean = false,
    val secondsRemaining: Int = 30,
    val lastActionStatus: String? = null,
    val isActionSuccess: Boolean = true,
    val isSending: Boolean = false,
    val isTestingConnection: Boolean = false,
    val connectionDialogMessage: String? = null,
    val showSetPeriodDialog: Boolean = false,
    val lastBroadcastHtml: String? = null
)

data class WinGoStats(
    val totalSignals: Int = 0,
    val winCount: Int = 0,
    val lossCount: Int = 0,
    val winRate: Float = 0f,
    val currentStreak: Int = 0
)

class WinGoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WinGoRepository
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(WinGoUiState())
    val uiState: StateFlow<WinGoUiState> = _uiState.asStateFlow()

    val botConfig: StateFlow<BotConfig>

    val historySignals: StateFlow<List<SignalEntity>>

    val stats: StateFlow<WinGoStats>

    init {
        val database = AppDatabase.getDatabase(application)
        val telegramService = TelegramBotService()
        repository = WinGoRepository(database.signalDao(), telegramService, application)
        botConfig = repository.botConfig

        historySignals = repository.getAllSignals().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        stats = historySignals.map { list ->
            calculateStats(list)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            WinGoStats()
        )

        // Initialize state
        val (pred, lucky) = WinGoLogic.generatePrediction()
        _uiState.value = _uiState.value.copy(
            currentPrediction = pred,
            currentLuckyNumber = lucky,
            currentColor = WinGoLogic.getColor(lucky),
            secondsRemaining = botConfig.value.timerIntervalSeconds
        )

        // Load latest period from history if available
        viewModelScope.launch {
            val latest = repository.getLatestSignal()
            if (latest != null && latest.period >= _uiState.value.currentPeriod) {
                _uiState.value = _uiState.value.copy(currentPeriod = latest.period + 1)
            }
        }
    }

    private fun calculateStats(list: List<SignalEntity>): WinGoStats {
        val resolved = list.filter { it.isWin != null }
        val total = resolved.size
        val wins = resolved.count { it.isWin == true }
        val losses = resolved.count { it.isWin == false }
        val rate = if (total > 0) (wins.toFloat() / total) * 100f else 0f

        var streak = 0
        for (item in resolved) {
            if (item.isWin == true) {
                streak++
            } else {
                break
            }
        }

        return WinGoStats(
            totalSignals = total,
            winCount = wins,
            lossCount = losses,
            winRate = rate,
            currentStreak = streak
        )
    }

    fun startSignalLoop() {
        if (_uiState.value.isRunning) return
        _uiState.value = _uiState.value.copy(isRunning = true)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            // First send signal for current period if not yet broadcasted
            broadcastCurrentSignal()

            while (_uiState.value.isRunning) {
                val interval = botConfig.value.timerIntervalSeconds
                for (s in interval downTo 1) {
                    if (!_uiState.value.isRunning) break
                    _uiState.value = _uiState.value.copy(secondsRemaining = s)
                    delay(1000)
                }

                if (!_uiState.value.isRunning) break

                // Countdown finished! Trigger new period
                vibrate(100)
                val nextPeriod = _uiState.value.currentPeriod + 1
                val (newPred, newLucky) = WinGoLogic.generatePrediction()
                val newColor = WinGoLogic.getColor(newLucky)

                _uiState.value = _uiState.value.copy(
                    currentPeriod = nextPeriod,
                    currentPrediction = newPred,
                    currentLuckyNumber = newLucky,
                    currentColor = newColor,
                    secondsRemaining = interval
                )

                broadcastCurrentSignal()
            }
        }
    }

    fun stopSignalLoop() {
        _uiState.value = _uiState.value.copy(isRunning = false)
        timerJob?.cancel()
        timerJob = null
    }

    fun setPeriodNumber(newPeriod: Long) {
        if (newPeriod <= 0) return
        _uiState.value = _uiState.value.copy(
            currentPeriod = newPeriod,
            showSetPeriodDialog = false,
            lastActionStatus = "Period updated to #$newPeriod"
        )
    }

    fun openSetPeriodDialog() {
        _uiState.value = _uiState.value.copy(showSetPeriodDialog = true)
    }

    fun closeSetPeriodDialog() {
        _uiState.value = _uiState.value.copy(showSetPeriodDialog = false)
    }

    fun regeneratePrediction() {
        val (pred, lucky) = WinGoLogic.generatePrediction()
        _uiState.value = _uiState.value.copy(
            currentPrediction = pred,
            currentLuckyNumber = lucky,
            currentColor = WinGoLogic.getColor(lucky),
            lastActionStatus = "New prediction generated!"
        )
    }

    fun broadcastCurrentSignalManually() {
        viewModelScope.launch {
            broadcastCurrentSignal()
        }
    }

    private suspend fun broadcastCurrentSignal() {
        val state = _uiState.value
        val config = botConfig.value
        val messageHtml = WinGoLogic.formatSignalMessage(
            period = state.currentPeriod,
            luckyNumber = state.currentLuckyNumber,
            prediction = state.currentPrediction,
            color = state.currentColor,
            officialHandle = config.officialHandle
        )

        _uiState.value = _uiState.value.copy(
            isSending = true,
            lastBroadcastHtml = messageHtml
        )

        val result = repository.sendTelegramMessage(messageHtml)
        val statusString = when (result) {
            is TelegramResult.Success -> "DELIVERED"
            is TelegramResult.Error -> "FAILED"
        }

        // Save signal to local DB
        val signalEntity = SignalEntity(
            period = state.currentPeriod,
            luckyNumber = state.currentLuckyNumber,
            prediction = state.currentPrediction,
            color = state.currentColor,
            telegramStatus = statusString,
            signalMessage = messageHtml
        )
        repository.saveSignal(signalEntity)

        val actionMsg = when (result) {
            is TelegramResult.Success -> "📡 Signal posted for #${state.currentPeriod}!"
            is TelegramResult.Error -> "⚠️ Telegram error: ${result.errorMessage}"
        }

        _uiState.value = _uiState.value.copy(
            isSending = false,
            lastActionStatus = actionMsg,
            isActionSuccess = result is TelegramResult.Success
        )
    }

    fun submitDirectNumberWin() {
        viewModelScope.launch {
            val state = _uiState.value
            val config = botConfig.value
            val resultHtml = WinGoLogic.formatDirectNumberWinMessage(
                period = state.currentPeriod,
                luckyNumber = state.currentLuckyNumber,
                color = state.currentColor,
                officialHandle = config.officialHandle
            )

            _uiState.value = _uiState.value.copy(isSending = true, lastBroadcastHtml = resultHtml)
            val result = repository.sendTelegramMessage(resultHtml)

            updateDbWithResult(
                period = state.currentPeriod,
                resNumber = state.currentLuckyNumber,
                resColor = state.currentColor,
                status = "LUCKY NUMBER MATCH WIN ✅🎯",
                isWin = true,
                resultMsg = resultHtml
            )

            vibrate(200)
            _uiState.value = _uiState.value.copy(
                isSending = false,
                lastActionStatus = "🎯 Direct Number Win posted for #${state.currentPeriod}!",
                isActionSuccess = result is TelegramResult.Success
            )

            advancePeriodIfEnabled()
        }
    }

    fun submitBigSmallWin() {
        viewModelScope.launch {
            val state = _uiState.value
            val config = botConfig.value
            val resultHtml = WinGoLogic.formatBigSmallWinMessage(
                period = state.currentPeriod,
                prediction = state.currentPrediction,
                officialHandle = config.officialHandle
            )

            _uiState.value = _uiState.value.copy(isSending = true, lastBroadcastHtml = resultHtml)
            val result = repository.sendTelegramMessage(resultHtml)

            updateDbWithResult(
                period = state.currentPeriod,
                resNumber = null,
                resColor = null,
                status = "BIG/SMALL WIN ✅",
                isWin = true,
                resultMsg = resultHtml
            )

            vibrate(150)
            _uiState.value = _uiState.value.copy(
                isSending = false,
                lastActionStatus = "🔮 Big/Small Win posted for #${state.currentPeriod}!",
                isActionSuccess = result is TelegramResult.Success
            )

            advancePeriodIfEnabled()
        }
    }

    fun submitDirectLoss() {
        viewModelScope.launch {
            val state = _uiState.value
            val config = botConfig.value
            val resultHtml = WinGoLogic.formatDirectLossMessage(
                period = state.currentPeriod,
                officialHandle = config.officialHandle
            )

            _uiState.value = _uiState.value.copy(isSending = true, lastBroadcastHtml = resultHtml)
            val result = repository.sendTelegramMessage(resultHtml)

            updateDbWithResult(
                period = state.currentPeriod,
                resNumber = null,
                resColor = null,
                status = "LOSS ❌",
                isWin = false,
                resultMsg = resultHtml
            )

            vibrate(100)
            _uiState.value = _uiState.value.copy(
                isSending = false,
                lastActionStatus = "❌ Loss posted for #${state.currentPeriod}!",
                isActionSuccess = result is TelegramResult.Success
            )

            advancePeriodIfEnabled()
        }
    }

    fun submitResultNumber(selectedNum: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val config = botConfig.value
            val (statusText, resultHtml, isWin) = WinGoLogic.evaluateNumberResult(
                period = state.currentPeriod,
                currentLuckyNumber = state.currentLuckyNumber,
                currentPrediction = state.currentPrediction,
                selectedNum = selectedNum,
                officialHandle = config.officialHandle
            )

            _uiState.value = _uiState.value.copy(isSending = true, lastBroadcastHtml = resultHtml)
            val result = repository.sendTelegramMessage(resultHtml)

            updateDbWithResult(
                period = state.currentPeriod,
                resNumber = selectedNum,
                resColor = WinGoLogic.getColor(selectedNum),
                status = statusText,
                isWin = isWin,
                resultMsg = resultHtml
            )

            vibrate(if (isWin) 200 else 80)
            _uiState.value = _uiState.value.copy(
                isSending = false,
                lastActionStatus = "Result (#$selectedNum): $statusText",
                isActionSuccess = result is TelegramResult.Success
            )

            advancePeriodIfEnabled()
        }
    }

    private suspend fun updateDbWithResult(
        period: Long,
        resNumber: Int?,
        resColor: String?,
        status: String,
        isWin: Boolean,
        resultMsg: String
    ) {
        val existing = repository.getSignalByPeriod(period)
        if (existing != null) {
            val updated = existing.copy(
                resultNumber = resNumber,
                resultColor = resColor,
                resultStatus = status,
                isWin = isWin,
                resultMessage = resultMsg
            )
            repository.updateSignal(updated)
        } else {
            val newSignal = SignalEntity(
                period = period,
                luckyNumber = _uiState.value.currentLuckyNumber,
                prediction = _uiState.value.currentPrediction,
                color = _uiState.value.currentColor,
                resultNumber = resNumber,
                resultColor = resColor,
                resultStatus = status,
                isWin = isWin,
                signalMessage = WinGoLogic.formatSignalMessage(
                    period,
                    _uiState.value.currentLuckyNumber,
                    _uiState.value.currentPrediction,
                    _uiState.value.currentColor
                ),
                resultMessage = resultMsg
            )
            repository.saveSignal(newSignal)
        }
    }

    private fun advancePeriodIfEnabled() {
        if (botConfig.value.autoAdvancePeriod && !_uiState.value.isRunning) {
            val nextPeriod = _uiState.value.currentPeriod + 1
            val (pred, lucky) = WinGoLogic.generatePrediction()
            _uiState.value = _uiState.value.copy(
                currentPeriod = nextPeriod,
                currentPrediction = pred,
                currentLuckyNumber = lucky,
                currentColor = WinGoLogic.getColor(lucky)
            )
        }
    }

    fun testTelegramConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingConnection = true)
            val result = repository.testTelegramConnection()
            val message = when (result) {
                is TelegramResult.Success -> "✅ Success!\n${result.responseMessage}\nTelegram Bot API connected successfully."
                is TelegramResult.Error -> "❌ Connection Failed:\n${result.errorMessage}\n\nPlease check your Bot Token and internet connection."
            }
            _uiState.value = _uiState.value.copy(
                isTestingConnection = false,
                connectionDialogMessage = message
            )
        }
    }

    fun dismissConnectionDialog() {
        _uiState.value = _uiState.value.copy(connectionDialogMessage = null)
    }

    fun updateConfig(config: BotConfig) {
        repository.updateConfig(config)
        _uiState.value = _uiState.value.copy(
            secondsRemaining = config.timerIntervalSeconds,
            lastActionStatus = "Settings updated successfully"
        )
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.value = _uiState.value.copy(lastActionStatus = "History cleared")
        }
    }

    private fun vibrate(millis: Long) {
        if (!botConfig.value.vibrationEnabled) return
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(millis)
                }
            }
        } catch (_: Exception) {}
    }
}
