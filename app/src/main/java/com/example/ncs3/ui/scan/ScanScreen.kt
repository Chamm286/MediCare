package com.example.ncs3.ui.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume

// ========== DỮ LIỆU QR ==========
sealed class QRCodeResult {
    data class BankTransfer(
        val bankName: String,
        val accountNumber: String,
        val accountName: String,
        val amount: String,
        val content: String
    ) : QRCodeResult()

    data class MediCarePayment(
        val orderId: String,
        val amount: Double,
        val customerName: String
    ) : QRCodeResult()

    data class MediCareBooking(
        val bookingId: String,
        val doctorName: String,
        val appointmentDate: String
    ) : QRCodeResult()

    data class DoctorInfo(
        val doctorId: String,
        val doctorName: String
    ) : QRCodeResult()

    data class Unknown(val rawValue: String) : QRCodeResult()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var scannedCode by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(true) }
    var torchEnabled by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var qrResult by remember { mutableStateOf<QRCodeResult?>(null) }
    var isCameraReady by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageAnalysis by remember { mutableStateOf<ImageAnalysis?>(null) }
    var cameraExecutor by remember { mutableStateOf<ExecutorService?>(null) }

    val barcodeScanner = remember { BarcodeScanning.getClient() }

    // Animation
    val scanLineOffset by animateFloatAsState(
        targetValue = if (isScanning && isCameraReady) 220f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    val pulseAlpha by animateFloatAsState(
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // ========== PHÂN TÍCH QR CODE ==========
    fun parseQRCode(rawValue: String): QRCodeResult {
        return try {
            when {
                // QR ngân hàng - Định dạng: bank|Vietcombank|123456789|NGUYEN VAN A|100000|Noi dung
                rawValue.startsWith("bank|") -> {
                    val parts = rawValue.split("|")
                    if (parts.size >= 6) {
                        QRCodeResult.BankTransfer(
                            bankName = parts[1],
                            accountNumber = parts[2],
                            accountName = parts[3],
                            amount = parts[4],
                            content = parts[5]
                        )
                    } else {
                        QRCodeResult.Unknown(rawValue)
                    }
                }
                // QR MediCare Payment
                rawValue.startsWith("MEDICARE|payment|") -> {
                    val parts = rawValue.split("|")
                    if (parts.size >= 4) {
                        QRCodeResult.MediCarePayment(
                            orderId = parts[2],
                            amount = parts.getOrNull(3)?.toDoubleOrNull() ?: 0.0,
                            customerName = parts.getOrNull(4) ?: ""
                        )
                    } else {
                        QRCodeResult.Unknown(rawValue)
                    }
                }
                // QR MediCare Booking
                rawValue.startsWith("MEDICARE|booking|") -> {
                    val parts = rawValue.split("|")
                    if (parts.size >= 5) {
                        QRCodeResult.MediCareBooking(
                            bookingId = parts[2],
                            doctorName = parts.getOrNull(3) ?: "",
                            appointmentDate = parts.getOrNull(4) ?: ""
                        )
                    } else {
                        QRCodeResult.Unknown(rawValue)
                    }
                }
                // QR Bác sĩ
                rawValue.startsWith("MEDICARE|doctor|") -> {
                    val parts = rawValue.split("|")
                    QRCodeResult.DoctorInfo(
                        doctorId = parts.getOrNull(2) ?: "",
                        doctorName = parts.getOrNull(3) ?: ""
                    )
                }
                // QR chuyển tiền ngân hàng (định dạng phổ biến)
                rawValue.contains("acqId") && rawValue.contains("amount") -> {
                    // Parse QR code theo chuẩn VietQR
                    val params = rawValue.split("&").associate {
                        val kv = it.split("=")
                        if (kv.size == 2) kv[0] to kv[1] else "" to ""
                    }
                    QRCodeResult.BankTransfer(
                        bankName = params["acqName"] ?: "Ngân hàng",
                        accountNumber = params["accountNo"] ?: "",
                        accountName = params["accountName"] ?: "",
                        amount = params["amount"] ?: "0",
                        content = params["desc"] ?: ""
                    )
                }
                else -> QRCodeResult.Unknown(rawValue)
            }
        } catch (e: Exception) {
            QRCodeResult.Unknown(rawValue)
        }
    }

    // Hàm quét QR từ bitmap
    suspend fun scanQRFromBitmap(bitmap: Bitmap): String? {
        return suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    continuation.resume(barcodes.firstOrNull()?.rawValue)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    // Chọn ảnh từ thư viện
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isLoading = true
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val result = scanQRFromBitmap(bitmap)
                    isLoading = false
                    if (!result.isNullOrEmpty()) {
                        scannedCode = result
                        isScanning = false
                        qrResult = parseQRCode(result)
                        showResultDialog = true
                    } else {
                        qrResult = QRCodeResult.Unknown("")
                        showResultDialog = true
                    }
                } catch (e: Exception) {
                    isLoading = false
                    qrResult = QRCodeResult.Unknown("")
                    showResultDialog = true
                }
            }
        }
    }

    fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    // Yêu cầu quyền camera
    if (!hasCameraPermission) {
        PermissionDialog(
            title = "Yêu cầu quyền Camera",
            message = "Ứng dụng cần quyền truy cập camera để quét mã QR.",
            onRequestPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val activity = context as? androidx.activity.ComponentActivity
                    activity?.requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
                }
            },
            onDismiss = { navController.navigateUp() }
        )
        return
    }

    // Setup camera
    DisposableEffect(Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                isCameraReady = true

                val preview = Preview.Builder().build()

                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis?.setAnalyzer(cameraExecutor!!) { imageProxy ->
                    if (isScanning && scannedCode.isEmpty() && isCameraReady) {
                        scanBarcode(imageProxy, barcodeScanner) { result ->
                            if (result != null && scannedCode.isEmpty()) {
                                scannedCode = result
                                isScanning = false
                                qrResult = parseQRCode(result)
                                showResultDialog = true
                            }
                        }
                    } else {
                        imageProxy.close()
                    }
                }

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
                isCameraReady = false
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraExecutor?.shutdown()
            barcodeScanner.close()
        }
    }

    // Dialog kết quả theo từng loại QR
    if (showResultDialog && qrResult != null) {
        when (val result = qrResult!!) {
            is QRCodeResult.BankTransfer -> {
                BankTransferDialog(
                    result = result,
                    onDismiss = {
                        showResultDialog = false
                        scannedCode = ""
                        isScanning = true
                    },
                    onConfirm = {
                        showResultDialog = false
                        scannedCode = ""
                        isScanning = true
                        // TODO: Mở ứng dụng ngân hàng hoặc chuyển sang màn hình chuyển khoản
                    }
                )
            }
            is QRCodeResult.MediCarePayment -> {
                PaymentDialog(
                    result = result,
                    onDismiss = {
                        showResultDialog = false
                        scannedCode = ""
                        isScanning = true
                    },
                    onConfirm = {
                        showResultDialog = false
                        navController.navigate("checkout")
                    }
                )
            }
            is QRCodeResult.MediCareBooking -> {
                BookingDialog(
                    result = result,
                    onDismiss = {
                        showResultDialog = false
                        scannedCode = ""
                        isScanning = true
                    },
                    onConfirm = {
                        showResultDialog = false
                        navController.navigate("booking/${result.bookingId}")
                    }
                )
            }
            is QRCodeResult.DoctorInfo -> {
                DoctorInfoDialog(
                    result = result,
                    onDismiss = {
                        showResultDialog = false
                        scannedCode = ""
                        isScanning = true
                    },
                    onConfirm = {
                        showResultDialog = false
                        navController.navigate("doctor_detail/${result.doctorId}")
                    }
                )
            }
            is QRCodeResult.Unknown -> {
                UnknownResultDialog(
                    rawValue = result.rawValue,
                    onDismiss = {
                        showResultDialog = false
                        scannedCode = ""
                        isScanning = true
                    }
                )
            }
        }
    }

    // UI chính
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quét mã QR", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                        Text("Quét mã thanh toán, đặt lịch, chuyển khoản", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { torchEnabled = !torchEnabled }) {
                        Icon(
                            if (torchEnabled) Icons.Filled.FlashOn else Icons.Outlined.FlashOff,
                            null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Camera preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }
            )

            // Lớp phủ mờ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            // Khung quét
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .border(2.dp, Color.White, RoundedCornerShape(24.dp))
                        .background(Color.Transparent)
                ) {
                    val cornerSize = 30.dp
                    val cornerWidth = 4.dp

                    Box(modifier = Modifier.size(cornerSize, cornerWidth).align(Alignment.TopStart).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerWidth, cornerSize).align(Alignment.TopStart).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerSize, cornerWidth).align(Alignment.TopEnd).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerWidth, cornerSize).align(Alignment.TopEnd).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerSize, cornerWidth).align(Alignment.BottomStart).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerWidth, cornerSize).align(Alignment.BottomStart).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerSize, cornerWidth).align(Alignment.BottomEnd).background(Color(0xFF0D47A1)))
                    Box(modifier = Modifier.size(cornerWidth, cornerSize).align(Alignment.BottomEnd).background(Color(0xFF0D47A1)))
                }

                // Vạch quét
                Box(
                    modifier = Modifier
                        .width(260.dp)
                        .height(3.dp)
                        .offset(y = (scanLineOffset - 140).dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF0D47A1).copy(alpha = pulseAlpha), Color.Transparent)
                            )
                        )
                )
            }

            // Nút chọn ảnh
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .clickable { openGallery() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chọn ảnh chứa mã QR", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Đang xử lý...", color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            if (!isCameraReady && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Đang khởi tạo camera...", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// ========== DIALOG CHO TỪNG LOẠI QR ==========

@Composable
fun BankTransferDialog(
    result: QRCodeResult.BankTransfer,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Thông tin chuyển khoản", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2C3E))
            }
        },
        text = {
            Column {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🏦 NGÂN HÀNG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text(result.bankName, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("💳 SỐ TÀI KHOẢN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text(result.accountNumber, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("👤 CHỦ TÀI KHOẢN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text(result.accountName, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("💰 SỐ TIỀN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text("${formatCurrency(result.amount.toDoubleOrNull() ?: 0.0)}đ", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFFD32F2F))

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("📝 NỘI DUNG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text(result.content, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tiếp tục", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Đóng", color = Color.Gray)
            }
        }
    )
}

@Composable
fun PaymentDialog(
    result: QRCodeResult.MediCarePayment,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF0D47A1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Payment, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Xác nhận thanh toán", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2C3E))
            }
        },
        text = {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mã đơn hàng", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.orderId, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Khách hàng", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.customerName.ifEmpty { "Khách lẻ" }, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Số tiền", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(formatCurrency(result.amount), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xác nhận thanh toán", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Hủy", color = Color.Gray)
            }
        }
    )
}

