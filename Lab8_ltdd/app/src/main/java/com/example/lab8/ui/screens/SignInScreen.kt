package com.example.lab8.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab8.ui.components.AuthScreenContainer
import com.example.lab8.ui.components.EmailField
import com.example.lab8.ui.components.LoadingButton
import com.example.lab8.ui.components.MessageBox
import com.example.lab8.ui.components.PasswordField
import com.example.lab8.ui.viewmodel.AuthUiState

@Composable
fun SignInScreen(
    state: AuthUiState,
    onSignIn: (email: String, password: String) -> Unit,
    onGoToSignUp: () -> Unit,
    onGoToForgotPassword: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    // Mật khẩu chỉ giữ trong bộ nhớ (remember), không lưu vào saved state.
    var password by remember { mutableStateOf("") }

    AuthScreenContainer(title = "Đăng nhập") {
        EmailField(value = email, onValueChange = { email = it }, enabled = !state.isLoading)
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Mật khẩu",
            enabled = !state.isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))

        MessageBox(message = state.message)
        if (state.message != null) Spacer(modifier = Modifier.height(12.dp))

        LoadingButton(
            text = "Đăng nhập",
            isLoading = state.isLoading,
            onClick = { onSignIn(email, password) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onGoToForgotPassword, enabled = !state.isLoading) {
            Text("Quên mật khẩu?")
        }
        TextButton(onClick = onGoToSignUp, enabled = !state.isLoading) {
            Text("Chưa có tài khoản? Đăng ký")
        }
    }
}
