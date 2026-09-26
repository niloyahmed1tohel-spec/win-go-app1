package com.example.data.repository

import android.content.Context
import com.example.data.local.SignalDao
import com.example.data.local.SignalEntity
import com.example.data.model.BotConfig
import com.example.data.telegram.TelegramBotService
import com.example.data.telegram.TelegramResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WinGoRepository(
    private val signalDao: SignalDao,
    private val telegramBotService: TelegramBotService,
    context: Context
) {
    private val prefs = context.getSharedPreferences("wingo_prefs", Context.MODE_PRIVATE)

    private val _botConfig = MutableStateFlow(loadConfig())
    val botConfig: StateFlow<BotConfig> = _botConfig.asStateFlow()

    private fun loadConfig(): BotConfig {
        return BotConfig(
            botToken = prefs.getString("bot_token", "8876521555:AAGeAmGpraiiLSSUR61ajKn3jIB5YXPT6dM")
                ?: "8876521555:AAGeAmGpraiiLSSUR61ajKn3jIB5YXPT6dM",
            chatId = prefs.getString("chat_id", "-1002646697361") ?: "-1002646697361",
            officialHandle = prefs.getString("official_handle", "@niloyEditzzone") ?: "@niloyEditzzone",
            autoSendTelegram = prefs.getBoolean("auto_send", true),
            timerIntervalSeconds = prefs.getInt("interval_seconds", 30),
            vibrationEnabled = prefs.getBoolean("vibration_enabled", true),
            autoAdvancePeriod = prefs.getBoolean("auto_advance", true)
        )
    }

    fun updateConfig(newConfig: BotConfig) {
        prefs.edit()
            .putString("bot_token", newConfig.botToken)
            .putString("chat_id", newConfig.chatId)
            .putString("official_handle", newConfig.officialHandle)
            .putBoolean("auto_send", newConfig.autoSendTelegram)
            .putInt("interval_seconds", newConfig.timerIntervalSeconds)
            .putBoolean("vibration_enabled", newConfig.vibrationEnabled)
            .putBoolean("auto_advance", newConfig.autoAdvancePeriod)
            .apply()
        _botConfig.value = newConfig
    }

    fun getAllSignals(): Flow<List<SignalEntity>> = signalDao.getAllSignals()

    fun getRecentSignals(limit: Int = 50): Flow<List<SignalEntity>> = signalDao.getRecentSignals(limit)

    suspend fun getLatestSignal(): SignalEntity? = signalDao.getLatestSignal()

    suspend fun saveSignal(signal: SignalEntity): Long = signalDao.insert(signal)

    suspend fun updateSignal(signal: SignalEntity) = signalDao.update(signal)

    suspend fun getSignalByPeriod(period: Long): SignalEntity? = signalDao.getSignalByPeriod(period)

    suspend fun clearHistory() = signalDao.clearAll()

    suspend fun sendTelegramMessage(textHtml: String): TelegramResult {
        val config = _botConfig.value
        if (!config.autoSendTelegram) {
            return TelegramResult.Success("Auto-send disabled (local only)")
        }
        return telegramBotService.sendMessage(config.botToken, config.chatId, textHtml)
    }

    suspend fun testTelegramConnection(): TelegramResult {
        val config = _botConfig.value
        return telegramBotService.testConnection(config.botToken, config.chatId)
    }
}
