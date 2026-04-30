package com.example.ncs3.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.Appointment
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    navController: NavController,
    doctorId: String
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    val filters = listOf("Tất cả", "Chờ xác nhận", "Đã xác nhận", "Hoàn thành")

    LaunchedEffect(doctorId) {
        scope.launch {
            appointments = repository.getAppointmentsByDoctor(doctorId)
            isLoading = false
        }
    }

    val filteredAppointments = when (selectedFilter) {
        "Chờ xác nhận" -> appointments.filter { it.status == "pending" }
        "Đã xác nhận" -> appointments.filter { it.status == "confirmed" }
        "Hoàn thành" -> appointments.filter { it.status == "completed" }
        else -> appointments
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch hẹn", fontWeight = FontWeight.Bold, color = Color.White) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F7FA))
            ) {
                // Filter chips
                LazyRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) }
                        )
                    }
                }

                // Appointments list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAppointments) { appointment ->
                        AppointmentCardForDoctorList(
                            appointment = appointment,
                            onClick = { navController.navigate("appointment_detail/${appointment.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCardForDoctorList(appointment: Appointment, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (appointment.status) {
                    "pending" -> Color(0xFFFFF3E0)
                    "confirmed" -> Color(0xFFE8F5E9)
                    else -> Color(0xFFE3F2FD)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        when (appointment.status) {
                            "pending" -> "⏳"
                            "confirmed" -> "✅"
                            else -> "🏥"
                        },
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Bệnh nhân", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("📅 ${appointment.date} - ${appointment.timeSlot}", fontSize = 12.sp, color = Color(0xFF0D47A1))
                Text(appointment.symptoms.take(30), fontSize = 11.sp, color = Color.Gray, maxLines = 1)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
        }
    }
}