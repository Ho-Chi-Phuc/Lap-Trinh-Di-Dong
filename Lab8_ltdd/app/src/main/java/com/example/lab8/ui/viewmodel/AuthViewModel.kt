package com.example.lab8.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lab8.data.repository.AuthRepository
import com.example.lab8.data.repository.AuthValidator
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Thông báo hiển thị trên màn hình: isError = true thì tô màu đỏ, ngược lại là thông tin/thành công. */
data class UiMessage(val text: String, val isError: Boolean)

/** Toàn bộ trạng thái giao diện xác thực - các màn hình chỉ việc đọc và hiển thị. */
data class AuthUiState(
    val isLoading: Boolean = false,
    val message: UiMessage? = null,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null,
    val isEmailVerified: Boolean = false
)

/**
 * ViewModel dùng chung cho các màn hình Đăng nhập / Đăng ký / Quên mật khẩu / Trang chủ.
 * Các hàm nhận callback (onSuccess) để màn hình biết khi nào được phép điều hướng.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isLoggedIn = repository.currentUser != null,
            userEmail = repository.currentUser?.email,
            isEmailVerified = repository.currentUser?.isEmailVerified == true
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** Có phiên đăng nhập Firebase hay không (dùng để chọn màn hình đầu tiên). */
    val isLoggedIn: Boolean
        get() = repository.currentUser != null

    private val stopObservingAuth = repository.observeAuthState { user -> applyUser(user) }

    override fun onCleared() {
        stopObservingAuth()
        super.onCleared()
    }

    private fun applyUser(user: FirebaseUser?) {
        _uiState.update {
            it.copy(
                isLoggedIn = user != null,
                userEmail = user?.email,
                isEmailVerified = user?.isEmailVerified == true
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun showError(text: String) {
        _uiState.update { it.copy(isLoading = false, message = UiMessage(text, isError = true)) }
    }

    private fun showInfo(text: String, stopLoading: Boolean = true) {
        _uiState.update {
            it.copy(
                isLoading = if (stopLoading) false else it.isLoading,
                message = UiMessage(text, isError = false)
            )
        }
    }

    private fun startLoading() {
        _uiState.update { it.copy(isLoading = true, message = null) }
    }

    // ---------------------------------------------------------------- Đăng nhập
    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return // chặn bấm liên tục
        val cleanEmail = email.trim()

        val error = AuthValidator.validateEmail(cleanEmail)
            ?: AuthValidator.validateSignInPassword(password)
        if (error != null) {
            showError(error)
            return
        }

        startLoading()
        repository.signIn(cleanEmail, password) { result ->
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, message = null) }
                onSuccess()
            }.onFailure { showError(AuthRepository.friendlyMessage(it)) }
        }
    }

    // ----------------------------------------------------------------- Đăng ký
    /**
     * Sau khi Firebase tạo tài khoản thành công:
     * 1. Gửi email xác minh (Firebase gửi, không phải SMTP tự viết).
     * 2. Đăng xuất, rồi chuyển về màn hình Đăng nhập kèm thông báo thành công.
     * Cách này luôn nhất quán: đăng ký xong -> đăng nhập lại để vào Trang chủ.
     */
    fun signUp(email: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return
        val cleanEmail = email.trim()

        val error = AuthValidator.validateEmail(cleanEmail)
            ?: AuthValidator.validateNewPassword(password, confirmPassword)
        if (error != null) {
            showError(error)
            return
        }

        startLoading()
        repository.signUp(cleanEmail, password) { result ->
            result.onFailure { showError(AuthRepository.friendlyMessage(it)) }
            result.onSuccess {
                // Tài khoản đã được tạo thật trên Firebase -> thử gửi email xác minh.
                repository.sendEmailVerification { verifyResult ->
                    repository.signOut()
                    val text = if (verifyResult.isSuccess) {
                        "Đăng ký thành công! Đã gửi email xác minh tới $cleanEmail. " +
                            "Hãy mở email để xác minh, sau đó đăng nhập."
                    } else {
                        "Đăng ký thành công! Chưa gửi được email xác minh, " +
                            "bạn có thể gửi lại trong Trang chủ sau khi đăng nhập."
                    }
                    showInfo(text)
                    onSuccess()
                }
            }
        }
    }

    // ------------------------------------------------------------ Quên mật khẩu
    fun sendPasswordReset(email: String) {
        if (_uiState.value.isLoading) return
        val cleanEmail = email.trim()

        val error = AuthValidator.validateEmail(cleanEmail)
        if (error != null) {
            showError(error)
            return
        }

        startLoading()
        repository.sendPasswordReset(cleanEmail) { result ->
            result.onSuccess {
                showInfo(
                    "Đã gửi yêu cầu đặt lại mật khẩu tới $cleanEmail. " +
                        "Hãy kiểm tra hộp thư (cả mục Spam) và làm theo hướng dẫn."
                )
            }.onFailure { showError(AuthRepository.friendlyMessage(it)) }
        }
    }

    // ---------------------------------------------------------------- Trang chủ
    /** Tải lại người dùng từ server để lấy trạng thái emailVerified mới nhất. */
    fun refreshUser(showResult: Boolean = false) {
        if (_uiState.value.isLoading) return
        if (repository.currentUser == null) return
        if (showResult) startLoading()

        repository.reloadUser { result ->
            result.onSuccess {
                applyUser(repository.currentUser)
                if (showResult) {
                    val verified = repository.currentUser?.isEmailVerified == true
                    showInfo(
                        if (verified) "Email của bạn đã được xác minh."
                        else "Email chưa được xác minh. Hãy mở email xác minh rồi kiểm tra lại."
                    )
                }
            }.onFailure { error ->
                if (error is FirebaseAuthInvalidUserException) {
                    // Tài khoản đã bị xóa/vô hiệu hóa trên server -> buộc đăng xuất.
                    repository.signOut()
                } else if (showResult) {
                    showError(AuthRepository.friendlyMessage(error))
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun resendVerificationEmail() {
        if (_uiState.value.isLoading) return
        startLoading()
        repository.sendEmailVerification { result ->
            result.onSuccess {
                showInfo("Đã gửi lại email xác minh. Hãy kiểm tra hộp thư của bạn.")
            }.onFailure { showError(AuthRepository.friendlyMessage(it)) }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.update { AuthUiState() }
    }
}