@Composable
fun BookingDialog(
    result: QRCodeResult.MediCareBooking,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Xác nhận lịch hẹn", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2C3E))
            }
        },
        text = {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mã lịch hẹn", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.bookingId, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Bác sĩ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.doctorName.ifEmpty { "Chưa cập nhật" }, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Ngày khám", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.appointmentDate.ifEmpty { "Chưa cập nhật" }, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Chi tiết lịch hẹn", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Đóng", color = Color.Gray)
            }
        }
    )
}

@Composable
fun DoctorInfoDialog(
    result: QRCodeResult.DoctorInfo,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF00BCD4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Thông tin bác sĩ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2C3E))
            }
        },
        text = {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mã bác sĩ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.doctorId, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Tên bác sĩ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(result.doctorName.ifEmpty { "Đang cập nhật" }, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xem chi tiết", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Đóng", color = Color.Gray)
            }
        }
    )
}

@Composable
fun UnknownResultDialog(
    rawValue: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9800)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Mã QR không xác định", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2C3E))
            }
        },
        text = {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nội dung:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(
                        rawValue.take(100),
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text("\n⚠️ Mã QR này không phải của MediCare hoặc không đúng định dạng.", fontSize = 12.sp, color = Color(0xFFD32F2F))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Đóng", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun PermissionDialog(
    title: String,
    message: String,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = { Text(message, fontSize = 14.sp, color = Color.Gray) },
        confirmButton = {
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Cấp quyền", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Từ chối", color = Color.Gray)
            }
        }
    )
}

fun scanBarcode(imageProxy: ImageProxy, scanner: BarcodeScanner, onResult: (String?) -> Unit) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                onResult(barcodes.firstOrNull()?.rawValue)
            }
            .addOnFailureListener {
                onResult(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
        onResult(null)
    }
}

fun formatCurrency(amount: Double): String {
    return String.format("%,.0f", amount)
}