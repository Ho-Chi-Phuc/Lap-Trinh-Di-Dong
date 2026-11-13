package com.example.lab8.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab8.ui.components.AuthScreenContainer
import com.example.lab8.ui.components.LoadingButton
import com.example.lab8.ui.components.MessageBox
import com.example.lab8.ui.viewmodel.AuthUiState

/**
 * Trang chủ: chỉ hiển thị khi đã đăng nhập.
 * Email lấy từ FirebaseAuth.currentUser (thông qua state của ViewModel).
 */
@Composable
fun HomeScreen(
    state: AuthUiState,
    onSignOut: () -> Unit,
    onRecheckVerification: () -> Unit,
    onResendVerification: () -> Unit
) {
    AuthScreenContainer(title = "Trang chủ") {
        Text(
            text = "Đăng nhập thành công!",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Email đang đăng nhập:")
        Text(
            text = state.userEmail ?: "(không có email)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (state.isEmailVerified) "Trạng thái email: Đã xác minh"
            else "Trạng thái email: Chưa xác minh"
        )
        Spacer(modifier = Modifier.height(16.dp))

        MessageBox(message = state.message)
        if (state.message != null) Spacer(modifier = Modifier.height(12.dp))

        if (!state.isEmailVerified) {
            // Hướng dẫn: mở email xác minh -> bấm liên kết -> quay lại bấm "Kiểm tra lại".
            OutlinedButton(
                onClick = onResendVerification,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Gửi lại email xác minh") }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onRecheckVerification,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Tôi đã xác minh, kiểm tra lại") }
            Spacer(modifier = Modifier.height(16.dp))
        }

        LoadingButton(
            text = "Đăng xuất",
            isLoading = state.isLoading,
            onClick = onSignOut
        )
    }
}
