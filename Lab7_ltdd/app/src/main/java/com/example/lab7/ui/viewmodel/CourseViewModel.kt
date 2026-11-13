package com.example.lab7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab7.data.model.Course
import com.example.lab7.data.repository.CourseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Toàn bộ trạng thái mà màn hình cần hiển thị. */
data class CourseUiState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val loadError: String? = null,
    // Form nhập liệu (dùng chung cho thêm và sửa)
    val name: String = "",
    val duration: String = "",
    val description: String = "",
    val nameError: String? = null,
    // Khóa học đang sửa / đang xem / chờ xác nhận xóa
    val editingCourse: Course? = null,
    val viewingCourse: Course? = null,
    val courseToDelete: Course? = null
)

/** Thông báo một lần (Snackbar): thành công hoặc lỗi. */
data class UiMessage(val text: String, val isError: Boolean)

class CourseViewModel(
    private val repository: CourseRepository = CourseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    init {
        loadCourses()
    }

    // ---------- Đọc danh sách ----------

    private var loadJob: Job? = null

    fun loadCourses() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            repository.getCourses()
                .onSuccess { list ->
                    _uiState.update { it.copy(courses = list, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, loadError = e.message) }
                }
        }
    }

    // ---------- Form ----------

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, nameError = null) }

    fun onDurationChange(value: String) = _uiState.update { it.copy(duration = value) }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }

    /** Nút chính của form: thêm mới hoặc lưu thay đổi tùy theo đang sửa hay không. */
    fun submit() {
        val state = _uiState.value
        if (state.isSaving) return

        val name = state.name.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(nameError = "Tên khóa học không được để trống") }
            return
        }
        val duration = state.duration.trim()
        val description = state.description.trim()
        val editing = state.editingCourse

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = if (editing == null) {
                repository.addCourse(name, duration, description)
            } else {
                repository.updateCourse(
                    editing.copy(
                        courseName = name,
                        courseDuration = duration,
                        courseDescription = description
                    )
                )
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    clearForm()
                    _messages.send(
                        UiMessage(
                            if (editing == null) "Thêm khóa học thành công"
                            else "Cập nhật khóa học thành công",
                            isError = false
                        )
                    )
                    reloadAfterChange()
                }
                .onFailure { e ->
                    // Giữ nguyên nội dung form để người dùng thử lại.
                    _uiState.update { it.copy(isSaving = false) }
                    _messages.send(UiMessage(e.message ?: "Thao tác thất bại", isError = true))
                }
        }
    }

    // ---------- Sửa ----------

    fun startEdit(course: Course) {
        _uiState.update {
            it.copy(
                editingCourse = course,
                name = course.courseName,
                duration = course.courseDuration,
                description = course.courseDescription,
                nameError = null,
                viewingCourse = null
            )
        }
    }

    /** Hủy sửa: không ghi gì lên Firestore, chỉ xóa form. */
    fun cancelEdit() = clearForm()

    private fun clearForm() {
        _uiState.update {
            it.copy(
                editingCourse = null,
                name = "",
                duration = "",
                description = "",
                nameError = null
            )
        }
    }

    // ---------- Xem chi tiết ----------

    fun showDetail(course: Course) = _uiState.update { it.copy(viewingCourse = course) }

    fun dismissDetail() = _uiState.update { it.copy(viewingCourse = null) }

    // ---------- Xóa ----------

    fun requestDelete(course: Course) = _uiState.update { it.copy(courseToDelete = course) }

    fun cancelDelete() {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(courseToDelete = null) }
    }

    fun confirmDelete() {
        val course = _uiState.value.courseToDelete ?: return
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.deleteCourse(course.courseID)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, courseToDelete = null) }
                    // Nếu đang sửa chính khóa học vừa xóa thì đóng form sửa.
                    if (_uiState.value.editingCourse?.courseID == course.courseID) clearForm()
                    _messages.send(UiMessage("Đã xóa khóa học \"${course.courseName}\"", false))
                    reloadAfterChange()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, courseToDelete = null) }
                    _messages.send(UiMessage(e.message ?: "Xóa thất bại", isError = true))
                }
        }
    }

    /** Tải lại danh sách sau khi thêm / sửa / xóa thành công. */
    private fun reloadAfterChange() = loadCourses()
}
