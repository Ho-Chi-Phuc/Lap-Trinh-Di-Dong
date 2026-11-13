package com.example.lab8.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

/**
 * Nơi duy nhất gọi Firebase Authentication thật.
 * Kết quả được trả về qua callback (Result<Unit>): thành công hoặc lỗi.
 * Mật khẩu chỉ được truyền thẳng cho Firebase, KHÔNG lưu hay ghi log ở bất kỳ đâu.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /** Người dùng đang đăng nhập (null nếu chưa đăng nhập) - lấy trực tiếp từ Firebase. */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /** Lắng nghe thay đổi đăng nhập/đăng xuất; trả về hàm để hủy lắng nghe. */
    fun observeAuthState(onChanged: (FirebaseUser?) -> Unit): () -> Unit {
        val listener = FirebaseAuth.AuthStateListener { onChanged(it.currentUser) }
        auth.addAuthStateListener(listener)
        return { auth.removeAuthStateListener(listener) }
    }

    fun signIn(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onResult(task.toResult()) }
    }

    fun signUp(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onResult(task.toResult()) }
    }

    fun sendPasswordReset(email: String, onResult: (Result<Unit>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task -> onResult(task.toResult()) }
    }

    /** Gửi email xác minh (do Firebase gửi) cho người dùng đang đăng nhập. */
    fun sendEmailVerification(onResult: (Result<Unit>) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(Result.failure(IllegalStateException("Chưa đăng nhập")))
            return
        }
        user.sendEmailVerification().addOnCompleteListener { task -> onResult(task.toResult()) }
    }

    /** Tải lại thông tin người dùng từ server để cập nhật trạng thái emailVerified. */
    fun reloadUser(onResult: (Result<Unit>) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(Result.failure(IllegalStateException("Chưa đăng nhập")))
            return
        }
        user.reload().addOnCompleteListener { task -> onResult(task.toResult()) }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun com.google.android.gms.tasks.Task<*>.toResult(): Result<Unit> =
        if (isSuccessful) Result.success(Unit)
        else Result.failure(exception ?: Exception("Lỗi không xác định"))

    companion object {
        /**
         * Đổi lỗi Firebase thành thông báo tiếng Việt dễ hiểu.
         * Không hiển thị stack trace hay thông điệp kỹ thuật cho người dùng.
         */
        fun friendlyMessage(error: Throwable?): String = when (error) {
            is FirebaseNetworkException ->
                "Không có kết nối mạng. Vui lòng kiểm tra Internet rồi thử lại."
            is FirebaseTooManyRequestsException ->
                "Bạn thao tác quá nhiều lần. Vui lòng thử lại sau ít phút."
            is FirebaseAuthWeakPasswordException ->
                "Mật khẩu quá yếu. Vui lòng dùng mật khẩu mạnh hơn."
            is FirebaseAuthUserCollisionException ->
                "Email này đã được đăng ký. Hãy đăng nhập hoặc dùng email khác."
            is FirebaseAuthInvalidUserException ->
                when (error.errorCode) {
                    "ERROR_USER_DISABLED" -> "Tài khoản này đã bị vô hiệu hóa."
                    else -> "Tài khoản không tồn tại hoặc không còn hiệu lực."
                }
            is FirebaseAuthInvalidCredentialsException ->
                when (error.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Email không đúng định dạng."
                    else -> "Email hoặc mật khẩu không đúng."
                }
            else -> "Đã xảy ra lỗi. Vui lòng thử lại sau."
        }
    }
}
