package com.example.lab7.data.repository

import com.example.lab7.data.model.Course
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

/**
 * Thực hiện CRUD trên collection "Courses" của Cloud Firestore.
 * Mọi hàm trả về [Result] để ViewModel xử lý thành công / lỗi rõ ràng.
 */
class CourseRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection(COLLECTION_NAME)

    /** Đọc toàn bộ khóa học trực tiếp từ server, sắp xếp theo tên. */
    suspend fun getCourses(): Result<List<Course>> = safeCall {
        val snapshot = collection.get(Source.SERVER).await()
        snapshot.documents
            .map { it.toCourse() }
            .sortedBy { it.courseName.lowercase() }
    }

    /** Thêm document mới; document ID được dùng làm courseID. */
    suspend fun addCourse(name: String, duration: String, description: String): Result<Unit> =
        safeCall {
            val ref = collection.document()
            val data = mapOf(
                FIELD_ID to ref.id,
                FIELD_NAME to name,
                FIELD_DURATION to duration,
                FIELD_DESCRIPTION to description
            )
            ref.set(data).await()
            Unit
        }

    /** Cập nhật đúng document theo ID, chỉ ghi 3 trường nội dung (không đổi ID). */
    suspend fun updateCourse(course: Course): Result<Unit> = safeCall {
        require(course.courseID.isNotBlank()) { "Thiếu mã khóa học" }
        val data = mapOf(
            FIELD_NAME to course.courseName,
            FIELD_DURATION to course.courseDuration,
            FIELD_DESCRIPTION to course.courseDescription
        )
        // update() báo lỗi NOT_FOUND nếu document không tồn tại (tránh tạo nhầm document mới).
        collection.document(course.courseID).update(data).await()
        Unit
    }

    /** Xóa đúng document theo ID. */
    suspend fun deleteCourse(courseID: String): Result<Unit> = safeCall {
        require(courseID.isNotBlank()) { "Thiếu mã khóa học" }
        collection.document(courseID).delete().await()
        Unit
    }

    /** Chuyển document sang Course, an toàn khi thiếu trường hoặc sai kiểu dữ liệu. */
    private fun DocumentSnapshot.toCourse(): Course = Course(
        courseID = id,
        courseName = get(FIELD_NAME)?.toString().orEmpty(),
        courseDuration = get(FIELD_DURATION)?.toString().orEmpty(),
        courseDescription = get(FIELD_DESCRIPTION)?.toString().orEmpty()
    )

    /** Chạy thao tác Firestore có giới hạn thời gian và đổi lỗi sang thông báo tiếng Việt. */
    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
        Result.success(withTimeout(TIMEOUT_MS) { block() })
    } catch (e: TimeoutCancellationException) {
        Result.failure(Exception("Quá thời gian chờ. Kiểm tra kết nối mạng và thử lại."))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception(e.toFriendlyMessage()))
    }

    private fun Exception.toFriendlyMessage(): String {
        if (this is FirebaseFirestoreException) {
            return when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "Không có quyền truy cập Firestore. Hãy kiểm tra Firestore Rules."
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "Không thể kết nối tới Firestore. Kiểm tra kết nối mạng."
                FirebaseFirestoreException.Code.NOT_FOUND ->
                    "Không tìm thấy dữ liệu (có thể khóa học đã bị xóa) hoặc chưa tạo database Firestore."
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                    "Quá thời gian chờ phản hồi từ Firestore."
                FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                    "Chưa xác thực với Firebase."
                else -> "Lỗi Firestore (${code.name}): ${message.orEmpty()}"
            }
        }
        return message ?: "Đã xảy ra lỗi không xác định."
    }

    private companion object {
        const val COLLECTION_NAME = "Courses"
        const val FIELD_ID = "courseID"
        const val FIELD_NAME = "courseName"
        const val FIELD_DURATION = "courseDuration"
        const val FIELD_DESCRIPTION = "courseDescription"
        const val TIMEOUT_MS = 15_000L
    }
}
