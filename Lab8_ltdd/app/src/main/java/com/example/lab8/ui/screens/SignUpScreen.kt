package com.example.lab8.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
fun SignUpScreen(
    state: AuthUiState,
    onSignUp: (email: String, password: String, confirmPassword: String) -> Unit,
    onBackToSignIn: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AuthScreenContainer(title = "Đăng ký") {
        EmailField(value = email, onValueChange = { email = it }, enabled = !state.isLoading)
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Mật khẩu (ít nhất 6 ký tự)",
            enabled = !state.isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Nhập lại mật khẩu",
            enabled = !state.isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))

        MessageBox(message = state.message)
        if (state.message != null) Spacer(modifier = Modifier.height(12.dp))

        LoadingButton(
            text = "Đăng ký",
            isLoading = state.isLoading,
            onClick = { onSignUp(email, password, confirmPassword) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onBackToSignIn, enabled = !state.isLoading) {
            Text("Đã có tài khoản? Đăng nhập")
        }
    }
}
