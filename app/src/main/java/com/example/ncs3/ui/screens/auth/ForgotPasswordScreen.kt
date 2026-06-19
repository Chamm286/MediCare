package com.example.ncs3.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenHeight < 700.dp
    val scrollState = rememberScrollState()

    val auth = FirebaseAuth.getInstance()

    // Đổi sang String thuần để đồng bộ dữ liệu với Login/Register
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Hệ màu Medical Premium đồng bộ tuyệt đối với Login & Register
    val brandPrimary = Color(0xFF1E6091)
    val brandGradient = listOf(Color(0xFF1A5276), Color(0xFF2980B9))
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val borderLight = Color(0xFFE2E8F0)
    val bgInput = Color(0xFFF8FAFC)
    val successColor = Color(0xFF10B981)
    val errorColor = Color(0xFFEF4444)

    // Animation vào màn hình mượt mà sạch lỗi biên dịch
    val fadeIn by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "buttonScale"
    )

    fun sendResetEmail() {
        if (email.trim().isEmpty()) {
            errorMessage = "Vui lòng nhập địa chỉ email của bạn"
            showErrorDialog = true
            return
        }

        isLoading = true
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    showSuccessDialog = true
                } else {
                    errorMessage = task.exception?.message ?: "Gửi email khôi phục thất bại. Vui lòng thử lại!"
                    showErrorDialog = true
                }
            }
    }

    // Dialog Thông báo Thành công Cao cấp
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = successColor,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Đã gửi email khôi phục", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
            },
            text = {
                Text(
                    text = "Hệ thống đã gửi hướng dẫn đặt lại mật khẩu đến email $email. Vui lòng kiểm tra hộp thư (hoặc thư rác) của bạn.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quay lại đăng nhập", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Dialog Thông báo Lỗi
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Đã xảy ra lỗi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textPrimary) },
            text = { Text(errorMessage, fontSize = 14.sp, color = textSecondary) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Đóng", color = errorColor, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Text("Khôi phục mật khẩu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textPrimary)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = textPrimary)
                    }
                },
                modifier = Modifier.background(Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .alpha(fadeIn)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isSmallScreen) 35.dp else 55.dp))

            // 1. BRAND LOGO - Đồng bộ hình ảnh thực tế từ tài nguyên drawable thay thế emoji
            Image(
                painter = painterResource(id = R.drawable.anh1),
                contentDescription = "Medicare Logo",
                modifier = Modifier
                    .size(if (isSmallScreen) 90.dp else 100.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. HEADER TYPOGRAPHY
            Text(
                text = "QUÊN MẬT KHẨU?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = textPrimary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Nhập địa chỉ email đăng ký hệ thống để nhận liên kết thiết lập lại mật khẩu bảo mật mới",
                fontSize = 13.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(if (isSmallScreen) 30.dp else 40.dp))

            // 3. MAIN FORM INPUT CARD (Thiết kế phẳng tinh giản Modern UI)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(BorderStroke(1.dp, borderLight), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                // Email Input Field
                Text(
                    text = "Địa chỉ Email của bạn",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("example@email.com", color = textSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, null, tint = brandPrimary, modifier = Modifier.size(20.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandPrimary,
                        unfocusedBorderColor = borderLight,
                        focusedContainerColor = bgInput,
                        unfocusedContainerColor = bgInput,
                        cursorColor = brandPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(26.dp))

                // 4. ACTION BUTTON WITH GRADIENT GLOW EFFECT
                Button(
                    onClick = { sendResetEmail() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(colors = brandGradient),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Outlined.LockReset, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "GỬI YÊU CẦU KHÔI PHỤC", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. SWITCH TO LOGIN LINK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Bạn đã nhớ mật khẩu? ", fontSize = 14.sp, color = textSecondary)
                    Text(
                        text = "Đăng nhập",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandPrimary,
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}