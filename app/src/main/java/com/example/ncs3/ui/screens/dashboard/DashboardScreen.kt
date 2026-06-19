package com.example.ncs3.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.graphicsLayer
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import com.example.ncs3.R
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import java.util.Calendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import com.example.ncs3.utils.SharedPrefs
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.Doctor
import com.example.ncs3.data.models.Specialty
import com.example.ncs3.data.repository.MedicareRepository
import com.example.ncs3.ui.components.*
import com.example.ncs3.utils.RegistrationData.fullName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PromoItem(
    val id: String = "",
    val icon: String,
    val title: String,
    val desc: String,
    val color: String,
    val discount: String = "",
    val code: String = ""
)

data class QuickAction(
    val icon: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val route: String
)

data class TipItem(
    val icon: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val benefits: List<String>,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    userId: String,
    userRole: String = "patient",
    onLogout: () -> Unit = {}
) {
    val repository = remember { MedicareRepository() }
    var userName by remember { mutableStateOf("") }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var specialties by remember { mutableStateOf<List<Specialty>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDrawer by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var currentBannerIndex by remember { mutableStateOf(0) }

    val banners = listOf(
        Triple("🏥", "ĐẶT LỊCH KHÁM", "Đặt lịch nhanh chóng, tiện lợi"),
        Triple("💊", "MUA THUỐC ONLINE", "Giao hàng tận nơi, ưu đãi lớn"),
        Triple("🩺", "TƯ VẤN SỨC KHỎE", "Bác sĩ giàu kinh nghiệm 24/7")
    )

    val quickActions = listOf(
        QuickAction("🏥", "Đặt lịch khám", "Khám bệnh", Color(0xFF0D47A1), "booking"),
        QuickAction("💊", "Mua thuốc", "Giao tận nơi", Color(0xFF4CAF50), "medicine_store"),
        QuickAction("📋", "Tra cứu đơn", "Đơn thuốc của bạn", Color(0xFFFF9800), "prescription"),
        QuickAction("👤", "Bác sĩ", "Tư vấn online", Color(0xFF9C27B0), "doctors")
    )

    val promotionsState = remember { mutableStateOf<List<PromoItem>>(emptyList()) }
    var promotions by promotionsState

    LaunchedEffect(Unit) {
        scope.launch {
            repository.seedSpecialtiesIfEmpty()
            repository.seedHospitalsIfEmpty()
            repository.seedMedicinesIfEmpty()
            repository.seedDoctorsIfEmpty()
            repository.seedPromotionsIfEmpty()

            if (isLoggedIn && userId.isNotEmpty()) {
                val user = repository.getUser(userId)
                userName = user?.fullName?.split(" ")?.firstOrNull() ?: "Bạn"
            } else {
                userName = "Khách"
            }

            doctors = repository.getDoctors().take(6)
            specialties = repository.getSpecialties()

            val promoData = repository.getActivePromotions()
            promotions = if (promoData.isNotEmpty()) {
                promoData.map { promo ->
                    PromoItem(
                        id = promo.id, icon = promo.icon, title = promo.title,
                        desc = promo.description, color = promo.color,
                        discount = promo.discount, code = promo.code
                    )
                }
            } else {
                listOf(
                    PromoItem("⚡", "GIẢM 30%", "Đặt lịch đầu tiên", "#FF6B6B", "30%", "MEDI30"),
                    PromoItem("🚚", "FREE SHIP", "Đơn từ 200k", "#4ECDC4", "Free", "SHIPFREE"),
                    PromoItem("🎁", "TẶNG VOUCHER", "Khám định kỳ", "#FFB347", "50k", "VOUCHER50")
                )
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentBannerIndex = (currentBannerIndex + 1) % banners.size
        }
    }

    val primaryColor = Color(0xFF0D47A1)

    // ─── ĐÃ SỬA: Dùng Box tổng bao trùm toàn bộ để các layer đè lên nhau chuẩn khít ───
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer nền dưới cùng: Toàn bộ nội dung ứng dụng chính
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🏥", fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("MediCare", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Khỏe để sống trọn vẹn", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor),
                    navigationIcon = {
                        if (isLoggedIn) {
                            IconButton(onClick = { showDrawer = !showDrawer }) {
                                Icon(Icons.Default.Menu, null, tint = Color.White)
                            }
                        }
                    },
                    // Phần hiển thị tên và avatar trong DashboardScreen
                    actions = {
                        if (!isLoggedIn) {
                            // ... button login
                        } else {
                            val avatarUrl = SharedPrefs.getUserAvatar()
                            val fullName = SharedPrefs.getUserName()
                            val displayName = fullName.split(" ").lastOrNull() ?: fullName  // 👈 LẤY TÊN

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(displayName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { navController.navigate("profile") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (avatarUrl.isNotEmpty() && avatarUrl != "null") {
                                        AsyncImage(
                                            model = avatarUrl,
                                            contentDescription = "Avatar",
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                            placeholder = painterResource(R.drawable.anh1),
                                            error = painterResource(R.drawable.anh1)
                                        )
                                    } else {
                                        // 👈 LẤY CHỮ CÁI ĐẦU CỦA TÊN
                                        val firstChar = displayName.take(1).uppercase().ifEmpty { "?" }
                                        Text(
                                            firstChar,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                when (userRole) {
                    "admin" -> AdminBottomNavigation(
                        navController = navController,
                        currentRoute = "admin_dashboard"
                    )
                    "doctor" -> DoctorBottomNavigation(
                        navController = navController,
                        currentRoute = "doctor_dashboard"
                    )
                    else -> BottomNavigation(
                        selectedRoute = "dashboard",
                        onNavigateToDashboard = {},
                        onNavigateToAppointments = { navController.navigate("appointments") },
                        onNavigateToDoctors = { navController.navigate("doctors") },
                        onNavigateToProfile = { navController.navigate("profile") },
                        onQuickBooking = { navController.navigate("booking") },
                        onFindDoctor = { navController.navigate("doctors") },
                        onScanQR = { navController.navigate("scan") }
                    )
                }
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { SearchBarMedical(navController) }
                    item { QuickActionsMedical(quickActions, navController, isLoggedIn) }
                    item { BannerCarousel(banners, currentBannerIndex) }

                    if (promotions.isNotEmpty()) {
                        item {
                            SectionHeader("🔥 ƯU ĐÃI ĐẶC BIỆT", "Xem thêm") { }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(promotions) { promo -> PromoCard(promo) }
                            }
                        }
                    }

                    item {
                        SectionHeader("🏥 CHUYÊN KHOA", "Xem tất cả") { navController.navigate("specialties") }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(specialties) { specialty -> SpecialtyCard(specialty) }
                        }
                    }

                    item {
                        SectionHeader("⭐ BÁC SĨ NỔI BẬT", "Xem tất cả") { navController.navigate("doctors") }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(doctors) { doctor ->
                                DoctorCard(
                                    doctor = doctor,
                                    primaryColor = primaryColor,
                                    onViewDetail = { navController.navigate("doctor_detail/${doctor.id}") },
                                    onBooking = {
                                        if (isLoggedIn) navController.navigate("booking/${doctor.id}")
                                        else navController.navigate("login")
                                    }
                                )
                            }
                        }
                    }

                    item { HealthTipCard() }
                }
            }
        }

        // Layer nổi phía trên: Xử lý đóng mở Drawer lơ lửng mượt mà không gây xê dịch nền
        DrawerMenu(
            isOpen = showDrawer,
            isLoggedIn = isLoggedIn,
            userRole = userRole,
            userName = userName,
            onClose = { showDrawer = false },
            onNavigateToHome = { navController.navigate("dashboard"); showDrawer = false },
            onNavigateToProfile = { navController.navigate("profile"); showDrawer = false },
            onNavigateToAppointments = { navController.navigate("appointments"); showDrawer = false },
            onNavigateToDoctors = { navController.navigate("doctors"); showDrawer = false },
            onNavigateToMedicine = { navController.navigate("medicine_store"); showDrawer = false },
            onNavigateToHistory = { navController.navigate("history"); showDrawer = false },
            onNavigateToSettings = { navController.navigate("settings"); showDrawer = false },
            onNavigateToSchedule = { navController.navigate("doctor_schedule"); showDrawer = false },
            onNavigateToPatients = { navController.navigate("my_patients"); showDrawer = false },
            onNavigateToManageDoctors = { navController.navigate("manage_doctors"); showDrawer = false },
            onNavigateToManagePatients = { navController.navigate("manage_patients"); showDrawer = false },
            onNavigateToReports = { navController.navigate("reports"); showDrawer = false },
            onLogout = { onLogout(); showDrawer = false },
            onLogin = { navController.navigate("login"); showDrawer = false }
        )
    }
}

