package com.example.ncs3.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.User
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePatientsScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var patients by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Tất cả") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPatient by remember { mutableStateOf<User?>(null) }

    val roles = listOf("Tất cả", "patient", "doctor", "admin")

    val filteredPatients = patients.filter { patient ->
        (searchQuery.isEmpty() ||
                patient.fullName.contains(searchQuery, ignoreCase = true) ||
                patient.email.contains(searchQuery, ignoreCase = true) ||
                patient.phone.contains(searchQuery, ignoreCase = true)) &&
                (selectedRole == "Tất cả" || patient.role == selectedRole)
    }

    fun loadPatients() {
        scope.launch {
            // TODO: Thêm hàm getUsers() vào repository nếu cần
            // Hiện tại dùng empty list
            patients = emptyList()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadPatients()
    }

    if (showDeleteDialog && selectedPatient != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa tài khoản", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Bạn có chắc chắn muốn xóa tài khoản?")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        selectedPatient?.fullName ?: "",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Text("Hành động này không thể hoàn tác.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            selectedPatient?.let {
                                // TODO: Xóa user khỏi Firestore
                                loadPatients()
                            }
                            showDeleteDialog = false
                            selectedPatient = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Lọc theo vai trò", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    roles.forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRole = role
                                    showFilterDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRole == role,
                                onClick = {
                                    selectedRole = role
                                    showFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    when (role) {
                                        "patient" -> "Bệnh nhân"
                                        "doctor" -> "Bác sĩ"
                                        "admin" -> "Quản trị viên"
                                        else -> "Tất cả"
                                    },
                                    fontSize = 14.sp
                                )
                                if (role != "Tất cả") {
                                    Text(
                                        when (role) {
                                            "patient" -> "Người dùng đặt lịch khám"
                                            "doctor" -> "Bác sĩ khám bệnh"
                                            "admin" -> "Quản trị hệ thống"
                                            else -> ""
                                        },
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Đóng", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quản lý người dùng", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${patients.size} người dùng", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Outlined.FilterList, null, tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            // Search bar
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Search, null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Tìm kiếm theo tên, email, số điện thoại...", color = Color(0xFF9AA0A6), fontSize = 13.sp)
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Outlined.Close, null, modifier = Modifier.size(18.dp), tint = Color(0xFF9AA0A6))
                        }
                    }
                }
            }

            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PatientStatsCard(
                    title = "Bệnh nhân",
                    value = patients.count { it.role == "patient" }.toString(),
                    icon = "👤",
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                PatientStatsCard(
                    title = "Bác sĩ",
                    value = patients.count { it.role == "doctor" }.toString(),
                    icon = "👨‍⚕️",
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
                PatientStatsCard(
                    title = "Admin",
                    value = patients.count { it.role == "admin" }.toString(),
                    icon = "🔧",
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
            }

            // Patient list
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0D47A1))
                }
            } else if (filteredPatients.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.People, null, modifier = Modifier.size(80.dp), tint = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Không tìm thấy người dùng", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text("Thử tìm kiếm với từ khóa khác", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPatients) { patient ->
                        PatientCard(
                            patient = patient,
                            onEdit = {
                                navController.navigate("edit_patient/${patient.uid}")
                            },
                            onDelete = {
                                selectedPatient = patient
                                showDeleteDialog = true
                            },
                            onViewDetail = {
                                navController.navigate("patient_detail/${patient.uid}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientStatsCard(title: String, value: String, icon: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            }
            Text(icon, fontSize = 28.sp)
        }
    }
}

@Composable
fun PatientCard(
    patient: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetail: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFF00BCD4))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(patient.avatar.ifEmpty { "👤" }, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    patient.fullName.ifEmpty { "Chưa cập nhật" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    patient.email,
                    fontSize = 12.sp,
                    color = Color(0xFF0D47A1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Phone,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Text(
                        " ${patient.phone.ifEmpty { "Chưa cập nhật" }}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Text(
                        " ${patient.dob.ifEmpty { "Chưa cập nhật" }}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // Role badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (patient.role) {
                    "doctor" -> Color(0xFFE3F2FD).copy(alpha = 0.5f)
                    "admin" -> Color(0xFFFFF3E0).copy(alpha = 0.5f)
                    else -> Color(0xFFE8F5E9).copy(alpha = 0.5f)
                }
            ) {
                Text(
                    when (patient.role) {
                        "doctor" -> "Bác sĩ"
                        "admin" -> "Admin"
                        else -> "Bệnh nhân"
                    },
                    fontSize = 10.sp,
                    color = when (patient.role) {
                        "doctor" -> Color(0xFF0D47A1)
                        "admin" -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),  // Sửa: gộp modifier đúng cách
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f).height(40.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sửa", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f).height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE53935)
                )
            ) {
                Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Xóa", fontSize = 12.sp)
            }
        }
    }
}