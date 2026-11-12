package com.example.tiptime.ui

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiptime.R
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Stateful Composable - Quản lý State chính của màn hình:
 * - Khởi tạo `amountInput` với `remember` và `mutableStateOf`.
 * - Tính toán số tiền `amount` và tiền boa `tip`.
 * - Truyền dữ liệu và event xuống các Stateless Composable con (State Hoisting).
 */
@Composable
fun TipTimeScreen(modifier: Modifier = Modifier) {
    // 4.A: Khai báo State tại TipTimeScreen
    var amountInput by remember { mutableStateOf("") }

    // Chuyển dữ liệu String thành Double an toàn
    val amount = amountInput.toDoubleOrNull() ?: 0.0

    // Tự động tính tiền boa khi dữ liệu thay đổi
    val tip = calculateTip(amount = amount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // A. Header
        HeaderSection()

        // B. Khu vực nhập hóa đơn (Card màu trắng bo góc 20dp)
        BillInputCard(
            amountInput = amountInput,
            onAmountChange = { newAmount ->
                amountInput = newAmount
            }
        )

        // C. Khu vực hiển thị kết quả (Card lớn nổi bật nền gradient)
        TipResultCard(tipAmount = tip)

        // D. Phần giải thích công thức tính
        FormulaSection()

        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * HeaderSection: Phần đầu màn hình với Icon, Title và Subtitle.
 */
@Composable
private fun HeaderSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_calculator),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.header_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.header_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * BillInputCard: Khung Card chứa thông tin hóa đơn và trường nhập.
 */
@Composable
private fun BillInputCard(
    amountInput: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_receipt),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.bill_info_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4.C: Sử dụng Stateless Composable EditNumberField
            EditNumberField(
                value = amountInput,
                onValueChange = onAmountChange
            )
        }
    }
}

/**
 * 5. HÀM TÍNH TIỀN BOA:
 * - Tỷ lệ tiền boa mặc định là 15.0%.
 * - Định dạng tiền tệ theo Locale Việt Nam (VNĐ).
 * - Xử lý an toàn: dữ liệu rỗng, số âm hoặc không hợp lệ trả về "0 ₫".
 */
@VisibleForTesting
internal fun calculateTip(
    amount: Double,
    tipPercent: Double = 15.0
): String {
    if (amount <= 0.0 || amount.isNaN() || amount.isInfinite()) {
        return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(0.0)
    }
    val tip = tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(tip)
}

@Preview(showBackground = true)
@Composable
fun TipTimeScreenPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}
