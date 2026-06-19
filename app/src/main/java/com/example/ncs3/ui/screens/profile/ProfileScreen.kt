package com.example.ncs3.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ncs3.ui.components.*
import com.example.ncs3.utils.SharedPrefs
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    userId: String,
    userRole: String = "patient",
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestore = remember { FirebaseFirestore.getInstance() }

    val primaryColor = Color(0xFF0D47A1)
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(primaryColor, Color(0xFF1976D2))
    )

    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }

    // 👉 LẤY DỮ LIỆU TỪ SHAREDPREFS TRƯỚC
    var userName by remember { mutableStateOf(SharedPrefs.getUserName()) }
    var fullName by remember { mutableStateOf(SharedPrefs.getUserName()) }
    var email by remember { mutableStateOf(SharedPrefs.getUserEmail()) }
    var phone by remember { mutableStateOf(SharedPrefs.getUserPhone()) }
    var dob by remember { mutableStateOf(SharedPrefs.getUserDob()) }
    var gender by remember { mutableStateOf(SharedPrefs.getUserGender()) }
    var address by remember { mutableStateOf(SharedPrefs.getUserAddress()) }
    var bloodType by remember { mutableStateOf(SharedPrefs.getUserBloodType()) }
    var avatarUrlOnline by remember { mutableStateOf(SharedPrefs.getUserAvatar()) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    var heightCm by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var insuranceCard by remember { mutableStateOf("") }

    val bmiStatus = remember(heightCm, weightKg) {
        val h = heightCm.toFloatOrNull()?.div(100f)
        val w = weightKg.toFloatOrNull()
        if (h != null && w != null && h > 0) {
            val bmi = w / h.pow(2)
            val formattedBmi = (bmi * 10).roundToInt() / 10f
            when {
                bmi < 18.5 -> "$formattedBmi (Gầy)"
                bmi < 24.9 -> "$formattedBmi (Bình thường)"
                bmi < 29.9 -> "$formattedBmi (Tiền béo phì)"
                else -> "$formattedBmi (Béo phì)"
            }
        } else "Chưa đủ chỉ số"
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUri = uri
            avatarUrlOnline = uri.toString()
            Toast.makeText(context, "Đã chọn ảnh đại diện mới!", Toast.LENGTH_SHORT).show()
        }
    }

    // 👉 ĐỒNG BỘ DỮ LIỆU TỪ FIRESTORE
    LaunchedEffect(userId) {
        if (isLoggedIn && userId.isNotEmpty()) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        fullName = doc.getString("fullName") ?: fullName
                        email = doc.getString("email") ?: email
                        phone = doc.getString("phone") ?: phone
                        userName = fullName.split(" ").firstOrNull() ?: "Bệnh nhân"
                        dob = doc.getString("dob") ?: dob
                        gender = doc.getString("gender") ?: gender
                        address = doc.getString("address") ?: address
                        bloodType = doc.getString("bloodType") ?: bloodType
                        avatarUrlOnline = doc.getString("avatar") ?: avatarUrlOnline
                        heightCm = doc.getString("heightCm") ?: ""
                        weightKg = doc.getString("weightKg") ?: ""
                        allergies = doc.getString("allergies") ?: ""
                        insuranceCard = doc.getString("insuranceCard") ?: ""

                        // 👉 LƯU LẠI SHAREDPREFS
                        SharedPrefs.saveUserName(fullName)
                        SharedPrefs.saveUserAvatar(avatarUrlOnline)
                        SharedPrefs.saveUserPhone(phone)
                        SharedPrefs.saveUserDob(dob)
                        SharedPrefs.saveUserGender(gender)
                        SharedPrefs.saveUserAddress(address)
                        SharedPrefs.saveUserBloodType(bloodType)
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hồ Sơ Sức Khỏe", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    navigationIcon = {
                        if (isLoggedIn) {
                            IconButton(onClick = { showDrawer = !showDrawer }) {
                                Icon(Icons.Default.Menu, null, tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (isEditing) {
                                isLoading = true
                                val dataToSave = mapOf(
                                    "fullName" to fullName,
                                    "phone" to phone,
                                    "dob" to dob,
                                    "gender" to gender,
                                    "address" to address,
                                    "bloodType" to bloodType,
                                    "avatar" to avatarUrlOnline,
                                    "heightCm" to heightCm,
                                    "weightKg" to weightKg,
                                    "allergies" to allergies,
                                    "insuranceCard" to insuranceCard
                                )

                                firestore.collection("users").document(userId)
                                    .update(dataToSave)
                                    .addOnSuccessListener {
                                        userName = fullName.split(" ").firstOrNull() ?: "Bệnh nhân"
                                        SharedPrefs.saveUserName(fullName)
                                        SharedPrefs.saveUserAvatar(avatarUrlOnline)
                                        isEditing = false
                                        isLoading = false
                                        Toast.makeText(context, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Lỗi lưu dữ liệu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                isEditing = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null, tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
                )
            },
            bottomBar = {
                when (userRole) {
                    "admin" -> AdminBottomNavigation(navController = navController, currentRoute = "profile")
                    "doctor" -> DoctorBottomNavigation(navController = navController, currentRoute = "profile")
                    else -> BottomNavigation(
                        selectedRoute = "profile",
                        onNavigateToDashboard = { navController.navigate("dashboard") },
                        onNavigateToAppointments = { navController.navigate("appointments") },
                        onNavigateToDoctors = { navController.navigate("doctors") },
                        onNavigateToProfile = {},
                        onQuickBooking = { navController.navigate("booking") },
                        onFindDoctor = { navController.navigate("doctors") },
                        onScanQR = { navController.navigate("scan") }
                    )
                }
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF1F5F9)),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- PROFILE HEADER ---
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(gradientBackground).padding(top = 16.dp, bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(92.dp)) {
                                    Surface(
                                        shape = CircleShape, color = Color.White,
                                        modifier = Modifier.fillMaxSize().border(2.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    ) {
                                        // 👉 HIỂN THỊ AVATAR
                                        if (avatarUri != null) {
                                            AsyncImage(
                                                model = avatarUri,
                                                contentDescription = null,
                                                modifier = Modifier.clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else if (avatarUrlOnline.isNotEmpty() && avatarUrlOnline != "null") {
                                            AsyncImage(
                                                model = avatarUrlOnline,
                                                contentDescription = null,
                                                modifier = Modifier.clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            // 👉 HIỂN THỊ CHỮ CÁI ĐẦU CỦA TÊN (không phải họ)
                                            val firstName = fullName.split(" ").lastOrNull() ?: fullName
                                            Box(modifier = Modifier.background(Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                                                Text(
                                                    firstName.take(1).uppercase().ifEmpty { "?" },
                                                    fontSize = 32.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryColor
                                                )
                                            }
                                        }
                                    }
                                    if (isEditing) {
                                        Box(
                                            modifier = Modifier.size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF9800))
                                                .clickable { imagePickerLauncher.launch("image/*") }
                                                .border(1.5.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // 👉 HIỂN THỊ TÊN ĐẦY ĐỦ
                                Text(
                                    fullName.ifEmpty { "Chưa cập nhật họ tên" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(email, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // --- THÔNG TIN CƠ BẢN ---
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("THÔNG TIN CƠ BẢN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))

                            ProfileRowItem(label = "Họ và tên bệnh nhân", value = fullName, placeholder = "Nhập họ và tên đầy đủ", leadingIcon = Icons.Outlined.Person, isEditing = isEditing, onValueChange = { fullName = it })
                            ProfileRowItem(label = "Số điện thoại liên hệ", value = phone, placeholder = "Nhập số điện thoại", leadingIcon = Icons.Outlined.Phone, isEditing = isEditing, onValueChange = { phone = it })

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1.1f)) {
                                    ProfileRowItem(label = "Ngày sinh", value = dob, placeholder = "Ngày/Tháng/Năm", leadingIcon = Icons.Outlined.CalendarMonth, isEditing = isEditing, onValueChange = { dob = it })
                                }
                                Box(modifier = Modifier.weight(0.9f)) {
                                    ProfileRowItem(label = "Giới tính", value = gender, placeholder = "Nam/Nữ", leadingIcon = Icons.Outlined.Wc, isEditing = isEditing, onValueChange = { gender = it })
                                }
                            }
                        }
                    }

                    // --- THỂ TRẠNG ---
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("THỂ TRẠNG & CHỈ SỐ SINH TỒN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProfileRowItem(label = "Chiều cao (cm)", value = heightCm, placeholder = "Ví dụ: 170", leadingIcon = Icons.Outlined.Height, isEditing = isEditing, onValueChange = { heightCm = it })
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    ProfileRowItem(label = "Cân nặng (kg)", value = weightKg, placeholder = "Ví dụ: 65", leadingIcon = Icons.Outlined.MonitorWeight, isEditing = isEditing, onValueChange = { weightKg = it })
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProfileRowItem(label = "Chỉ số BMI (Tự tính)", value = if(!isEditing) bmiStatus else heightCm.isNotEmpty().let { bmiStatus }, placeholder = "--", leadingIcon = Icons.Outlined.Analytics, isEditing = false, onValueChange = {})
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    ProfileRowItem(label = "Nhóm máu", value = bloodType, placeholder = "A/B/O/AB", leadingIcon = Icons.Outlined.Bloodtype, isEditing = isEditing, onValueChange = { bloodType = it })
                                }
                            }

                            ProfileRowItem(label = "Tiền sử dị ứng (Thuốc, Thức ăn)", value = allergies, placeholder = "Ghi rõ nếu có dị ứng hoặc ghi 'Không'", leadingIcon = Icons.Outlined.Coronavirus, isEditing = isEditing, onValueChange = { allergies = it })
                        }
                    }

                    // --- BẢO HIỂM ---
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("BẢO HIỂM VÀ CƯ TRÚ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))

                            ProfileRowItem(label = "Mã số thẻ Bảo Hiểm Y Tế (BHYT)", value = insuranceCard, placeholder = "Nhập 15 ký tự trên thẻ BHYT", leadingIcon = Icons.Outlined.CardMembership, isEditing = isEditing, onValueChange = { insuranceCard = it })
                            ProfileRowItem(label = "Địa chỉ cư trú hiện tại", value = address, placeholder = "Số nhà, tên đường, Phường/Xã, Tỉnh...", leadingIcon = Icons.Outlined.Home, isEditing = isEditing, onValueChange = { address = it })

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isEditing) {
                                Button(
                                    onClick = {
                                        isLoading = true
                                        val dataToSave = mapOf(
                                            "fullName" to fullName, "phone" to phone, "dob" to dob, "gender" to gender,
                                            "address" to address, "bloodType" to bloodType, "avatar" to avatarUrlOnline,
                                            "heightCm" to heightCm, "weightKg" to weightKg, "allergies" to allergies, "insuranceCard" to insuranceCard
                                        )
                                        firestore.collection("users").document(userId).update(dataToSave)
                                            .addOnSuccessListener {
                                                isEditing = false
                                                isLoading = false
                                                SharedPrefs.saveUserName(fullName)
                                                SharedPrefs.saveUserAvatar(avatarUrlOnline)
                                                Toast.makeText(context, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                                            }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Save, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Lưu", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DRAWER ---
        if (showDrawer && isLoggedIn) {
            DrawerMenu(
                isOpen = showDrawer, isLoggedIn = isLoggedIn, userRole = userRole, userName = userName,
                onClose = { showDrawer = false },
                onNavigateToHome = { navController.navigate("dashboard"); showDrawer = false },
                onNavigateToProfile = { showDrawer = false },
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
}

// --- PROFILE ROW ITEM ---
@Composable
fun ProfileRowItem(
    label: String,
    value: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    val isMissing = value.trim().isEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = isEditing,
            transitionSpec = { fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(180)) },
            label = "ProfileFieldTransition"
        ) { editing ->
            if (editing) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isMissing) Color(0xFFD32F2F) else Color(0xFF64748B), modifier = Modifier.padding(bottom = 2.dp))
                    OutlinedTextField(
                        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(leadingIcon, null, tint = Color(0xFF0D47A1), modifier = Modifier.size(20.dp)) },
                        placeholder = { Text(placeholder, fontSize = 13.sp) },
                        singleLine = true, shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0D47A1),
                            unfocusedBorderColor = if (isMissing) Color(0xFFE2E8F0) else Color(0xFFCBD5E1)
                        )
                    )
                    if (isMissing) {
                        Text(text = "* Thông tin này đang để trống", fontSize = 10.sp, color = Color(0xFFD32F2F), modifier = Modifier.padding(top = 2.dp, start = 4.dp))
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 0.5.dp, modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFFF1F5F9)), contentAlignment = Alignment.Center) {
                            Icon(leadingIcon, null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = value.ifEmpty { placeholder }, fontSize = 14.sp,
                                fontWeight = if (value.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (value.isNotEmpty()) Color(0xFF0F172A) else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}