package com.example.ncs3.ui.screens.doctor

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.repository.MedicareRepository
import com.example.ncs3.ui.components.DoctorBottomNavigation
import com.example.ncs3.ui.components.FloatingAIChatbot
import com.example.ncs3.utils.SharedPrefs
import kotlinx.coroutines.launch

data class DoctorProfileData(
    val name: String = "",
    val title: String = "",
    val specialty: String = "",
    val hospital: String = "",
    val phone: String = "",
    val price: String = "0Ä‘",
    val experienceYears: String = "",
    val rating: String = "5.0",
    val totalPatients: String = "1,200+",
    val biography: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileScreen(
    navController: NavController,
    doctorId: String,
    doctorName: String = ""
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var doctorProfile by remember { mutableStateOf<DoctorProfileData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Báº¢NG MÃ€U PREMIUM TEAL Äá»’NG Bá»˜ 100% Vá»šI DASHBOARD
    val medicarePrimary = Color(0xFF00796B)
    val medicareAccent = Color(0xFF00BFA5)
    val medicareGradient = Brush.verticalGradient(listOf(Color(0xFF004D40), Color(0xFF00695C)))
    val backgroundColor = Color(0xFFF7F9FB)
    val currentRoute = "doctor_profile"

    // ðŸ› ï¸ ÄÆ¯A HÃ€M RA ÄÃ‚Y: Fix triá»‡t Ä‘á»ƒ lá»—i Unresolved reference cho mÃ¡
    fun handleLogout() {
        SharedPrefs.logout()
        navController.navigate("login") {
            popUpTo("doctor_dashboard") { inclusive = true }
        }
    }

    LaunchedEffect(doctorId) {
        scope.launch {
            try {
                doctorProfile = DoctorProfileData(
                    name = doctorName.ifEmpty { "Nguyá»…n VÄƒn HÃ¹ng" },
                    title = "BÃ¡c sÄ© CKII / Tháº¡c sÄ© Y khoa",
                    specialty = "ChuyÃªn khoa Ná»™i Tá»•ng QuÃ¡t",
                    hospital = "Bá»‡nh viá»‡n Äáº¡i há»c Y DÆ°á»£c TP.HCM",
                    phone = SharedPrefs.getUserEmail() ?: "0905 123 456",
                    price = "350.000 Ä‘",
                    experienceYears = "15 nÄƒm",
                    rating = "4.9",
                    totalPatients = "1,450+",
                    biography = "Tá»‘t nghiá»‡p chÃ­nh quy há»‡ Tháº¡c sÄ© táº¡i Äáº¡i há»c Y DÆ°á»£c. Tá»«ng cÃ³ thá»i gian dÃ i tu nghiá»‡p chuyÃªn sÃ¢u vá» phÃ¡c Ä‘á»“ Ä‘iá»u trá»‹ bá»‡nh lÃ½ ná»™i khoa mÃ£n tÃ­nh táº¡i Singapore. ThÃ nh viÃªn Há»™i Ná»™i khoa Viá»‡t Nam."
                )
            } catch (e: Exception) {
                Log.e("DoctorProfile", "Lá»—i: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Há»“ sÆ¡ chuyÃªn mÃ´n", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = { /* Sá»­a thÃ´ng tin */ }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF004D40)),
                modifier = Modifier.shadow(0.dp)
            )
        },
        bottomBar = {
            DoctorBottomNavigation(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = medicarePrimary, strokeWidth = 3.dp)
                }
            } else {
                doctorProfile?.let { profile ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 100.dp)
                    ) {

                        // --- 1. BANNER GRADIENT Ná»€N PHÃ€O ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(medicareGradient)
                        )

                        // --- 2. THÃ”NG TIN BÃC SÄ¨ (HIá»†U á»¨NG CHá»’NG LAYER) ---
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (-60).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar viá»n tráº¯ng ná»•i khá»‘i
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(8.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(4.dp)
                                    .background(medicarePrimary.copy(alpha = 0.1f))
                                    .border(1.dp, medicarePrimary.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.name.take(1).uppercase(),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = medicarePrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "BS. ${profile.name}",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${profile.title} â€¢ ${profile.specialty}",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // --- 3. CHá»ˆ Sá» NHANH Äá»’NG Bá»˜ Äáº¸P ---
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GridStatItem(value = profile.experienceYears, label = "Kinh nghiá»‡m", icon = Icons.Outlined.WorkspacePremium, iconColor = medicarePrimary)
                                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE5E7EB)))
                                    GridStatItem(value = profile.totalPatients, label = "Bá»‡nh nhÃ¢n", icon = Icons.Outlined.People, iconColor = medicareAccent)
                                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE5E7EB)))
                                    GridStatItem(value = profile.rating, label = "ÄÃ¡nh giÃ¡", icon = Icons.Outlined.Star, iconColor = Color(0xFFFFB000))
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // --- 4. THÃ”NG TIN HÃ€NH NGHá»€ CHUYÃŠN NGHIá»†P ---
                            SectionTitle("ThÃ´ng tin hÃ nh nghá»")

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    ModernInfoRow(icon = Icons.Outlined.LocalHospital, label = "CÆ¡ sá»Ÿ cÃ´ng tÃ¡c chÃ­nh", value = profile.hospital)
                                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                                    ModernInfoRow(icon = Icons.Outlined.Payments, label = "Chi phÃ­ khÃ¡m cá»‘ Ä‘á»‹nh", value = profile.price)
                                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                                    ModernInfoRow(icon = Icons.Outlined.AlternateEmail, label = "TÃ i khoáº£n há»‡ thá»‘ng", value = profile.phone)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // --- 5. TIá»‚U Sá»¬ ÄÃ€O Táº O ---
                            SectionTitle("Tiá»ƒu sá»­ & QuÃ¡ trÃ¬nh Ä‘Ã o táº¡o")

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                            ) {
                                Text(
                                    text = profile.biography,
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF4B5563),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // --- 6. NÃšT ÄÄ‚NG XUáº¤T PREMIUM ---
                            OutlinedButton(
                                onClick = { handleLogout() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4D4D)),
                                border = BorderStroke(1.dp, Color(0xFFFF4D4D).copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ÄÄƒng xuáº¥t tÃ i khoáº£n", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // FLOATING AI CHATBOT Äá»’NG Bá»˜ DASHBOARD
            if (!isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)) {
                    FloatingAIChatbot(navController = navController,)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF374151),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun GridStatItem(value: String, label: String, icon: ImageVector, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ModernInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
        }
    }
}
