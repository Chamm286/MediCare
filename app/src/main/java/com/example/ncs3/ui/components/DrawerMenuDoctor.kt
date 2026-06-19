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
    // Định nghĩa bảng màu Medicare đồng bộ
    val medicarePrimary = Color(0xFF00796B) // Xanh Teal chủ đạo
    val medicareGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF004D40), Color(0xFF00796B))
    )
    val footerTextColor = Color(0xFF00796B)

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = Color.White,
        // Bo góc mềm mại ở cạnh phải của Drawer giống các App hiện đại ngày nay
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ================= HEADER PROFILE BÁC SĨ =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = medicareGradient)
                    .statusBarsPadding() // Tránh bị đè bởi thanh trạng thái hệ thống Android
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Vòng tròn chứa Avatar Bác sĩ (Viền mờ sang trọng)
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .padding(4.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍⚕️", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "BS. $doctorName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = doctorEmail,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= DANH SÁCH MENU ITEMS =================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Khoảng cách đều giữa các mục
            ) {
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Dashboard,
                    title = "Tổng quan (Dashboard)",
                    onClick = { onClose(); onNavigateToDashboard() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Person,
                    title = "Hồ sơ cá nhân",
                    onClick = { onClose(); onNavigateToProfile() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Lịch làm việc",
                    onClick = { onClose(); onNavigateToSchedule() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.ListAlt,
                    title = "Quản lý lịch hẹn",
                    badge = "4", // Số ca đang chờ xử lý
                    badgeColor = Color(0xFFFF5252),
                    onClick = { onClose(); onNavigateToAppointments() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.People,
                    title = "Danh sách bệnh nhân",
                    onClick = { onClose(); onNavigateToPatients() }
                )
                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.BarChart,
                    title = "Báo cáo thống kê",
                    onClick = { onClose(); onNavigateToStats() }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                    color = Color.LightGray.copy(alpha = 0.4f)
                )

                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Settings,
                    title = "Cấu hình hệ thống",
                    onClick = { onClose(); onNavigateToSettings() }
                )

                DrawerMenuItemDoctor(
                    icon = Icons.Outlined.Logout,
                    title = "Đăng xuất tài khoản",
                    iconColor = Color(0xFFFF5252), // Màu đỏ cảnh báo hiện đại
                    textColor = Color(0xFFFF5252),
                    onClick = onLogout
                )
            }

            // ================= FOOTER THƯƠNG HIỆU =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🏥 MediCare",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = footerTextColor
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hệ thống quản lý lâm sàng chuyên nghiệp",
                        fontSize = 10.sp,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Phiên bản ổn định 2026",
                        fontSize = 9.sp,
                        color = Color.LightGray
                    )
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
    badgeColor: Color = Color(0xFFFF5252),
    iconColor: Color = Color(0xFF455A64), // Tăng sắc xám xanh để nhìn sang trọng hơn xám thuần cũ
    textColor: Color = Color(0xFF263238),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // Tạo bo góc khi người dùng bấm (Ripple Effect đẹp hơn)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium, // Đổi từ Normal sang Medium để chữ nét và dễ đọc hơn
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(6.dp), // Dạng hình kẹo (Badge Pill) nhìn hiện đại hơn tròn xoe
                color = badgeColor,
                modifier = Modifier.height(18.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}