// ========== CÁC HÀM COMPONENT PHỤ TRỢ ==========

@Composable
fun SearchBarMedical(navController: NavController) {
    val searchHints = listOf(
        "🔍 Tìm bác sĩ nội khoa...",
        "🏥 Bệnh viện Bạch Mai...",
        "💊 Thuốc gần đây...",
        "🩺 Khám tim mạch...",
        "👨‍⚕️ Bác sĩ giỏi..."
    )
    var currentHint by remember { mutableStateOf(searchHints[0]) }
    LaunchedEffect(Unit) {
        var index = 0
        while (true) {
            delay(3000)
            index = (index + 1) % searchHints.size
            currentHint = searchHints[index]
        }
    }
    val transition = rememberInfiniteTransition(label = "search")
    val alpha by transition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "searchAlpha"
    )
    Surface(
        shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { navController.navigate("search") }
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Search, "Tìm kiếm", tint = Color(0xFF0D47A1), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = currentHint, fontSize = 14.sp, color = Color(0xFF0D47A1).copy(alpha = alpha), modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.Mic, "Tìm bằng giọng nói", tint = Color(0xFF9AA0A6), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun QuickActionsMedical(actions: List<QuickAction>, navController: NavController, isLoggedIn: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(action = actions[0], modifier = Modifier.weight(1f), onClick = {
                if (isLoggedIn) navController.navigate("booking") else navController.navigate("login")
            })
            QuickActionCard(action = actions[1], modifier = Modifier.weight(1f), onClick = {
                if (isLoggedIn) navController.navigate("medicine_store") else navController.navigate("login")
            })
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(action = actions[2], modifier = Modifier.weight(1f), onClick = {
                if (isLoggedIn) navController.navigate("prescription") else navController.navigate("login")
            })
            QuickActionCard(action = actions[3], modifier = Modifier.weight(1f), onClick = {
                if (isLoggedIn) navController.navigate("doctors") else navController.navigate("login")
            })
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp), modifier = modifier.height(80.dp).clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(action.color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Text(action.icon, fontSize = 26.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(action.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(action.subtitle, fontSize = 10.sp, color = Color(0xFF9AA0A6), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun BannerCarousel(banners: List<Triple<String, String, String>>, currentIndex: Int) {
    val banner = banners[currentIndex]
    val colors = listOf(Color(0xFF0D47A1), Color(0xFF4CAF50), Color(0xFFFF9800))
    val bannerColor = colors[currentIndex % colors.size]
    Card(
        shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(120.dp).clickable { },
        colors = CardDefaults.cardColors(containerColor = bannerColor)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(banner.second, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(banner.third, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.width(90.dp)) {
                    Text("Xem ngay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = bannerColor, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            Text(banner.first, fontSize = 48.sp)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
        TextButton(onClick = onAction) { Text(actionText, fontSize = 12.sp, color = Color(0xFF0D47A1)) }
    }
}

@Composable
fun PromoCard(promo: PromoItem) {
    val promoColor = Color(android.graphics.Color.parseColor(promo.color))
    Card(
        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = promoColor.copy(alpha = 0.1f)),
        modifier = Modifier.width(140.dp).height(80.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(promo.icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(promo.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = promoColor)
                Text(promo.desc, fontSize = 9.sp, color = Color(0xFF6C757D), maxLines = 2)
                Text(promo.code, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = promoColor)
            }
        }
    }
}

@Composable
fun SpecialtyCard(specialty: Specialty) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp).clickable { }) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFF1F3F4)), contentAlignment = Alignment.Center) {
            Text(specialty.icon, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(specialty.name, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 2, textAlign = TextAlign.Center, color = Color(0xFF1A1A2E))
    }
}

@Composable
fun DoctorCard(doctor: Doctor, primaryColor: Color, onViewDetail: () -> Unit, onBooking: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.width(170.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ SỬA: Hiển thị ảnh thật từ Firebase
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))))
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onViewDetail() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    // ✅ KIỂM TRA: Nếu có imageUrl thì hiển thị ảnh, không thì hiển thị avatar
                    if (doctor.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = doctor.imageUrl,
                            contentDescription = doctor.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.anh1),
                            error = painterResource(R.drawable.anh1)
                        )
                    } else {
                        Text(doctor.avatar.ifEmpty { "👨‍⚕️" }, fontSize = 36.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                doctor.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF1A1A2E),
                modifier = Modifier.clickable { onViewDetail() }
            )

            Text(
                doctor.specialty,
                fontSize = 11.sp,
                color = primaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        if (index < doctor.rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFFFB800)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("${doctor.rating}", fontSize = 11.sp, color = Color(0xFF9AA0A6))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = primaryColor.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "💰 ${doctor.price/1000}k",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBooking,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Icon(Icons.Outlined.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Đặt lịch", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun HealthTipCard() {
    val context = LocalContext.current
    val tips = listOf(
        TipItem(icon = "💧", title = "Hydratation", subtitle = "Uống đủ nước",
            description = "2 lít/ngày giúp thanh lọc cơ thể, tăng cường trao đổi chất và làm đẹp da.",
            benefits = listOf("💪 Tăng năng lượng", "🧠 Tốt cho não", "❤️ Tim mạch khỏe"), color = Color(0xFF2196F3)),
        TipItem(icon = "😴", title = "Sleep Well", subtitle = "Ngủ đủ giấc",
            description = "Ngủ 7-8 tiếng mỗi đêm giúp phục hồi năng lượng, tăng cường trí nhớ và giảm stress.",
            benefits = listOf("🧠 Cải thiện trí nhớ", "💪 Phục hồi năng lượng", "😊 Giảm căng thẳng"), color = Color(0xFF9C27B0)),
        TipItem(icon = "🏃", title = "Active Life", subtitle = "Tập thể dục",
            description = "30 phút mỗi ngày giúp tim mạch khỏe mạnh, đốt cháy calo và cải thiện tâm trạng.",
            benefits = listOf("❤️ Tim mạch khỏe", "🔥 Đốt cháy calo", "😄 Cải thiện tâm trạng"), color = Color(0xFF4CAF50)),
        TipItem(icon = "🥗", title = "Clean Eating", subtitle = "Ăn rau xanh",
            description = "Bổ sung rau xanh và trái cây giúp tăng cường vitamin, chất xơ và hệ miễn dịch.",
            benefits = listOf("🥦 Tăng vitamin", "🛡️ Tăng đề kháng", "💚 Thanh lọc cơ thể"), color = Color(0xFFFF9800)),
        TipItem(icon = "🧘", title = "Mindfulness", subtitle = "Thiền định",
            description = "Thiền 10 phút mỗi ngày giúp giảm stress, cải thiện sự tập trung và cân bằng cảm xúc.",
            benefits = listOf("😌 Giảm stress", "🎯 Tăng tập trung", "⚖️ Cân bằng cảm xúc"), color = Color(0xFFE91E63))
    )
    var currentTipIndex by remember { mutableStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "glowFloat"
    )
    LaunchedEffect(Unit) { currentTipIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % tips.size }
    val currentTip = tips[currentTipIndex]

    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().background(brush = Brush.verticalGradient(colors = listOf(currentTip.color.copy(alpha = 0.08f), Color.White)))) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    var scale by remember { mutableStateOf(1f) }
                    LaunchedEffect(currentTipIndex) { scale = 1.1f; delay(200); scale = 1f }
                    Box(modifier = Modifier.size(60.dp).scale(scale).graphicsLayer { shadowElevation = 8f * glow; shape = CircleShape; clip = true }
                        .clip(CircleShape).background(brush = Brush.radialGradient(colors = listOf(currentTip.color, currentTip.color.copy(alpha = 0.7f)))),
                        contentAlignment = Alignment.Center) { Text(currentTip.icon, fontSize = 32.sp) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("✨ TIPS HÔM NAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = currentTip.color, letterSpacing = 1.sp)
                            Surface(shape = RoundedCornerShape(20.dp), color = currentTip.color.copy(alpha = 0.1f)) {
                                Text("${currentTipIndex + 1}/${tips.size}", fontSize = 9.sp, color = currentTip.color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp))
                            }
                        }
                        Text(currentTip.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                        Text(currentTip.subtitle, fontSize = 12.sp, color = Color(0xFF9AA0A6))
                    }
                    Surface(shape = CircleShape, color = Color(0xFFF5F5F5), modifier = Modifier.size(36.dp).clickable { isExpanded = !isExpanded }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown, null, tint = currentTip.color, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(currentTip.description, fontSize = 13.sp, color = Color(0xFF5F6368), lineHeight = 20.sp)

                AnimatedVisibility(visible = isExpanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("🎯 LỢI ÍCH TIÊU BIỂU", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = currentTip.color)
                        Spacer(modifier = Modifier.height(12.dp))
                        currentTip.benefits.forEachIndexed { index, benefit ->
                            AnimatedVisibility(visible = isExpanded, enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300 + index * 100)) + fadeIn()) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(currentTip.color))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(benefit, fontSize = 13.sp, color = Color(0xFF5F6368))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(shape = RoundedCornerShape(16.dp), color = currentTip.color.copy(alpha = 0.05f), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("📖", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Theo khuyến nghị của WHO", fontSize = 11.sp, color = currentTip.color, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(30.dp), color = Color(0xFFF5F5F5),
                        modifier = Modifier.weight(1f).height(48.dp).clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, currentTip.title)
                                putExtra(Intent.EXTRA_TEXT, "${currentTip.title} - ${currentTip.subtitle}\n\n${currentTip.description}\n\nChia sẻ từ ứng dụng MediCare.")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ mẹo sức khỏe"))
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Share, null, tint = Color(0xFF5F6368), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chia sẻ", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF5F6368))
                        }
                    }
                }
            }
        }
    }
}
