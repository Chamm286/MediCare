package com.example.ncs3.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.outlined.*
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
import com.example.ncs3.data.models.User
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    doctorId: String,
    patientId: String
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var patient by remember { mutableStateOf<User?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    LaunchedEffect(patientId) {
        scope.launch {
            patient = repository.getUser(patientId)
            // TODO: Load messages from Firestore
            // messages = repository.getChatMessages(doctorId, patientId)
            messages = listOf(
                ChatMessage("1", patientId, doctorId, "Chào bác sĩ, tôi bị đau đầu", System.currentTimeMillis() - 3600000),
                ChatMessage("2", doctorId, patientId, "Chào bạn, triệu chứng thế nào?", System.currentTimeMillis() - 1800000),
                ChatMessage("3", patientId, doctorId, "Đau vùng trán, hơi sốt", System.currentTimeMillis() - 900000)
            )
            isLoading = false
            delay(100)
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage() {
        if (inputMessage.isBlank()) return
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = doctorId,
            receiverId = patientId,
            message = inputMessage,
            timestamp = System.currentTimeMillis()
        )
        messages = messages + newMessage
        inputMessage = ""
        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
            // TODO: Save to Firestore
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(patient?.avatar?.ifEmpty { "👤" } ?: "👤", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                patient?.fullName ?: "Bệnh nhân",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Online",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Gọi điện */ }) {
                        Icon(Icons.Outlined.Call, null, tint = Color.White)
                    }
                    IconButton(onClick = { /* Video call */ }) {
                        Icon(Icons.Outlined.Videocam, null, tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0D47A1))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F7FA))
            ) {
                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    state = listState,
                    reverseLayout = false,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(
                            message = message,
                            isMine = message.senderId == doctorId
                        )
                    }
                }

                // Input row
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { /* Thêm ảnh */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Outlined.Add, null, tint = Color(0xFF0D47A1))
                        }

                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Nhập tin nhắn...", fontSize = 13.sp) },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0D47A1),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            singleLine = true
                        )

                        IconButton(
                            onClick = { sendMessage() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D47A1)),
                            enabled = inputMessage.isNotBlank()
                        ) {
                            Icon(
                                Icons.Outlined.Send,
                                null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 250.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMine) 16.dp else 4.dp,
                    bottomEnd = if (isMine) 4.dp else 16.dp
                ),
                color = if (isMine) Color(0xFF0D47A1) else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    message.message,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    color = if (isMine) Color.White else Color(0xFF1A1A2E)
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
            ) {
                Text(
                    formatTime(message.timestamp),
                    fontSize = 9.sp,
                    color = Color.Gray
                )
                if (isMine) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        if (message.isRead) Icons.Outlined.DoneAll else Icons.Outlined.Done,
                        null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Date()
    val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
    return if (date.day == now.day) {
        sdf.format(date)
    } else {
        SimpleDateFormat("dd/MM HH:mm", Locale("vi", "VN")).format(date)
    }
}

val Date.day: Int
    get() = SimpleDateFormat("dd", Locale("vi", "VN")).format(this).toInt()