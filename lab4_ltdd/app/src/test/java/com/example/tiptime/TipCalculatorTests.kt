package com.example.tiptime

import com.example.tiptime.ui.calculateTip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

/**
 * Kiểm thử đơn vị (Unit Test) cho hàm calculateTip().
 * Kiểm tra các trường hợp trong mục 10 của yêu cầu đề bài:
 * - Trường hợp 1: 100.000 VNĐ -> 15.000 ₫
 * - Trường hợp 2: 200.000 VNĐ -> 30.000 ₫
 * - Trường hợp 3: 500.000 VNĐ -> 75.000 ₫
 * - Trường hợp 4: Xóa toàn bộ nội dung / 0.0 -> 0 ₫
 * - Trường hợp 5: Dữ liệu âm / không hợp lệ -> 0 ₫
 */
class TipCalculatorTests {

    private val vietnameseLocale = Locale("vi", "VN")

    @Test
    fun calculateTip_100k_returns15k() {
        val amount = 100000.0
        val expected = NumberFormat.getCurrencyInstance(vietnameseLocale).format(15000.0)
        val actual = calculateTip(amount = amount, tipPercent = 15.0)
        assertEquals(expected, actual)
        assertTrue(actual.contains("15.000"))
    }

    @Test
    fun calculateTip_200k_returns30k() {
        val amount = 200000.0
        val expected = NumberFormat.getCurrencyInstance(vietnameseLocale).format(30000.0)
        val actual = calculateTip(amount = amount, tipPercent = 15.0)
        assertEquals(expected, actual)
        assertTrue(actual.contains("30.000"))
    }

    @Test
    fun calculateTip_500k_returns75k() {
        val amount = 500000.0
        val expected = NumberFormat.getCurrencyInstance(vietnameseLocale).format(75000.0)
        val actual = calculateTip(amount = amount, tipPercent = 15.0)
        assertEquals(expected, actual)
        assertTrue(actual.contains("75.000"))
    }

    @Test
    fun calculateTip_zeroOrEmpty_returnsZeroCurrency() {
        val amount = 0.0
        val expected = NumberFormat.getCurrencyInstance(vietnameseLocale).format(0.0)
        val actual = calculateTip(amount = amount, tipPercent = 15.0)
        assertEquals(expected, actual)
        assertTrue(actual.contains("0"))
    }

    @Test
    fun calculateTip_negativeAmount_returnsZeroCurrency() {
        val amount = -50000.0
        val expected = NumberFormat.getCurrencyInstance(vietnameseLocale).format(0.0)
        val actual = calculateTip(amount = amount, tipPercent = 15.0)
        assertEquals(expected, actual)
    }

    @Test
    fun calculateTip_customTipPercent_calculatesCorrectly() {
        val amount = 100000.0
        val tipPercent = 20.0
        val expected = NumberFormat.getCurrencyInstance(vietnameseLocale).format(20000.0)
        val actual = calculateTip(amount = amount, tipPercent = tipPercent)
        assertEquals(expected, actual)
    }
}

