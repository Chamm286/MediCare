package com.example.ncs3.ui.screens.doctor

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.Appointment
import com.example.ncs3.data.models.User
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHistoryScreen(
    navController: NavController,
    patientId: String,
    doctorId: String
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var patient by remember { mutableStateOf<User?>(null) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(patientId) {
        scope.launch {
            patient = repository.getUser(patientId)
            val allAppointments = repository.getAppointmentsByPatient(patientId)
            appointments = allAppointments.sortedByDescending { parseDate(it.date) }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Lịch sử khám bệnh", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(patient?.fullName ?: "Bệnh nhân", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("chat/$patientId") }) {
                        Icon(Icons.Outlined.Chat, null, tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0D47A1))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F7FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Info Card
                item {
                    PatientInfoCard(patient = patient)
                }

                // Stats
                item {
                    PatientStatsCard(appointments = appointments)
                }

                // Tabs
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Color(0xFF0D47A1)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("📋 Lịch sử khám", fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("📝 Đơn thuốc", fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("📊 Thống kê", fontSize = 13.sp) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> {
                        if (appointments.isEmpty()) {
                            item {
                                EmptyHistoryCard()
                            }
                        } else {
                            items(appointments) { appointment ->
                                HistoryAppointmentCard(
                                    appointment = appointment,
                                    onClick = {
                                        navController.navigate("appointment_detail/${appointment.id}")
                                    }
                                )
                            }
                        }
                    }
                    1 -> {
                        val prescriptions = appointments.filter { it.status == "completed" }
                        if (prescriptions.isEmpty()) {
                            item {
                                EmptyPrescriptionCard()
                            }
                        } else {
                            items(prescriptions) { appointment ->
                                PrescriptionHistoryCard(
                                    appointment = appointment,
                                    onClick = {
                                        navController.navigate("prescription/${appointment.id}")
                                    }
                                )
                            }
                        }
                    }
                    2 -> {
                        item {
                            PatientStatisticsChart(appointments = appointments)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientInfoCard(patient: User?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(patient?.avatar?.ifEmpty { "👤" } ?: "👤", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(patient?.fullName ?: "Bệnh nhân", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(patient?.email ?: "", fontSize = 12.sp, color = Color(0xFF0D47A1))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Outlined.Phone, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(" ${patient?.phone ?: "Chưa cập nhật"}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun PatientStatsCard(appointments: List<Appointment>) {
    val totalVisits = appointments.size
    val completedVisits = appointments.count { it.status == "completed" }
    val cancelledVisits = appointments.count { it.status == "cancelled" }
    val pendingVisits = appointments.count { it.status == "pending" }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📊 Thống kê nhanh", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBadge(
                    value = totalVisits.toString(),
                    label = "Tổng lượt",
                    color = Color(0xFF0D47A1),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    value = completedVisits.toString(),
                    label = "Hoàn thành",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    value = pendingVisits.toString(),
                    label = "Chờ",
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    value = cancelledVisits.toString(),
                    label = "Đã hủy",
                    color = Color(0xFFE53935),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatBadge(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun HistoryAppointmentCard(appointment: Appointment, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when (appointment.status) {
                    "completed" -> Color(0xFFE8F5E9)
                    "cancelled" -> Color(0xFFFFEBEE)
                    else -> Color(0xFFFFF3E0)
                },
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        when (appointment.status) {
                            "completed" -> "✅"
                            "cancelled" -> "❌"
                            else -> "⏳"
                        },
                        fontSize = 24.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Bác sĩ: ${appointment.doctorName}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("📅 ${appointment.date} - ${appointment.timeSlot}", fontSize = 12.sp, color = Color(0xFF0D47A1))
                Text(appointment.symptoms.take(40), fontSize = 11.sp, color = Color.Gray, maxLines = 1)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun PrescriptionHistoryCard(appointment: Appointment, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE3F2FD),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📄", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Đơn thuốc ngày ${appointment.date}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("Bác sĩ: ${appointment.doctorName}", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun PatientStatisticsChart(appointments: List<Appointment>) {
    val monthlyData = appointments.groupBy { getMonthYear(it.date) }
        .map { (month, list) -> month to list.size }
        .sortedBy { it.first }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📈 Biểu đồ tần suất khám", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            Spacer(modifier = Modifier.height(16.dp))

            if (monthlyData.isEmpty()) {
                Text("Chưa có dữ liệu", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(32.dp))
            } else {
                Column {
                    monthlyData.forEach { (month, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(month, fontSize = 11.sp, modifier = Modifier.width(60.dp), color = Color.Gray)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE0E0E0))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth((count / (monthlyData.maxOfOrNull { it.second } ?: 1).toFloat()).coerceIn(0f, 1f))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0D47A1))
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$count lần", fontSize = 11.sp, modifier = Modifier.width(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📋", fontSize = 48.sp)
            Text("Chưa có lịch sử khám bệnh", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Bệnh nhân chưa có lịch hẹn nào", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun EmptyPrescriptionCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📄", fontSize = 48.sp)
            Text("Chưa có đơn thuốc", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("Bệnh nhân chưa được kê đơn thuốc", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

fun parseDate(dateStr: String): Date {
    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).parse(dateStr) ?: Date()
    } catch (e: Exception) { Date() }
}

fun getMonthYear(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        val date = sdf.parse(dateStr)
        SimpleDateFormat("MM/yyyy", Locale("vi", "VN")).format(date)
    } catch (e: Exception) { "" }
}