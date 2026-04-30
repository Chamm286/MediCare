package com.example.ncs3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerMenuDoctor(
    doctorName: String,
    doctorEmail: String,
    onClose: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(0.dp, 24.dp, 24.dp, 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFF1565C0))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍⚕️", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        doctorName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        doctorEmail,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Dashboard,
                    title = "Dashboard",
                    onClick = { onClose(); onNavigateToDashboard() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Person,
                    title = "Hồ sơ của tôi",
                    onClick = { onClose(); onNavigateToProfile() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Lịch làm việc",
                    onClick = { onClose(); onNavigateToSchedule() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.ListAlt,
                    title = "Lịch hẹn",
                    badge = "4",
                    onClick = { onClose(); onNavigateToAppointments() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.People,
                    title = "Bệnh nhân của tôi",
                    onClick = { onClose(); onNavigateToPatients() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.BarChart,
                    title = "Thống kê",
                    onClick = { onClose(); onNavigateToStats() }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Settings,
                    title = "Cài đặt",
                    onClick = { onClose(); onNavigateToSettings() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Logout,
                    title = "Đăng xuất",
                    iconColor = Color(0xFFE53935),
                    textColor = Color(0xFFE53935),
                    onClick = onLogout
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏥 MediCare", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0D47A1))
                    Text("Phiên bản 1.0.0", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItemDoctor(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    badge: String? = null,
    iconColor: Color = Color(0xFF5F6368),
    textColor: Color = Color(0xFF202124),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE53935),
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(badge, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}