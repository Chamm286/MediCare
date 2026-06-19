package com.example.ncs3.ui.screens.medicine

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ncs3.data.models.CartItem
import com.example.ncs3.data.repository.MedicareRepository
import com.google.firebase.auth.FirebaseAuth // 🔥 Thêm Auth để check tài khoản đăng nhập
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class PaymentMethod(
    val icon: String,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    userId: String // Dùng ID truyền từ Nav, hoặc tự động bốc từ FirebaseAuth bên dưới
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() } // 🔥 Lấy instance Auth

    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf(0) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 🔥 CÁC BIẾN LƯU THÔNG TIN KHÁCH HÀNG THỰC TẾ
    var customerName by remember { mutableStateOf("Đang tải tên...") }
    var customerPhone by remember { mutableStateOf("Đang tải SĐT...") }
    var deliveryAddress by remember { mutableStateOf("Đang tải địa chỉ nhận hàng...") }

    // Hệ màu thương mại cao cấp
    val primaryBlue = Color(0xFF0D47A1)
    val lightBg = Color(0xFFF8FAFC)
    val accentRed = Color(0xFFE53935)
    val cardBorderColor = Color(0xFFE2E8F0)

    val paymentMethods = listOf(
        PaymentMethod("💰", "Thanh toán khi nhận hàng (COD)", "Trả tiền mặt khi nhận thuốc tận nơi"),
        PaymentMethod("💳", "Thẻ tín dụng / Ghi nợ", "Hỗ trợ Visa, Mastercard, JCB qua cổng"),
        PaymentMethod("🏦", "Chuyển khoản Ngân hàng nội địa", "Quét mã QR tự động qua Napas247"),
        PaymentMethod("📱", "Ví điện tử MoMo / ZaloPay", "Ứng dụng tự động liên kết tài khoản")
    )

    // 🔥 ĐỒNG BỘ LUỒNG DỮ LIỆU ĐĂNG NHẬP THỰC TẾ TỪ FIREBASE
    LaunchedEffect(userId) {
        val currentUserId = userId.ifEmpty { auth.currentUser?.uid ?: "" }

        if (currentUserId.isNotEmpty()) {
            try {
                // 1. Tải danh sách giỏ hàng như cũ
                cartItems = repository.getCart(currentUserId)

                // 2. Bốc thông tin Profile thật của User từ bảng 'users'
                val userDoc = db.collection("users").document(currentUserId).get().await()
                if (userDoc.exists()) {
                    customerName = userDoc.getString("fullName") ?: userDoc.getString("name") ?: "Khách hàng vô danh"
                    customerPhone = userDoc.getString("phoneNumber") ?: userDoc.getString("phone") ?: "Chưa cập nhật SĐT"
                    deliveryAddress = userDoc.getString("address") ?: "Chưa có địa chỉ, vui lòng bổ sung"
                } else {
                    // Dự phòng nếu tài khoản đăng nhập qua Email/Google nhưng chưa tạo Doc bên bảng 'users'
                    customerName = auth.currentUser?.displayName ?: "Người dùng MediCare"
                    customerPhone = auth.currentUser?.phoneNumber ?: "Chưa có SĐT"
                    deliveryAddress = "Chưa thiết lập địa chỉ giao thuốc"
                }

            } catch (e: Exception) {
                Log.e("Checkout_Fetch_User_Error", "Lỗi lấy thông tin khách hàng: ${e.message}")
                errorMessage = "Không thể tải thông tin trang cá nhân."
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
            errorMessage = "Lỗi: Bạn chưa đăng nhập hệ thống!"
        }
    }

    val subtotal = cartItems.sumOf { item -> item.price * item.quantity }
    val shippingFee = if (subtotal >= 200000 || subtotal == 0) 0 else 30000
    val totalAmount = subtotal + shippingFee

    // Dialog thông báo đặt hàng thành công
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Brush.linearGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))), CircleShape)
                            .shadow(4.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đặt Hàng Thành Công!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Đơn hàng đã gửi đến hiệu thuốc xử lý.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Người nhận: $customerName", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = primaryBlue)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("medicine_store") {
                            popUpTo("checkout") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Tiếp tục mua sắm", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        )
    }

    Scaffold(
        containerColor = lightBg,
        topBar = {
            TopAppBar(
                title = { Text("Xác nhận thanh toán", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            }
            cartItems.isEmpty() && errorMessage == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Outlined.ShoppingBag, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Giỏ hàng đang trống", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.navigate("medicine_store") }, colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)) {
                            Text("Quay lại cửa hàng", color = Color.White)
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // SECTION 1: ĐỊA CHỈ THỰC CỦA KHÁCH HÀNG ĐÃ ĐĂNG NHẬP
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, cardBorderColor),
                            modifier = Modifier.fillMaxWidth().padding(16.dp).shadow(2.dp, RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(44.dp).background(primaryBlue.copy(alpha = 0.08f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.LocationOn, null, tint = primaryBlue, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ĐỊA CHỈ NHẬN THUỐC THỰC TẾ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "$customerName • $customerPhone", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = deliveryAddress, fontSize = 13.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
                            }
                        }
                    }

                    // SECTION 2: SẢN PHẨM MUA
                    item {
                        Text(
                            text = "Sản phẩm trong đơn hàng (${cartItems.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    items(cartItems) { item ->
                        CheckoutItemRow(item = item)
                    }

                    // SECTION 3: PHƯƠNG THỨC THANH TOÁN
                    item {
                        Text(
                            text = "Phương thức thanh toán",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(paymentMethods.indices.toList()) { index ->
                        val method = paymentMethods[index]
                        val isSelected = selectedPaymentMethod == index
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.5.dp, if (isSelected) primaryBlue else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .shadow(1.dp, RoundedCornerShape(14.dp))
                                .clickable { selectedPaymentMethod = index }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(42.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(method.icon, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(method.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(method.subtitle, fontSize = 11.sp, color = Color.Gray)
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = primaryBlue)
                                )
                            }
                        }
                    }

                    // SECTION 4: CHI TIẾT DÒNG TIỀN
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, cardBorderColor),
                            modifier = Modifier.fillMaxWidth().padding(16.dp).shadow(2.dp, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.CreditCard, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chi tiết hóa đơn thanh toán", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                }
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tạm tính tiền thuốc", fontSize = 13.sp, color = Color.Gray)
                                    Text("${String.format("%,d", subtotal)}đ", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Phí giao hàng siêu tốc (2h)", fontSize = 13.sp, color = Color.Gray)
                                    Text(
                                        text = if (shippingFee == 0) "Miễn phí" else "${String.format("%,d", shippingFee)}đ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (shippingFee == 0) Color(0xFF2E7D32) else Color(0xFF1E293B)
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 14.dp))

                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Tổng tiền thanh toán", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Text(
                                        text = "${String.format("%,d", totalAmount)}đ",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = accentRed
                                    )
                                }
                            }
                        }
                    }

                    // HIỂN THỊ ERROR
                    item {
                        AnimatedVisibility(visible = errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = accentRed,
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // SECTION 5: NÚT ĐẶT HÀNG (ĐẨY THÔNG TIN REAL LÊN DATABASE)
                    item {
                        Button(
                            onClick = {
                                val finalUserId = userId.ifEmpty { auth.currentUser?.uid ?: "" }
                                if (finalUserId.isEmpty()) {
                                    errorMessage = "Lỗi bảo mật: Vui lòng đăng nhập lại để đặt đơn!"
                                    return@Button
                                }

                                isProcessing = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val timestamp = System.currentTimeMillis()

                                        // A. Đẩy thông tin THẬT lên bảng orders
                                        val orderMap = hashMapOf(
                                            "patientId" to finalUserId,
                                            "patientName" to customerName,
                                            "patientPhone" to customerPhone,
                                            "address" to deliveryAddress,
                                            "paymentMethod" to paymentMethods[selectedPaymentMethod].title,
                                            "subtotal" to subtotal,
                                            "shippingFee" to shippingFee,
                                            "totalAmount" to totalAmount,
                                            "status" to "Pending",
                                            "createdAt" to timestamp,
                                            "items" to cartItems.map { item ->
                                                hashMapOf(
                                                    "medicineId" to item.medicineId,
                                                    "medicineName" to item.medicineName,
                                                    "price" to item.price,
                                                    "quantity" to item.quantity,
                                                    "imageUrl" to item.imageUrl
                                                )
                                            }
                                        )
                                        val orderRef = db.collection("orders").add(orderMap).await()

                                        // B. Bắn thông báo thời gian thực về máy Admin
                                        val adminNotificationMap = hashMapOf(
                                            "title" to "Có đơn hàng thuốc mới!",
                                            "body" to "Khách $customerName vừa đặt đơn thuốc trị giá ${String.format("%,d", totalAmount)}đ",
                                            "orderId" to orderRef.id,
                                            "type" to "new_order",
                                            "isRead" to false,
                                            "createdAt" to timestamp
                                        )
                                        db.collection("admin_notifications").add(adminNotificationMap).await()

                                        // C. Xóa giỏ hàng
                                        for (item in cartItems) {
                                            repository.removeFromCart(item.id)
                                        }

                                        showSuccessDialog = true

                                    } catch (e: Exception) {
                                        errorMessage = "Lỗi hệ thống: ${e.localizedMessage}"
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
                            } else {
                                Text("TIẾN HÀNH ĐẶT HÀNG", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutItemRow(item: CartItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).shadow(0.5.dp, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.medicineName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("💊", fontSize = 24.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.medicineName, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Đơn giá: ${String.format("%,d", item.price)}đ  •  SL: ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${String.format("%,d", item.price * item.quantity)}đ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
    }
}