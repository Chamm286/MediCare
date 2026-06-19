package com.example.ncs3.ui.screens.doctor

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.Appointment
import com.example.ncs3.ui.components.DoctorBottomNavigation
import com.example.ncs3.ui.components.FloatingAIChatbot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    navController: NavController,
    doctorId: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("Táº¥t cáº£") }
    var expandedAppointmentId by remember { mutableStateOf<String?>(null) }

    val filters = listOf("Táº¥t cáº£", "Chá» duyá»‡t", "ÄÃ£ xÃ¡c nháº­n", "ÄÃ£ khÃ¡m")

    val medicarePrimary = Color(0xFF00796B)
    val backgroundColor = Color(0xFFF4F6F8)
    val currentRoute = "doctor_appointments"

    fun fetchAppointments() {
        scope.launch {
            try {
                isLoading = true
                val snapshot = db.collection("appointments")
                    .whereEqualTo("doctorId", doctorId)
                    .get()
                    .await()

                appointments = snapshot.documents.mapNotNull { doc ->
                    val appointment = doc.toObject(Appointment::class.java)
                    appointment?.copy(id = doc.id)
                }.sortedByDescending { it.createdAt }

            } catch (e: Exception) {
                Log.e("FirebaseData", "Lá»—i táº£i lá»‹ch háº¹n: ${e.message}")
                Toast.makeText(context, "KhÃ´ng thá»ƒ táº£i dá»¯ liá»‡u tá»« Firebase", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: String) {
        scope.launch {
            try {
                db.collection("appointments")
                    .document(appointmentId)
                    .update("status", newStatus)
                    .await()

                Toast.makeText(context, "Cáº­p nháº­t tráº¡ng thÃ¡i thÃ nh cÃ´ng!", Toast.LENGTH_SHORT).show()
                fetchAppointments()
            } catch (e: Exception) {
                Toast.makeText(context, "Thao tÃ¡c tháº¥t báº¡i: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(doctorId) {
        fetchAppointments()
    }

    val filteredAppointments = when (selectedFilter) {
        "Chá» duyá»‡t" -> appointments.filter { it.status == "pending" }
        "ÄÃ£ xÃ¡c nháº­n" -> appointments.filter { it.status == "confirmed" }
        "ÄÃ£ khÃ¡m" -> appointments.filter { it.status == "completed" }
        else -> appointments.filter { it.status != "cancelled" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quáº£n lÃ½ lá»‹ch háº¹n", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Dá»¯ liá»‡u trá»±c tuyáº¿n Cloud Firestore", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF004D40)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "ThoÃ¡t", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { fetchAppointments() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Táº£i láº¡i", tint = Color.White)
                    }
                }
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
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = medicarePrimary, strokeWidth = 3.dp)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Bá»™ lá»c tráº¡ng thÃ¡i Custom Chip báº±ng Box (An toÃ n tuyá»‡t Ä‘á»‘i)
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            val isSelected = selectedFilter == filter

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) medicarePrimary.copy(alpha = 0.12f) else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) medicarePrimary else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) medicarePrimary else Color(0xFF6B7280)
                                )
                            }
                        }
                    }

                    Text(
                        text = "TÃ¬m tháº¥y ${filteredAppointments.size} lá»‹ch háº¹n bá»‡nh nhÃ¢n",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )

                    if (filteredAppointments.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.EventBusy, null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("KhÃ´ng cÃ³ dá»¯ liá»‡u lá»‹ch háº¹n nÃ o", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredAppointments) { appointment ->
                                val isExpanded = expandedAppointmentId == appointment.id

                                InteractiveAppointmentCard(
                                    appointment = appointment,
                                    isExpanded = isExpanded,
                                    medicarePrimary = medicarePrimary,
                                    onCardClick = {
                                        expandedAppointmentId = if (isExpanded) null else appointment.id
                                    },
                                    onConfirm = { updateAppointmentStatus(appointment.id, "confirmed") },
                                    onCancel = { updateAppointmentStatus(appointment.id, "cancelled") },
                                    onComplete = { updateAppointmentStatus(appointment.id, "completed") }
                                )
                            }
                        }
                    }
                }
            }

            if (!isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)) {
                    FloatingAIChatbot(navController = navController,)
                }
            }
        }
    }
}

