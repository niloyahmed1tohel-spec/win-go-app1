package com.example.data.model

import kotlin.random.Random

enum class PredictionType(val label: String, val numbers: List<Int>) {
    BIG("BIG 🔼", listOf(5, 6, 7, 8, 9)),
    SMALL("SMALL 🔽", listOf(0, 1, 2, 3, 4))
}

object WinGoLogic {
    fun getColor(num: Int): String {
        return when (num) {
            0 -> "RED 🔴 + VIOLET 🟣"
            5 -> "GREEN 🟢 + VIOLET 🟣"
            2, 4, 6, 8 -> "RED 🔴"
            else -> "GREEN 🟢"
        }
    }

    fun getColorType(num: Int): WinGoColorType {
        return when (num) {
            0 -> WinGoColorType.RED_VIOLET
            5 -> WinGoColorType.GREEN_VIOLET
            2, 4, 6, 8 -> WinGoColorType.RED
            else -> WinGoColorType.GREEN
        }
    }

    fun generatePrediction(): Pair<String, Int> {
        val type = if (Random.nextBoolean()) PredictionType.BIG else PredictionType.SMALL
        val luckyNum = type.numbers.random()
        return Pair(type.label, luckyNum)
    }

    fun formatSignalMessage(
        period: Long,
        luckyNumber: Int,
        prediction: String,
        color: String,
        officialHandle: String = "@niloyEditzzone"
    ): String {
        return """⚡ <b>WIN GO 30s LIVE PREDICTION</b> ⚡
━━━━━━━━━━━━━━━━━━━━━
🆔 <b>PERIOD:</b> #$period
🎯 <b>LUCKY NUMBER:</b> #$luckyNumber
🔮 <b>PREDICTION:</b> $prediction
🎨 <b>COLOR:</b> $color
⏱️ <b>TIME:</b> 30 SECONDS LIVE
━━━━━━━━━━━━━━━━━━━━━
📢 <b>OFFICIAL:</b> $officialHandle"""
    }

    fun formatDirectNumberWinMessage(
        period: Long,
        luckyNumber: Int,
        color: String,
        officialHandle: String = "@niloyEditzzone"
    ): String {
        return """⚡ <b>WIN GO 30s RESULT</b> ⚡
━━━━━━━━━━━━━━━━━━━━━
🆔 <b>PERIOD:</b> #$period
🎯 <b>LUCKY NUMBER:</b> #$luckyNumber ✅ (RIGHT NUMBER)
🎨 <b>COLOR:</b> $color
🔥 <b>STATUS:</b> WIN ✅
━━━━━━━━━━━━━━━━━━━━━
📢 <b>OFFICIAL:</b> $officialHandle"""
    }

    fun formatBigSmallWinMessage(
        period: Long,
        prediction: String,
        officialHandle: String = "@niloyEditzzone"
    ): String {
        return """⚡ <b>WIN GO 30s RESULT</b> ⚡
━━━━━━━━━━━━━━━━━━━━━
🆔 <b>PERIOD:</b> #$period
🔮 <b>PREDICTION:</b> $prediction ✅ (RIGHT)
🔥 <b>STATUS:</b> WIN ✅
━━━━━━━━━━━━━━━━━━━━━
📢 <b>OFFICIAL:</b> $officialHandle"""
    }

    fun formatDirectLossMessage(
        period: Long,
        officialHandle: String = "@niloyEditzzone"
    ): String {
        return """⚡ <b>WIN GO 30s RESULT</b> ⚡
━━━━━━━━━━━━━━━━━━━━━
🆔 <b>PERIOD:</b> #$period
✨ <b>STATUS:</b> LOSS ❌
━━━━━━━━━━━━━━━━━━━━━
📢 <b>OFFICIAL:</b> $officialHandle"""
    }

    fun evaluateNumberResult(
        period: Long,
        currentLuckyNumber: Int,
        currentPrediction: String,
        selectedNum: Int,
        officialHandle: String = "@niloyEditzzone"
    ): Triple<String, String, Boolean> {
        val selectedColor = getColor(selectedNum)
        val isDirectNumber = selectedNum == currentLuckyNumber
        val isBigSmallMatch = (selectedNum >= 5 && currentPrediction.contains("BIG")) ||
                (selectedNum < 5 && currentPrediction.contains("SMALL"))

        val (statusText, isWin) = when {
            isDirectNumber -> Pair("LUCKY NUMBER MATCH WIN ✅🎯 (RIGHT NUMBER: #$selectedNum)", true)
            isBigSmallMatch -> Pair("BIG/SMALL WIN ✅ ($currentPrediction)", true)
            else -> Pair("LOSS ❌", false)
        }

        val message = """⚡ <b>WIN GO 30s RESULT</b> ⚡
━━━━━━━━━━━━━━━━━━━━━
🆔 <b>PERIOD:</b> #$period
🎯 <b>RESULT NUMBER:</b> #$selectedNum
🎨 <b>RESULT COLOR:</b> $selectedColor
🔥 <b>STATUS:</b> $statusText
━━━━━━━━━━━━━━━━━━━━━
📢 <b>OFFICIAL:</b> $officialHandle"""

        return Triple(statusText, message, isWin)
    }
}

enum class WinGoColorType {
    GREEN,
    RED,
    VIOLET,
    RED_VIOLET,
    GREEN_VIOLET
}

data class BotConfig(
    val botToken: String = "8876521555:AAGeAmGpraiiLSSUR61ajKn3jIB5YXPT6dM",
    val chatId: String = "-1002646697361",
    val officialHandle: String = "@niloyEditzzone",
    val autoSendTelegram: Boolean = true,
    val timerIntervalSeconds: Int = 30,
    val vibrationEnabled: Boolean = true,
    val autoAdvancePeriod: Boolean = true
)
