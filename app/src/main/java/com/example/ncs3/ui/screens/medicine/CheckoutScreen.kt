package com.example.ncs3.ui.screens.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.data.models.CartItem
import com.example.ncs3.data.repository.MedicareRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    userId: String
) {
    val scope = rememberCoroutineScope()
    val repository = remember { MedicareRepository() }

    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf(0) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val paymentMethods = listOf(
        PaymentMethod("💰", "Thanh toán khi nhận hàng", "Trả tiền mặt khi nhận thuốc"),
        PaymentMethod("💳", "Thẻ tín dụng/ghi nợ", "Visa, Mastercard, JCB"),
        PaymentMethod("🏦", "Chuyển khoản ngân hàng", "MB Bank, Vietcombank, Techcombank"),
        PaymentMethod("📱", "Ví điện tử", "MoMo, ZaloPay, ShopeePay")
    )

    LaunchedEffect(Unit) {
        scope.launch {
            cartItems = repository.getCart(userId)
            isLoading = false
        }
    }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val shipping = if (subtotal >= 200000) 0 else 30000
    val total = subtotal + shipping

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Đặt hàng thành công!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cảm ơn bạn đã mua hàng", fontSize = 14.sp, color = Color.Gray)
                    Text("Đơn hàng sẽ được giao trong 2-3 ngày", fontSize = 13.sp, color = Color(0xFF0D47A1))
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tiếp tục mua sắm", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // SỬA: dùng emoji hoặc Icons.Default.ShoppingCart
                    Text("🛒", fontSize = 80.sp)
                    // HOẶC: Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(80.dp), tint = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Giỏ hàng trống", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("Hãy thêm sản phẩm vào giỏ hàng", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate("medicine_store") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Mua sắm ngay", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F7FA))
            ) {
                // Địa chỉ giao hàng
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFF0D47A1))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Địa chỉ giao hàng", fontSize = 12.sp, color = Color.Gray)
                                Text("Nhà thuốc MediCare", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Số 123 Đường Nguyễn Trãi, Quận 1, TP.HCM", fontSize = 12.sp, color = Color.Gray)
                            }
                            Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
                        }
                    }
                }

                // Danh sách sản phẩm
                item {
                    Text(
                        "Đơn hàng (${cartItems.size} sản phẩm)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(cartItems) { item ->
                    OrderItemCard(item)
                }

                // Chi tiết thanh toán
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Chi tiết thanh toán", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tạm tính", fontSize = 13.sp, color = Color.Gray)
                                Text("${subtotal/1000}.000đ", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Phí vận chuyển", fontSize = 13.sp, color = Color.Gray)
                                Text(
                                    if (shipping == 0) "Miễn phí" else "${shipping/1000}.000đ",
                                    fontSize = 13.sp,
                                    color = if (shipping == 0) Color(0xFF4CAF50) else Color.Gray
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tổng cộng", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${total/1000}.000đ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }
                }

                // Phương thức thanh toán
                item {
                    Text(
                        "Phương thức thanh toán",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(paymentMethods.indices.toList()) { index ->
                    val method = paymentMethods[index]
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPaymentMethod == index) Color(0xFFE3F2FD) else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { selectedPaymentMethod = index }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(method.icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(method.title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(method.subtitle, fontSize = 11.sp, color = Color.Gray)
                            }
                            RadioButton(
                                selected = selectedPaymentMethod == index,
                                onClick = { selectedPaymentMethod = index },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                        }
                    }
                }

                // Nút đặt hàng
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            isProcessing = true
                            scope.launch {
                                // Xử lý thanh toán
                                kotlinx.coroutines.delay(1500)
                                // Xóa giỏ hàng
                                for (item in cartItems) {
                                    repository.removeFromCart(item.id)
                                }
                                isProcessing = false
                                showSuccessDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("ĐẶT HÀNG", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(item: CartItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text("💊", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.medicineName, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 2)
                Text("Số lượng: ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                "${(item.price * item.quantity)/1000}.000đ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935)
            )
        }
    }
}

data class PaymentMethod(
    val icon: String,
    val title: String,
    val subtitle: String
)