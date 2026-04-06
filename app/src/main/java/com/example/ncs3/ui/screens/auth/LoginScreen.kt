package com.example.ncs3.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF00BCD4)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text("🏥", fontSize = 60.sp)
            Text(
                text = "MediCare",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Đặt lịch khám dễ dàng",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = "" },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = errorMessage.isNotEmpty()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        label = { Text("Mật khẩu") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = errorMessage.isNotEmpty()
                    )
                    
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Quên mật khẩu?",
                        fontSize = 13.sp,
                        color = Color(0xFF0D47A1),
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { navController.navigate(Screen.ForgotPassword.route) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                errorMessage = "Vui lòng nhập đầy đủ thông tin"
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                try {
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            isLoading = false
                                            if (task.isSuccessful) {
                                                onLoginSuccess()
                                            } else {
                                                errorMessage = task.exception?.message ?: "Đăng nhập thất bại"
                                            }
                                        }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage = e.message ?: "Lỗi kết nối"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        shape = RoundedCornerShape(26.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Đăng nhập", fontSize = 16.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row {
                        Text("Chưa có tài khoản? ", color = Color.Gray)
                        Text(
                            text = "Đăng ký ngay",
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate(Screen.Register.route) }
                        )
                    }
                }
            }
        }
    }
}