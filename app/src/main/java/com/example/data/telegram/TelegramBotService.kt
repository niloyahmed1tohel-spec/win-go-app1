package com.example.data.telegram

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class TelegramResult {
    data class Success(val responseMessage: String) : TelegramResult()
    data class Error(val errorMessage: String) : TelegramResult()
}

class TelegramBotService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(botToken: String, chatId: String, htmlText: String): TelegramResult {
        return withContext(Dispatchers.IO) {
            try {
                val cleanToken = botToken.trim()
                val cleanChatId = chatId.trim()
                if (cleanToken.isEmpty() || cleanChatId.isEmpty()) {
                    return@withContext TelegramResult.Error("Bot token or Chat ID is empty.")
                }

                val body = FormBody.Builder()
                    .add("chat_id", cleanChatId)
                    .add("text", htmlText)
                    .add("parse_mode", "HTML")
                    .build()

                val request = Request.Builder()
                    .url("https://api.telegram.org/bot$cleanToken/sendMessage")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string().orEmpty()
                    if (response.isSuccessful) {
                        TelegramResult.Success("Delivered to $cleanChatId")
                    } else {
                        val errorDescription = try {
                            val json = JSONObject(responseStr)
                            json.optString("description", "HTTP ${response.code}")
                        } catch (e: Exception) {
                            "HTTP ${response.code}: $responseStr"
                        }
                        TelegramResult.Error(errorDescription)
                    }
                }
            } catch (e: Exception) {
                TelegramResult.Error(e.localizedMessage ?: "Network connection failed")
            }
        }
    }

    suspend fun testConnection(botToken: String, chatId: String): TelegramResult {
        return withContext(Dispatchers.IO) {
            try {
                val cleanToken = botToken.trim()
                if (cleanToken.isEmpty()) {
                    return@withContext TelegramResult.Error("Bot token is empty.")
                }

                val request = Request.Builder()
                    .url("https://api.telegram.org/bot$cleanToken/getMe")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string().orEmpty()
                    if (response.isSuccessful) {
                        val botName = try {
                            val json = JSONObject(responseStr)
                            val result = json.getJSONObject("result")
                            result.optString("first_name", "Bot") + " (@" + result.optString("username", "") + ")"
                        } catch (e: Exception) {
                            "Bot Connected"
                        }
                        TelegramResult.Success("Bot verified: $botName")
                    } else {
                        val errorDescription = try {
                            val json = JSONObject(responseStr)
                            json.optString("description", "HTTP ${response.code}")
                        } catch (e: Exception) {
                            "HTTP ${response.code}"
                        }
                        TelegramResult.Error(errorDescription)
                    }
                }
            } catch (e: Exception) {
                TelegramResult.Error(e.localizedMessage ?: "Failed to reach Telegram API")
            }
        }
    }
}
