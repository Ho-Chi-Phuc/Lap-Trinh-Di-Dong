package com.example.lab7.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab7.data.model.Course
import com.example.lab7.ui.components.CourseItem
import com.example.lab7.ui.viewmodel.CourseUiState
import com.example.lab7.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(viewModel: CourseViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var lastMessageIsError by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Hiển thị thông báo thành công / lỗi từ ViewModel.
    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            lastMessageIsError = message.isError
            snackbarHostState.showSnackbar(message.text)
        }
    }

    // Khi bấm "Sửa", cuộn lên đầu để thấy form đã được điền sẵn.
    LaunchedEffect(state.editingCourse?.courseID) {
        if (state.editingCourse != null) listState.animateScrollToItem(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Quản lý khóa học") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = viewModel::loadCourses, enabled = !state.isLoading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Làm mới danh sách")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (lastMessageIsError) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.inverseSurface
                    },
                    contentColor = if (lastMessageIsError) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.inverseOnSurface
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
        ) {
            if (state.isLoading || state.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "form") {
                    CourseForm(
                        state = state,
                        onNameChange = viewModel::onNameChange,
                        onDurationChange = viewModel::onDurationChange,
                        onDescriptionChange = viewModel::onDescriptionChange,
                        onSubmit = viewModel::submit,
                        onCancelEdit = viewModel::cancelEdit
                    )
                }
                item(key = "header") {
                    Text(
                        text = "Danh sách khóa học (${state.courses.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (state.loadError != null) {
                    item(key = "error") {
                        ErrorCard(message = state.loadError.orEmpty(), onRetry = viewModel::loadCourses)
                    }
                }
                if (state.isLoading && state.courses.isEmpty()) {
                    item(key = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (!state.isLoading && state.loadError == null && state.courses.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = "Chưa có khóa học nào. Hãy thêm khóa học đầu tiên ở biểu mẫu phía trên.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    }
                }
                items(items = state.courses, key = { it.courseID }) { course ->
                    CourseItem(
                        course = course,
                        enabled = !state.isSaving,
                        onView = { viewModel.showDetail(course) },
                        onEdit = { viewModel.startEdit(course) },
                        onDelete = { viewModel.requestDelete(course) }
                    )
                }
            }
        }
    }

    state.viewingCourse?.let { course ->
        CourseDetailDialog(
            course = course,
            onDismiss = viewModel::dismissDetail,
            onEdit = { viewModel.startEdit(course) }
        )
    }

    state.courseToDelete?.let { course ->
        DeleteConfirmDialog(
            course = course,
            isDeleting = state.isSaving,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::cancelDelete
        )
    }
}

/** Form dùng chung cho Thêm và Sửa (chế độ sửa khi [CourseUiState.editingCourse] != null). */
@Composable
private fun CourseForm(
    state: CourseUiState,
    onNameChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancelEdit: () -> Unit
) {
    val isEditing = state.editingCourse != null
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEditing) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isEditing) "Sửa khóa học" else "Thêm khóa học mới",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Tên khóa học *") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { error -> { Text(error) } },
                singleLine = true,
                enabled = !state.isSaving,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.duration,
                onValueChange = onDurationChange,
                label = { Text("Thời lượng (ví dụ: 3 tháng)") },
                singleLine = true,
                enabled = !state.isSaving,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Mô tả khóa học") },
                minLines = 3,
                maxLines = 5,
                enabled = !state.isSaving,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSubmit,
                    enabled = !state.isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = if (isEditing) Icons.Filled.Check else Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = if (isEditing) "Lưu thay đổi" else "Thêm khóa học",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (isEditing) {
                    OutlinedButton(onClick = onCancelEdit, enabled = !state.isSaving) {
                        Text("Hủy")
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Không tải được dữ liệu", style = MaterialTheme.typography.titleSmall)
            Text(message, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onRetry) { Text("Thử lại") }
        }
    }
}

@Composable
private fun CourseDetailDialog(course: Course, onDismiss: () -> Unit, onEdit: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(course.courseName.ifBlank { "(Chưa có tên)" }) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow("Mã khóa học", course.courseID)
                DetailRow("Thời lượng", course.courseDuration.ifBlank { "—" })
                DetailRow("Mô tả", course.courseDescription.ifBlank { "—" })
            }
        },
        confirmButton = { TextButton(onClick = onEdit) { Text("Sửa") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DeleteConfirmDialog(
    course: Course,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận xóa") },
        text = { Text("Bạn có chắc muốn xóa khóa học \"${course.courseName}\"? Thao tác này không thể hoàn tác.") },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isDeleting) {
                Text("Xóa", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) { Text("Hủy") }
        }
    )
}
