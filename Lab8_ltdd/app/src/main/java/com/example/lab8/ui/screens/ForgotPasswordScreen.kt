package com.example.lab8.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab8.ui.components.AuthScreenContainer
import com.example.lab8.ui.components.EmailField
import com.example.lab8.ui.components.LoadingButton
import com.example.lab8.ui.components.MessageBox
import com.example.lab8.ui.viewmodel.AuthUiState

@Composable
fun ForgotPasswordScreen(
    state: AuthUiState,
    onSendReset: (email: String) -> Unit,
    onBackToSignIn: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }

    AuthScreenContainer(title = "Quên mật khẩu") {
        Text("Nhập email đã đăng ký, Firebase sẽ gửi liên kết đặt lại mật khẩu cho bạn.")
        Spacer(modifier = Modifier.height(16.dp))

        EmailField(value = email, onValueChange = { email = it }, enabled = !state.isLoading)
        Spacer(modifier = Modifier.height(12.dp))

        MessageBox(message = state.message)
        if (state.message != null) Spacer(modifier = Modifier.height(12.dp))

        LoadingButton(
            text = "Gửi email đặt lại mật khẩu",
            isLoading = state.isLoading,
            onClick = { onSendReset(email) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onBackToSignIn, enabled = !state.isLoading) {
            Text("Quay lại đăng nhập")
        }
    }
}
