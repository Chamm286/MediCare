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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.Doctor
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDoctorsScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedSpecialty by remember { mutableStateOf("Tất cả") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }

    val specialties = listOf("Tất cả") + doctors.map { it.specialty }.distinct()

    val filteredDoctors = doctors.filter { doctor ->
        (searchQuery.isEmpty() ||
                doctor.name.contains(searchQuery, ignoreCase = true) ||
                doctor.specialty.contains(searchQuery, ignoreCase = true)) &&
                (selectedSpecialty == "Tất cả" || doctor.specialty == selectedSpecialty)
    }

    val verifiedCount = doctors.count { it.isVerified }
    val pendingCount = doctors.count { !it.isVerified }

    fun loadDoctors() {
        scope.launch {
            doctors = repository.getDoctors()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadDoctors()
    }

    if (showDeleteDialog && selectedDoctor != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa bác sĩ", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Bạn có chắc chắn muốn xóa bác sĩ?")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        selectedDoctor?.name ?: "",
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
                            selectedDoctor?.let {
                                // repository.deleteDoctor(it.id)
                                loadDoctors()
                            }
                            showDeleteDialog = false
                            selectedDoctor = null
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
            title = { Text("Lọc theo chuyên khoa", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    specialties.forEach { specialty ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSpecialty = specialty
                                    showFilterDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSpecialty == specialty,
                                onClick = {
                                    selectedSpecialty = specialty
                                    showFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(specialty, fontSize = 14.sp)
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
                        Text("Quản lý bác sĩ", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${verifiedCount} đã duyệt · ${pendingCount} chờ duyệt", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
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
                                Text("Tìm kiếm bác sĩ...", color = Color(0xFF9AA0A6), fontSize = 14.sp)
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
                AdminStatsCard(
                    title = "Tổng số",
                    value = doctors.size.toString(),
                    icon = "👨‍⚕️",
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                AdminStatsCard(
                    title = "Đã duyệt",
                    value = verifiedCount.toString(),
                    icon = "✅",
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
                AdminStatsCard(
                    title = "Chờ duyệt",
                    value = pendingCount.toString(),
                    icon = "⏳",
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
            }

            // Doctor list
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0D47A1))
                }
            } else if (filteredDoctors.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.People, null, modifier = Modifier.size(80.dp), tint = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Không tìm thấy bác sĩ", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text("Thử tìm kiếm với từ khóa khác", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        AdminDoctorManageCard(
                            doctor = doctor,
                            onVerify = {
                                scope.launch {
                                    repository.verifyDoctor(doctor.id, !doctor.isVerified)
                                    loadDoctors()
                                }
                            },
                            onEdit = {
                                navController.navigate("edit_doctor/${doctor.id}")
                            },
                            onDelete = {
                                selectedDoctor = doctor
                                showDeleteDialog = true
                            },
                            onViewDetail = {
                                navController.navigate("doctor_detail/${doctor.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatsCard(title: String, value: String, icon: String, color: Color, modifier: Modifier = Modifier) {
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
fun AdminDoctorManageCard(
    doctor: Doctor,
    onVerify: () -> Unit,
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0D47A1), Color(0xFF00BCD4))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(doctor.avatar.ifEmpty { "👨‍⚕️" }, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(doctor.specialty, fontSize = 13.sp, color = Color(0xFF0D47A1))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(Icons.Outlined.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFB800))
                        Text(" ${doctor.rating}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Outlined.MedicalServices, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(" ${doctor.experience} năm", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(doctor.hospital, fontSize = 11.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // Status badge
                Surface(
                    shape = CircleShape,
                    color = if (doctor.isVerified) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFFF9800).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (doctor.isVerified) "Đã duyệt" else "Chờ duyệt",
                            fontSize = 10.sp,
                            color = if (doctor.isVerified) Color(0xFF4CAF50) else Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerify,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (doctor.isVerified) Color(0xFFE53935) else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        if (doctor.isVerified) Icons.Outlined.DisabledByDefault else Icons.Outlined.Verified,
                        null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (doctor.isVerified) "Hủy duyệt" else "Duyệt", fontSize = 12.sp)
                }

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
}