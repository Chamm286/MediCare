package com.example.ncs3.ui.screens.dashboard

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val hospital: String,
    val rating: Float,
    val experience: Int,
    val price: Int,
    val avatar: String = "👨‍⚕️"
)

data class Specialty(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color
)

data class AppointmentItem(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val hospital: String,
    val date: String,
    val time: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, onLoginClick: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var isLoggedIn by remember { mutableStateOf(false) }
    
    val tabs = listOf("Trang chủ", "Lịch hẹn", "Thuốc", "Hồ sơ", "Tài khoản")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "MediCare", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White,
                        fontSize = 22.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.White)
                    }
                    if (!isLoggedIn) {
                        TextButton(
                            onClick = onLoginClick,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = "Đăng nhập", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đăng nhập", fontSize = 13.sp)
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    NavigationItem(Icons.Default.Home, "Trang chủ"),
                    NavigationItem(Icons.Default.CalendarToday, "Lịch hẹn"),
                    NavigationItem(Icons.Default.MedicalServices, "Thuốc"),
                    NavigationItem(Icons.Default.Person, "Hồ sơ"),
                    NavigationItem(Icons.Default.AccountCircle, "Tài khoản")
                )
                
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0D47A1),
                            selectedTextColor = Color(0xFF0D47A1),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            when (selectedTab) {
                0 -> HomeContent(isLoggedIn, onLoginClick)
                1 -> AppointmentContent(isLoggedIn, onLoginClick)
                2 -> MedicineContent(isLoggedIn)
                3 -> ProfileContent(isLoggedIn, onLoginClick)
                4 -> AccountContent(isLoggedIn, onLoginClick)
            }
        }
    }
}

data class NavigationItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String
)

// ========== TRANG CHỦ ==========
@Composable
fun HomeContent(isLoggedIn: Boolean, onLoginClick: () -> Unit) {
    val specialties = listOf(
        Specialty("1", "Nội tổng quát", "🏥", Color(0xFF0D47A1)),
        Specialty("2", "Tim mạch", "❤️", Color(0xFFE53935)),
        Specialty("3", "Nhi khoa", "👶", Color(0xFF43A047)),
        Specialty("4", "Sản phụ khoa", "🤰", Color(0xFFEC407A)),
        Specialty("5", "Răng hàm mặt", "🦷", Color(0xFF8D6E63)),
        Specialty("6", "Da liễu", "💆", Color(0xFF7B1FA2))
    )
    
    val doctors = listOf(
        Doctor("1", "BS. Trần Thị Binh", "Tim mạch", "BV Chợ Rẫy", 4.9f, 15, 500000, "👩‍⚕️"),
        Doctor("2", "BS. Lê Văn Cường", "Nội tổng quát", "PK Hoàn Mỹ", 4.8f, 10, 400000, "👨‍⚕️"),
        Doctor("3", "BS. Nguyễn Thị Ngọc Dung", "Nhi khoa", "BV Nhi Đồng 1", 4.9f, 12, 450000, "👩‍⚕️"),
        Doctor("4", "BS. Phạm Văn Tâm", "Sản phụ khoa", "BV Từ Dũ", 4.7f, 8, 350000, "👨‍⚕️")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Banner
        item {
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                ) {
                    Text(
                        text = if (isLoggedIn) "Chào mừng trở lại!" else "Xin chào, Quý khách",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Đặt lịch khám ngay hôm nay",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Đặt lịch
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("📋 Đặt lịch khám", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text("Đặt hẹn với bác sĩ chuyên khoa", fontSize = 13.sp, color = Color(0xFF666666))
                    }
                    Button(
                        onClick = { if (!isLoggedIn) onLoginClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Đặt ngay", color = Color.White)
                    }
                }
            }
        }
        
        // Chuyên khoa
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Chuyên khoa phổ biến", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text("Chọn chuyên khoa để tìm bác sĩ", fontSize = 13.sp, color = Color(0xFF888888))
            }
        }
        
        item {
            LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(specialties) { specialty ->
                    SpecialtyCard(specialty = specialty)
                }
            }
        }
        
        // Bác sĩ
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Bác sĩ nổi bật", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text("Đội ngũ bác sĩ giàu kinh nghiệm", fontSize = 13.sp, color = Color(0xFF888888))
            }
        }
        
        items(doctors) { doctor ->
            DoctorCardItem(doctor = doctor, isLoggedIn = isLoggedIn, onLoginClick = onLoginClick)
        }
    }
}

