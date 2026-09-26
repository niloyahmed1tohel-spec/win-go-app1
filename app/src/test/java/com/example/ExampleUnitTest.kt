package com.example

import com.example.data.model.WinGoLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWinGoColorLogic() {
        // From python script:
        // 0 -> RED 🔴 + VIOLET 🟣
        // 5 -> GREEN 🟢 + VIOLET 🟣
        // num % 2 == 0 -> RED 🔴
        // else -> GREEN 🟢
        assertEquals("RED 🔴 + VIOLET 🟣", WinGoLogic.getColor(0))
        assertEquals("GREEN 🟢 + VIOLET 🟣", WinGoLogic.getColor(5))
        assertEquals("RED 🔴", WinGoLogic.getColor(2))
        assertEquals("RED 🔴", WinGoLogic.getColor(4))
        assertEquals("RED 🔴", WinGoLogic.getColor(6))
        assertEquals("RED 🔴", WinGoLogic.getColor(8))
        assertEquals("GREEN 🟢", WinGoLogic.getColor(1))
        assertEquals("GREEN 🟢", WinGoLogic.getColor(3))
        assertEquals("GREEN 🟢", WinGoLogic.getColor(7))
        assertEquals("GREEN 🟢", WinGoLogic.getColor(9))
    }

    @Test
    fun testEvaluationDirectNumberWin() {
        val (status, msg, isWin) = WinGoLogic.evaluateNumberResult(
            period = 100,
            currentLuckyNumber = 7,
            currentPrediction = "BIG 🔼",
            selectedNum = 7
        )
        assertTrue(isWin)
        assertTrue(status.contains("LUCKY NUMBER MATCH WIN"))
        assertTrue(msg.contains("#100"))
        assertTrue(msg.contains("PERIOD"))
        assertTrue(msg.contains("#7"))
        assertTrue(msg.contains("RESULT NUMBER"))
    }

    @Test
    fun testEvaluationBigSmallWin() {
        val (status, _, isWin) = WinGoLogic.evaluateNumberResult(
            period = 101,
            currentLuckyNumber = 7,
            currentPrediction = "BIG 🔼",
            selectedNum = 8
        )
        assertTrue(isWin)
        assertTrue(status.contains("BIG/SMALL WIN"))
    }

    @Test
    fun testEvaluationLoss() {
        val (status, _, isWin) = WinGoLogic.evaluateNumberResult(
            period = 102,
            currentLuckyNumber = 7,
            currentPrediction = "BIG 🔼",
            selectedNum = 2
        )
        assertTrue(!isWin)
        assertTrue(status.contains("LOSS ❌"))
    }
}
