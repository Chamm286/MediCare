package com.example.ncs3.ui.screens.profile

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ cá nhân", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(50.dp)),
                color = Color(0xFF0D47A1)
            ) {
                Box(contentAlignment = Alignment.Center) { Text("👤", fontSize = 48.sp) }
            }
            
            Spacer(Modifier.height(16.dp))
            Text("Nguyễn Văn A", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("32 tuổi - Nam", color = Color.Gray)
            Text("Mã số: MC205001234", color = Color.Gray, fontSize = 12.sp)
            
            Spacer(Modifier.height(32.dp))
            
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    ProfileRow(Icons.Default.Email, "Email", "nguyenvana@gmail.com")
                    Divider()
                    ProfileRow(Icons.Default.Phone, "Số điện thoại", "0123456789")
                    Divider()
                    ProfileRow(Icons.Default.CalendarToday, "Ngày sinh", "01/01/1993")
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            Button(
                onClick = { onLogout(); navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                shape = RoundedCornerShape(24.dp)
            ) { Text("Đăng xuất", color = Color.White) }
        }
    }
}

@Composable
fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF0D47A1))
            Spacer(Modifier.width(12.dp))
            Text(title, fontSize = 14.sp)
        }
        Text(value, fontSize = 14.sp, color = Color.Gray)
    }
}
