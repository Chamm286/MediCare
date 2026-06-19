package com.example.ncs3.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ncs3.data.repository.MedicareRepository
import com.example.ncs3.data.models.PatientInfo
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface

// ĐÃ XÓA DẤU NGOẶC ĐƠN THỪA Ở ĐÂY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPatientsScreen(
    navController: NavController,
    doctorId: String
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var patients by remember { mutableStateOf<List<PatientInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var selectedPatient by remember { mutableStateOf<PatientInfo?>(null) }
    var showPatientDetail by remember { mutableStateOf(false) }

    val filters = listOf("Tất cả", "Khám nhiều nhất", "Mới nhất", "Có bệnh nền")

    LaunchedEffect(doctorId) {
        scope.launch {
            val appointments = repository.getAppointmentsByDoctor(doctorId)
            val patientIds = appointments.map { it.patientId }.distinct()

            val patientList = mutableListOf<PatientInfo>()
            for (patientId in patientIds) {
                val user = repository.getUser(patientId)
                if (user != null) {
                    val patientAppointments = appointments.filter { it.patientId == patientId }
                    patientList.add(
                        PatientInfo(
                            id = patientId,
                            name = user.fullName.ifEmpty { "Bệnh nhân" },
                            email = user.email,
                            phone = user.phone.ifEmpty { "Chưa cập nhật" },
                            avatar = if (user.avatar.isNotEmpty()) user.avatar else "👤",
                            totalVisits = patientAppointments.size,
                            lastVisit = patientAppointments.maxOfOrNull { it.date } ?: "Chưa có",
                            medicalHistory = user.medicalHistory
                        )
                    )
                }
            }
            patients = patientList
            isLoading = false
        }
    }

    val filteredPatients = when (selectedFilter) {
        "Khám nhiều nhất" -> patients.sortedByDescending { it.totalVisits }
        "Mới nhất" -> patients.sortedByDescending { it.lastVisit }
        else -> patients
    }.filter { patient ->
        searchQuery.isEmpty() ||
                patient.name.contains(searchQuery, ignoreCase = true) ||
                patient.email.contains(searchQuery, ignoreCase = true) ||
                patient.phone.contains(searchQuery, ignoreCase = true)
    }

    if (showPatientDetail && selectedPatient != null) {
        PatientDetailDialog(
            patient = selectedPatient!!,
            onDismiss = { showPatientDetail = false },
            onNavigateToHistory = {
                showPatientDetail = false
                navController.navigate("patient_history/${selectedPatient?.id}")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bệnh nhân của tôi", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${patients.size} bệnh nhân", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Search, null, tint = Color.White)
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
            // Search Bar
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
                        textStyle = TextStyle(fontSize = 14.sp),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Tìm theo tên, email, số điện thoại...", color = Color(0xFF9AA0A6), fontSize = 13.sp)
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

            // Filter Chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0D47A1),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Stats Summary
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PatientStatItem(
                        icon = "👥",
                        value = patients.size.toString(),
                        label = "Tổng bệnh nhân"
                    )
                    PatientStatItem(
                        icon = "📊",
                        value = patients.sumOf { it.totalVisits }.toString(),
                        label = "Lượt khám"
                    )
                    PatientStatItem(
                        icon = "⭐",
                        value = if (patients.isNotEmpty()) String.format("%.1f", patients.map { it.totalVisits }.average()) else "0",
                        label = "Trung bình/BN"
                    )
                }
            }

            // Patients List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0D47A1))
                }
            } else if (filteredPatients.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.People, null, modifier = Modifier.size(80.dp), tint = Color(0xFFBDBDBD))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Không có bệnh nhân nào", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text("Bạn chưa có lịch hẹn nào với bệnh nhân", fontSize = 13.sp, color = Color.Gray)
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
                            onClick = {
                                selectedPatient = patient
                                showPatientDetail = true
                            },
                            onMessage = {
                                navController.navigate("chat/${patient.id}")
                            },
                            onViewHistory = {
                                navController.navigate("patient_history/${patient.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientStatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun PatientCard(
    patient: PatientInfo,
    onClick: () -> Unit,
    onMessage: () -> Unit,
    onViewHistory: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Text(if (patient.avatar.isNotEmpty()) patient.avatar else "👤", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        patient.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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
                        Icon(Icons.Outlined.CalendarMonth, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(" ${patient.totalVisits} lần khám", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Outlined.Schedule, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(" Lần cuối: ${patient.lastVisit}", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (patient.totalVisits > 5) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFFF9800).copy(alpha = 0.1f)
                ) {
                    Text(
                        if (patient.totalVisits > 5) "Thân thiết" else "Mới",
                        fontSize = 10.sp,
                        color = if (patient.totalVisits > 5) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onMessage,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Outlined.Chat, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nhắn tin", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onViewHistory,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Outlined.History, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lịch sử", fontSize = 12.sp)
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Outlined.Info, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chi tiết", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PatientDetailDialog(
    patient: PatientInfo,
    onDismiss: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (patient.avatar.isNotEmpty()) patient.avatar else "👤", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(patient.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(patient.email, fontSize = 12.sp, color = Color(0xFF0D47A1))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                InfoDetailRow(
                    icon = "📞",
                    title = "Số điện thoại",
                    value = patient.phone.ifEmpty { "Chưa cập nhật" }
                )
                InfoDetailRow(
                    icon = "📊",
                    title = "Tổng số lần khám",
                    value = "${patient.totalVisits} lần"
                )
                InfoDetailRow(
                    icon = "📅",
                    title = "Lần khám gần nhất",
                    value = patient.lastVisit
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    "📋 TIỀN SỬ BỆNH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (patient.medicalHistory.isEmpty()) {
                    Text("Chưa có thông tin", fontSize = 12.sp, color = Color.Gray)
                } else {
                    patient.medicalHistory.forEach { history ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                "• $history",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFF5F6368)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onNavigateToHistory()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xem lịch sử khám", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = Color.Gray)
            }
        }
    )
}

@Composable
fun InfoDetailRow(icon: String, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
        }
    }
}