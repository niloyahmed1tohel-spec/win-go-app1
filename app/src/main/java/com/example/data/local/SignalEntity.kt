package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class SignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val period: Long,
    val luckyNumber: Int,
    val prediction: String,
    val color: String,
    val timestamp: Long = System.currentTimeMillis(),
    val resultNumber: Int? = null,
    val resultColor: String? = null,
    val resultStatus: String? = null,
    val isWin: Boolean? = null,
    val telegramStatus: String = "DELIVERED",
    val signalMessage: String,
    val resultMessage: String? = null
)
