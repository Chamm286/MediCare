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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    onRegistrationData: (email: String, password: String, fullName: String, phone: String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenHeight < 700.dp
    val scrollState = rememberScrollState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isFormValid by remember { mutableStateOf(false) }

    // Hệ màu Medical Premium đồng bộ tuyệt đối với LoginScreen
    val brandPrimary = Color(0xFF1E6091)
    val brandGradient = listOf(Color(0xFF1A5276), Color(0xFF2980B9))
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val borderLight = Color(0xFFE2E8F0)
    val bgInput = Color(0xFFF8FAFC)
    val errorColor = Color(0xFFEF4444)

    // Animation vào màn hình mượt mà sử dụng FastOutSlowInEasing (Sạch lỗi biên dịch)
    val fadeIn by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isFormValid) 1f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "buttonScale"
    )

    // Kiểm tra tính hợp lệ của form
    LaunchedEffect(email, password, confirmPassword, fullName) {
        isFormValid = email.isNotEmpty() &&
                password.isNotEmpty() &&
                password == confirmPassword &&
                fullName.isNotEmpty()
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
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
            Spacer(modifier = Modifier.height(if (isSmallScreen) 30.dp else 50.dp))

            // 1. BRAND LOGO - Hình ảnh thực tế từ tài nguyên hệ thống
            Image(
                painter = painterResource(id = R.drawable.anh1),
                contentDescription = "Medicare Logo",
                modifier = Modifier
                    .size(if (isSmallScreen) 90.dp else 100.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. HEADER TYPOGRAPHY
            Text(
                text = "TẠO TÀI KHOẢN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = textPrimary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Đăng ký để trải nghiệm dịch vụ y tế số thông minh",
                fontSize = 13.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(if (isSmallScreen) 24.dp else 36.dp))

            // 3. MAIN FORM INPUTS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(BorderStroke(1.dp, borderLight), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                // Họ và tên
                Text(
                    text = "Họ và tên",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Nguyễn Văn A", color = textSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, null, tint = brandPrimary, modifier = Modifier.size(20.dp))
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

                Spacer(modifier = Modifier.height(14.dp))

                // Số điện thoại
                Text(
                    text = "Số điện thoại",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("0912345678", color = textSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.Phone, null, tint = brandPrimary, modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandPrimary,
                        unfocusedBorderColor = borderLight,
                        focusedContainerColor = bgInput,
                        unfocusedContainerColor = bgInput,
                        cursorColor = brandPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Email
                Text(
                    text = "Địa chỉ Email",
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandPrimary,
                        unfocusedBorderColor = borderLight,
                        focusedContainerColor = bgInput,
                        unfocusedContainerColor = bgInput,
                        cursorColor = brandPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mật khẩu
                Text(
                    text = "Mật khẩu",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••", color = textSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null, tint = brandPrimary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandPrimary,
                        unfocusedBorderColor = borderLight,
                        focusedContainerColor = bgInput,
                        unfocusedContainerColor = bgInput,
                        cursorColor = brandPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Xác nhận mật khẩu
                val isPasswordMismatch = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
                Text(
                    text = "Xác nhận mật khẩu",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("••••••••", color = textSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null, tint = if (isPasswordMismatch) errorColor else brandPrimary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = isPasswordMismatch,
                    supportingText = {
                        if (isPasswordMismatch) {
                            Text("Mật khẩu xác nhận không trùng khớp", color = errorColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandPrimary,
                        unfocusedBorderColor = borderLight,
                        errorBorderColor = errorColor,
                        focusedContainerColor = bgInput,
                        unfocusedContainerColor = bgInput,
                        errorContainerColor = bgInput,
                        cursorColor = brandPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. ACTION BUTTON WITH GRADIENT
                Button(
                    onClick = { onRegistrationData(email, password, fullName, phone) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(14.dp),
                    enabled = isFormValid,
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
                                brush = if (isFormValid) Brush.horizontalGradient(colors = brandGradient)
                                else Brush.horizontalGradient(colors = listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TIẾP TỤC ĐĂNG KÝ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. SWITCH TO LOGIN LINK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Đã có tài khoản? ", fontSize = 14.sp, color = textSecondary)
                    Text(
                        text = "Đăng nhập ngay",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandPrimary,
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. LEGAL TERMS & PRIVACY POLICY
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Đăng ký nghĩa là bạn đồng ý với ", fontSize = 11.sp, color = textSecondary, textAlign = TextAlign.Center)
                    Text(
                        text = "Điều khoản",
                        fontSize = 11.sp,
                        color = brandPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { }
                    )
                    Text(text = " & ", fontSize = 11.sp, color = textSecondary)
                    Text(
                        text = "Bảo mật",
                        fontSize = 11.sp,
                        color = brandPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}