@Composable
fun InteractiveAppointmentCard(
    appointment: Appointment,
    isExpanded: Boolean,
    medicarePrimary: Color,
    onCardClick: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(medicarePrimary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appointment.patientName.ifEmpty { "P" }.take(1).uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = medicarePrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.patientName.ifEmpty { "Bá»‡nh nhÃ¢n há»‡ thá»‘ng" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccessTime, null, tint = medicarePrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${appointment.date}  â€¢  ${appointment.timeSlot}",
                            fontSize = 12.sp,
                            color = medicarePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (appointment.status) {
                        "pending" -> Color(0xFFFFF3E0)
                        "confirmed" -> Color(0xFFE8F5E9)
                        "completed" -> Color(0xFFE3F2FD)
                        else -> Color(0xFFFFEBEE)
                    }
                ) {
                    Text(
                        text = when (appointment.status) {
                            "pending" -> "Chá» duyá»‡t"
                            "confirmed" -> "ÄÃ£ duyá»‡t"
                            "completed" -> "ÄÃ£ khÃ¡m"
                            else -> "ÄÃ£ há»§y"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (appointment.status) {
                            "pending" -> Color(0xFFE65100)
                            "confirmed" -> Color(0xFF2E7D32)
                            "completed" -> Color(0xFF1565C0)
                            else -> Color(0xFFC62828)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (!isExpanded && appointment.symptoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Triá»‡u chá»©ng: ${appointment.symptoms}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 56.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // ðŸ› ï¸ FIX DÃ’NG NÃ€Y: DÃ¹ng Box váº½ gáº¡ch ngang thay tháº¿ HorizontalDivider Ä‘á»ƒ trÃ¡nh lá»—i version chÃ©o
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .height(1.dp)
                            .background(Color(0xFFF0F0F0))
                    )

                    if (appointment.patientEmail.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                            Icon(Icons.Outlined.Mail, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Email liÃªn há»‡: ${appointment.patientEmail}", fontSize = 12.sp, color = Color(0xFF4B5563))
                        }
                    }

                    Text(
                        text = "ðŸ“‹ Triá»‡u chá»©ng bá»‡nh nhÃ¢n khai bÃ¡o:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = appointment.symptoms.ifEmpty { "KhÃ´ng cÃ³ mÃ´ táº£ triá»‡u chá»©ng Ä‘Ã­nh kÃ¨m." },
                        fontSize = 13.sp,
                        color = Color(0xFF1F2937),
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (appointment.patientEmail.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${appointment.patientEmail}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Medicare - Pháº£n há»“i lá»‹ch háº¹n khÃ¡m bá»‡nh")
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Gá»­i Email pháº£n há»“i..."))
                                } else {
                                    Toast.makeText(context, "KhÃ´ng tÃ¬m tháº¥y thÃ´ng tin email bá»‡nh nhÃ¢n", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(0.45f)
                                .height(40.dp)
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp)),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = medicarePrimary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ContactMail, contentDescription = "Mail", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LiÃªn há»‡", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        if (appointment.status == "pending") {
                            Button(
                                onClick = onCancel,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F0), contentColor = Color(0xFFF5222D)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.4f).height(40.dp)
                            ) {
                                Text("Tá»« chá»‘i", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onConfirm,
                                colors = ButtonDefaults.buttonColors(containerColor = medicarePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.45f).height(40.dp)
                            ) {
                                Text("XÃ¡c nháº­n", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else if (appointment.status == "confirmed") {
                            Button(
                                onClick = onComplete,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().weight(1f).height(40.dp)
                            ) {
                                Icon(Icons.Outlined.CheckCircleOutline, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ÄÃ¡nh dáº¥u hoÃ n thÃ nh ca khÃ¡m", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
