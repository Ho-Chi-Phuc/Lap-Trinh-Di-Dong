package com.example.lab8.data.repository

/**
 * Kiểm tra dữ liệu người dùng nhập TRƯỚC khi gọi Firebase.
 * Mỗi hàm trả về thông báo lỗi tiếng Việt, hoặc null nếu dữ liệu hợp lệ.
 * Viết bằng Kotlin thuần (không dùng Android API) nên có thể unit test trực tiếp.
 */
object AuthValidator {

    /** Firebase Authentication yêu cầu mật khẩu tối thiểu 6 ký tự. */
    const val MIN_PASSWORD_LENGTH = 6

    // Định dạng email cơ bản: có phần tên, dấu @, tên miền và đuôi tên miền.
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Vui lòng nhập email."
        !EMAIL_REGEX.matches(email.trim()) -> "Email không đúng định dạng."
        else -> null
    }

    fun validateSignInPassword(password: String): String? =
        if (password.isEmpty()) "Vui lòng nhập mật khẩu." else null

    fun validateNewPassword(password: String, confirmPassword: String): String? = when {
        password.isEmpty() -> "Vui lòng nhập mật khẩu."
        password.length < MIN_PASSWORD_LENGTH ->
            "Mật khẩu phải có ít nhất $MIN_PASSWORD_LENGTH ký tự."
        confirmPassword.isEmpty() -> "Vui lòng nhập lại mật khẩu."
        password != confirmPassword -> "Mật khẩu nhập lại không trùng khớp."
        else -> null
    }
}