// ========== LỊCH HẸN ==========
@Composable
fun AppointmentContent(isLoggedIn: Boolean, onLoginClick: () -> Unit) {
    if (!isLoggedIn) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📅", fontSize = 64.sp)
                Text("Vui lòng đăng nhập để xem lịch hẹn", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(16.dp))
                Button(onClick = onLoginClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))) {
                    Text("Đăng nhập ngay")
                }
            }
        }
    } else {
        val appointments = listOf(
            AppointmentItem("1", "BS. Trần Thị Binh", "Tim mạch", "BV Chợ Rẫy", "21/04/2025", "09:30", "Chờ khám"),
            AppointmentItem("2", "BS. Lê Văn Cường", "Nội tổng quát", "PK Hoàn Mỹ", "22/04/2025", "14:00", "Đã xác nhận")
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Lịch hẹn của tôi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            }
            
            items(appointments) { appointment ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(appointment.doctorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
                                Text(appointment.status, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Text(appointment.specialty, fontSize = 13.sp, color = Color(0xFF0D47A1))
                        Text(appointment.hospital, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("📅 ${appointment.date}", fontSize = 12.sp)
                            Text("⏰ ${appointment.time}", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ========== THUỐC ==========
@Composable
fun MedicineContent(isLoggedIn: Boolean) {
    if (!isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💊", fontSize = 64.sp)
                Text("Đăng nhập để xem đơn thuốc của bạn", fontSize = 16.sp, color = Color.Gray)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Đơn thuốc của tôi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            }
            
            item {
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cảm cúm cấp", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("BS. Trần Thị Binh - Tim mạch", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💊 Paracetamol 500mg", fontWeight = FontWeight.Bold)
                        Text("Uống 1 viên - 2 lần/ngày - Sau ăn", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💊 Efferalgan 500mg", fontWeight = FontWeight.Bold)
                        Text("Uống 1 viên - 2 lần/ngày - Sau ăn", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ========== HỒ SƠ ==========
@Composable
fun ProfileContent(isLoggedIn: Boolean, onLoginClick: () -> Unit) {
    if (!isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👤", fontSize = 64.sp)
                Text("Đăng nhập để xem hồ sơ của bạn", fontSize = 16.sp, color = Color.Gray)
                Button(onClick = onLoginClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)), modifier = Modifier.padding(top = 16.dp)) {
                    Text("Đăng nhập")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFF0D47A1)),
                contentAlignment = Alignment.Center
            ) {
                Text("A", fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Nguyễn Văn A", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("32 tuổi - Nam", fontSize = 14.sp, color = Color.Gray)
            Text("Mã số: MC205001234", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileRow(Icons.Default.Email, "Email", "nguyenvana@gmail.com")
                    Divider()
                    ProfileRow(Icons.Default.Phone, "Số điện thoại", "0123456789")
                    Divider()
                    ProfileRow(Icons.Default.CalendarToday, "Ngày sinh", "01/01/1993")
                }
            }
        }
    }
}

// ========== TÀI KHOẢN ==========
@Composable
fun AccountContent(isLoggedIn: Boolean, onLoginClick: () -> Unit) {
    if (!isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔐", fontSize = 64.sp)
                Text("Vui lòng đăng nhập", fontSize = 16.sp, color = Color.Gray)
                Button(onClick = onLoginClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)), modifier = Modifier.padding(top = 16.dp)) {
                    Text("Đăng nhập")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AccountMenuItem(Icons.Default.Lock, "Đổi mật khẩu")
                    Divider()
                    AccountMenuItem(Icons.Default.Notifications, "Thông báo")
                    Divider()
                    AccountMenuItem(Icons.Default.Help, "Trợ giúp")
                    Divider()
                    AccountMenuItem(Icons.Default.Info, "Giới thiệu")
                }
            }
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Đăng xuất", color = Color.White)
            }
        }
    }
}

// ========== COMPONENTS ==========
@Composable
fun SpecialtyCard(specialty: Specialty) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.width(100.dp).height(100.dp).clickable { }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(specialty.icon, fontSize = 36.sp)
            Text(specialty.name, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333333), textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

@Composable
fun DoctorCardItem(doctor: Doctor, isLoggedIn: Boolean, onLoginClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                Text(doctor.avatar, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text(doctor.specialty, fontSize = 13.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Text("⭐ ${doctor.rating}", color = Color(0xFFFFB800), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${doctor.experience} năm KN", fontSize = 12.sp, color = Color(0xFF888888))
                }
                Text(doctor.hospital, fontSize = 11.sp, color = Color(0xFF999999))
            }
            Button(onClick = { if (!isLoggedIn) onLoginClick() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)), shape = RoundedCornerShape(20.dp), modifier = Modifier.width(70.dp).height(36.dp)) {
                Text("Chọn", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 14.sp)
        }
        Text(value, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun AccountMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { }, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp)
    }
}