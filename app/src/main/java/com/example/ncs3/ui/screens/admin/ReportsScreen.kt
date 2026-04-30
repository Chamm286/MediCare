package com.example.ncs3.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var isLoading by remember { mutableStateOf(true) }
    var selectedPeriod by remember { mutableStateOf(0) }
    var showPeriodDialog by remember { mutableStateOf(false) }
    val periods = listOf("Hôm nay", "Tuần này", "Tháng này", "Năm nay")

    // Dữ liệu thống kê
    var totalDoctors by remember { mutableStateOf(0) }
    var totalPatients by remember { mutableStateOf(0) }
    var totalAppointments by remember { mutableStateOf(0) }
    var totalRevenue by remember { mutableStateOf(0) }
    var completedAppointments by remember { mutableStateOf(0) }
    var cancelledAppointments by remember { mutableStateOf(0) }
    var pendingAppointments by remember { mutableStateOf(0) }

    // Dữ liệu biểu đồ
    val appointmentData = listOf(
        "T2" to 12, "T3" to 15, "T4" to 18, "T5" to 14, "T6" to 20, "T7" to 10, "CN" to 8
    )
    val revenueData = listOf(
        "T2" to 3.6, "T3" to 4.2, "T4" to 5.1, "T5" to 4.8, "T6" to 6.2, "T7" to 3.2, "CN" to 2.5
    )

    fun loadData() {
        scope.launch {
            val doctors = repository.getDoctors()
            // TODO: Lấy dữ liệu thực tế từ repository
            totalDoctors = doctors.size
            totalPatients = 150
            totalAppointments = 245
            totalRevenue = 124500000
            completedAppointments = 180
            cancelledAppointments = 25
            pendingAppointments = 40
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    if (showPeriodDialog) {
        AlertDialog(
            onDismissRequest = { showPeriodDialog = false },
            title = { Text("Chọn thời gian", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    periods.forEachIndexed { index, period ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPeriod = index
                                    showPeriodDialog = false
                                    loadData()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPeriod == index,
                                onClick = {
                                    selectedPeriod = index
                                    showPeriodDialog = false
                                    loadData()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(period, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPeriodDialog = false }) {
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
                        Text("Báo cáo & Thống kê", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(periods[selectedPeriod], fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showPeriodDialog = true }) {
                        Icon(Icons.Outlined.DateRange, null, tint = Color.White)
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
                // Tổng quan
                item {
                    Text("TỔNG QUAN", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportStatCard(
                            title = "Tổng doanh thu",
                            value = "${totalRevenue/1000000}",
                            unit = "triệu",
                            icon = "💰",
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatCard(
                            title = "Lịch hẹn",
                            value = totalAppointments.toString(),
                            unit = "cuộc",
                            icon = "📅",
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportStatCard(
                            title = "Bác sĩ",
                            value = totalDoctors.toString(),
                            unit = "người",
                            icon = "👨‍⚕️",
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.weight(1f)
                        )
                        ReportStatCard(
                            title = "Bệnh nhân",
                            value = totalPatients.toString(),
                            unit = "người",
                            icon = "👤",
                            color = Color(0xFFFCE4EC),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Biểu đồ lịch hẹn
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📊 Lịch hẹn theo ngày", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Tuần này", fontSize = 11.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Simple bar chart
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                appointmentData.forEach { (day, count) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .width(30.dp)
                                                .height((count * 3).dp)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(Color(0xFF0D47A1))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(day, fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                // Biểu đồ doanh thu
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💰 Doanh thu theo ngày", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Triệu đồng", fontSize = 11.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                revenueData.forEach { (day, revenue) ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .width(30.dp)
                                                .height((revenue * 10).dp)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(Color(0xFF4CAF50))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(day, fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                // Thống kê trạng thái lịch hẹn
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📋 Trạng thái lịch hẹn", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatusItem(
                                    label = "Hoàn thành",
                                    value = completedAppointments,
                                    total = totalAppointments,
                                    color = Color(0xFF4CAF50)
                                )
                                StatusItem(
                                    label = "Chờ xác nhận",
                                    value = pendingAppointments,
                                    total = totalAppointments,
                                    color = Color(0xFFFF9800)
                                )
                                StatusItem(
                                    label = "Đã hủy",
                                    value = cancelledAppointments,
                                    total = totalAppointments,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }
                }

                // Nút xuất báo cáo
                item {
                    Button(
                        onClick = { /* TODO: Xuất báo cáo PDF/Excel */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(Icons.Outlined.Download, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xuất báo cáo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportStatCard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, fontSize = 11.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Text(unit, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }
    }
}

@Composable
fun StatusItem(label: String, value: Int, total: Int, color: Color) {
    val percentage = if (total > 0) (value * 100 / total) else 0

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${percentage}%",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, maxLines = 2)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}