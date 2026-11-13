package com.example.lab7.data.model

/**
 * Một khóa học. [courseID] chính là document ID của Firestore
 * (collection "Courses") nên ổn định và không đổi khi cập nhật.
 */
data class Course(
    val courseID: String = "",
    val courseName: String = "",
    val courseDuration: String = "",
    val courseDescription: String = ""
)
