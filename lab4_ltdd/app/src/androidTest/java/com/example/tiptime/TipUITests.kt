package com.example.tiptime

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tiptime.ui.TipTimeScreen
import com.example.tiptime.ui.theme.TipTimeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.NumberFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class TipUITests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun calculate_20_percent_tip() {
        composeTestRule.setContent {
            TipTimeTheme {
                TipTimeScreen()
            }
        }

        // Nhập số tiền 100000
        composeTestRule.onNodeWithText("Số tiền hóa đơn")
            .performTextInput("100000")

        val expectedTip = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(15000.0)

        // Kiểm tra kết quả hiển thị trên màn hình
        composeTestRule.onNodeWithText(expectedTip).assertExists(
            "Không tìm thấy số tiền boa hiển thị $expectedTip"
        )
    }
}

