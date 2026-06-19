package com.example.ncs3.ui.screens.doctor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    @field:JvmField val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    doctorId: String,
    patientId: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var patient by remember { mutableStateOf<User?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scrollState = rememberScrollState() // Quản lý trạng thái cuộn ngang của Chips gợi ý

    // Bảng màu thiết kế cao cấp đồng bộ hình ảnh mẫu bạn cung cấp
    val medicareDeepColors = listOf(Color(0xFF1A237E), Color(0xFF0D47A1))
    val medicarePrimary = Color(0xFF1A237E)
    val medicareGradient = Brush.linearGradient(medicareDeepColors)
    val chatBackgroundColor = Color(0xFFEDF2F7)

    // Đồng bộ Realtime từ Firestore API
    LaunchedEffect(patientId) {
        db.collection("users").document(patientId)
            .get()
            .addOnSuccessListener { doc -> if (doc.exists()) patient = doc.toObject(User::class.java) }
    }

    LaunchedEffect(doctorId, patientId) {
        val chatRoomId = if (doctorId < patientId) "${doctorId}_${patientId}" else "${patientId}_${doctorId}"
        db.collection("chats").document(chatRoomId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    messages = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    isLoading = false
                    if (messages.isNotEmpty()) {
                        scope.launch { listState.animateScrollToItem(messages.size - 1) }
                    }
                }
            }
    }

    fun sendMessage(textToSend: String = inputMessage) {
        if (textToSend.isBlank()) return
        val chatRoomId = if (doctorId < patientId) "${doctorId}_${patientId}" else "${patientId}_${doctorId}"
        val messageText = textToSend.trim()
        if (textToSend == inputMessage) inputMessage = ""

        val messageId = db.collection("chats").document(chatRoomId).collection("messages").document().id
        val newMessage = ChatMessage(messageId, doctorId, patientId, messageText, System.currentTimeMillis(), false)

        db.collection("chats").document(chatRoomId).collection("messages").document(messageId).set(newMessage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val patientName = patient?.fullName ?: "AI MediCare"
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(medicareGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(patientName.take(1).uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(patientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Trực tuyến", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Call, null, tint = Color.White) }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Videocam, null, tint = Color.White) }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(Color.Transparent)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(60.dp),
                    color = Color(0xFF1A237E),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {}) {
                            Icon(Icons.Outlined.Home, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                            Text("Home", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {}) {
                            Icon(Icons.Outlined.FavoriteBorder, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                            Text("Yêu thích", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.width(56.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {}) {
                            Icon(Icons.Outlined.Person, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                            Text("Hồ sơ", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {}) {
                            Icon(Icons.Outlined.MenuBook, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("Tư vấn", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .align(Alignment.TopCenter)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(medicareGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Explore, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                CircularProgressIndicator(color = medicarePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(chatBackgroundColor)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        ChatBubbleModern(message = message, isMine = message.senderId == doctorId, primaryColor = medicarePrimary)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 8.dp)
                ) {
                    // SỬA LỖI: Sử dụng modifier chuẩn horizontalScroll(scrollState) kế thừa từ androidx.compose.foundation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuggestionChipModern(text = "📅 Đặt lịch khám") { sendMessage("Tôi muốn hướng dẫn đặt lịch khám lâm sàng.") }
                        SuggestionChipModern(text = "👨‍⚕️ Tìm bác sĩ") { sendMessage("Vui lòng tìm giúp tôi bác sĩ chuyên khoa phù hợp.") }
                        SuggestionChipModern(text = "💊 Tư vấn thuốc") { sendMessage("Tôi cần tư vấn thêm về đơn thuốc hiện tại.") }
                    }

                    Row(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .imePadding()
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.AddCircleOutline, null, tint = medicarePrimary, modifier = Modifier.size(26.dp))
                        }

                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Nhập câu hỏi hoặc nội dung tư vấn...", fontSize = 13.sp, color = Color.Gray) },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = medicarePrimary,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { sendMessage() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (inputMessage.isNotBlank()) medicarePrimary else Color(0xFFE2E8F0)),
                            enabled = inputMessage.isNotBlank()
                        ) {
                            Icon(Icons.Filled.Send, null, modifier = Modifier.size(16.dp), tint = if (inputMessage.isNotBlank()) Color.White else Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleModern(message: ChatMessage, isMine: Boolean, primaryColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 270.dp),
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (isMine) 18.dp else 4.dp,
                    bottomEnd = if (isMine) 4.dp else 18.dp
                ),
                color = if (isMine) primaryColor else Color.White,
                modifier = Modifier.shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 18.dp, topEnd = 18.dp,
                        bottomStart = if (isMine) 18.dp else 4.dp,
                        bottomEnd = if (isMine) 4.dp else 18.dp
                    )
                )
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = if (isMine) Color.White else Color(0xFF1A202C)
                )
            }
            // SỬA LỖI: formatTime giờ đã nằm độc lập ngoài cấp class Composable giúp trình biên dịch nhận diện chính xác
            Text(
                text = formatTime(message.timestamp),
                fontSize = 9.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun SuggestionChipModern(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(34.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF0F4F8),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(text = text, fontSize = 12.sp, color = Color(0xFF1A237E), fontWeight = FontWeight.Medium)
        }
    }
}

// KHU VỰC ĐỊNH NGHĨA HÀM TIỆN ÍCH TIME - SỬA SẠCH LỖI UNRESOLVED REFERENCE
fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val calendarNow = Calendar.getInstance()
    val calendarMsg = Calendar.getInstance().apply { time = date }

    val isToday = calendarNow.get(Calendar.YEAR) == calendarMsg.get(Calendar.YEAR) &&
            calendarNow.get(Calendar.DAY_OF_YEAR) == calendarMsg.get(Calendar.DAY_OF_YEAR)

    val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
    return if (isToday) {
        sdf.format(date)
    } else {
        SimpleDateFormat("dd/MM HH:mm", Locale("vi", "VN")).format(date)
    }
}