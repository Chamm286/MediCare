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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF0097A7)
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
            Spacer(modifier = Modifier.height(80.dp))
            
            Text("🔐", fontSize = 60.sp)
            Text(
                text = "Quên mật khẩu?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Nhập email của bạn, chúng tôi sẽ gửi link đặt lại mật khẩu",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
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
                        onValueChange = { email = it; message = ""; isError = false },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = isError
                    )
                    
                    if (message.isNotEmpty()) {
                        Text(
                            text = message,
                            color = if (isError) Color.Red else Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (email.isEmpty()) {
                                message = "Vui lòng nhập email"
                                isError = true
                                return@Button
                            }
                            isLoading = true
                            auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        message = "Link đặt lại mật khẩu đã được gửi đến email của bạn"
                                        isError = false
                                    } else {
                                        message = task.exception?.message ?: "Gửi email thất bại"
                                        isError = true
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
                            Text("Gửi link đặt lại", fontSize = 16.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Quay lại đăng nhập",
                        color = Color(0xFF0D47A1),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { navController.navigate(Screen.Login.route) }
                    )
                }
            }
        }
    }
}