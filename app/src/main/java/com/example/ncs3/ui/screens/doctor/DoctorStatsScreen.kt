package com.example.ncs3.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorStatsScreen(
    navController: NavController,
    doctorId: String
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var totalAppointments by remember { mutableStateOf(0) }
    var completedAppointments by remember { mutableStateOf(0) }
    var cancelledAppointments by remember { mutableStateOf(0) }
    var totalPatients by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(doctorId) {
        scope.launch {
            val appointments = repository.getAppointmentsByDoctor(doctorId)
            totalAppointments = appointments.size
            completedAppointments = appointments.count { it.status == "completed" }
            cancelledAppointments = appointments.count { it.status == "cancelled" }
            totalPatients = appointments.map { it.patientId }.distinct().size
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
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
                item {
                    StatsCard(
                        title = "Tổng quan",
                        icon = "📊",
                        stats = listOf(
                            "Tổng lịch hẹn" to totalAppointments.toString(),
                            "Hoàn thành" to completedAppointments.toString(),
                            "Đã hủy" to cancelledAppointments.toString(),
                            "Bệnh nhân" to totalPatients.toString()
                        )
                    )
                }

                item {
                    StatsCard(
                        title = "Tỷ lệ hoàn thành",
                        icon = "📈",
                        stats = listOf(
                            "Tỷ lệ thành công" to String.format("%.1f%%", (completedAppointments.toDouble() / totalAppointments) * 100),
                            "Tỷ lệ hủy" to String.format("%.1f%%", (cancelledAppointments.toDouble() / totalAppointments) * 100)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCard(title: String, icon: String, stats: List<Pair<String, String>>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("$icon $title", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            Spacer(modifier = Modifier.height(16.dp))
            stats.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 13.sp, color = Color.Gray)
                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                }
            }
        }
    }
}