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
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

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
            Spacer(modifier = Modifier.height(40.dp))
            
            Text("🏥", fontSize = 50.sp)
            Text(
                text = "Tạo tài khoản mới",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it; errorMessage = "" },
                        label = { Text("Họ tên") }, leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = email, onValueChange = { email = it; errorMessage = "" },
                        label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it; errorMessage = "" },
                        label = { Text("Số điện thoại") }, leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = password, onValueChange = { password = it; errorMessage = "" },
                        label = { Text("Mật khẩu") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it; errorMessage = "" },
                        label = { Text("Xác nhận mật khẩu") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            when {
                                fullName.isEmpty() -> errorMessage = "Vui lòng nhập họ tên"
                                email.isEmpty() -> errorMessage = "Vui lòng nhập email"
                                password.isEmpty() -> errorMessage = "Vui lòng nhập mật khẩu"
                                password != confirmPassword -> errorMessage = "Mật khẩu không khớp"
                                else -> {
                                    isLoading = true
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val user = hashMapOf(
                                                    "uid" to auth.currentUser?.uid,
                                                    "fullName" to fullName,
                                                    "email" to email,
                                                    "phone" to phone,
                                                    "createdAt" to System.currentTimeMillis()
                                                )
                                                firestore.collection("users").document(auth.currentUser?.uid!!)
                                                    .set(user).addOnSuccessListener {
                                                        isLoading = false
                                                        navController.navigate(Screen.Login.route)
                                                    }
                                            } else {
                                                isLoading = false
                                                errorMessage = task.exception?.message ?: "Đăng ký thất bại"
                                            }
                                        }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        shape = RoundedCornerShape(26.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Đăng ký", fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row {
                        Text("Đã có tài khoản? ", color = Color.Gray)
                        Text("Đăng nhập", color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate(Screen.Login.route) })
                    }
                }
            }
        }
    }
}