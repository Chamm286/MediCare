package com.example.ncs3.ui.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.border
import android.os.Build
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
    var scanResult by remember { mutableStateOf("") }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageAnalysis by remember { mutableStateOf<ImageAnalysis?>(null) }
    var cameraExecutor by remember { mutableStateOf<ExecutorService?>(null) }

    val barcodeScanner = remember { BarcodeScanning.getClient() }

    // Request permission if not granted
    if (!hasCameraPermission) {
        PermissionDialog(
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
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis?.setAnalyzer(cameraExecutor!!) { imageProxy ->
                if (isScanning && scannedCode.isEmpty()) {
                    scanBarcode(imageProxy, barcodeScanner) { result ->
                        if (result != null && scannedCode.isEmpty()) {
                            scannedCode = result
                            isScanning = false
                            scanResult = result
                            showResultDialog = true
                        }
                    }
                }
            }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraExecutor?.shutdown()
            barcodeScanner.close()
        }
    }

    // Result dialog
    if (showResultDialog && scanResult.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
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
                        Icon(Icons.Default.QrCodeScanner, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("QR Code đã quét", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Nội dung:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            scanResult,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResultDialog = false
                        if (scanResult.contains("doctor") || scanResult.contains("booking")) {
                            // TODO: Navigate based on QR content
                        }
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Đóng", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showResultDialog = false
                    scannedCode = ""
                    isScanning = true
                }) {
                    Text("Quét lại", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quét QR Code", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Đưa mã QR vào khung hình", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
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
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }
            )

            // Qr code scanning frame
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                // Scan frame
                Box(
                    modifier = Modifier
                        .size(250.dp, 250.dp)
                        .background(Color.Transparent)
                        .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                ) {
                    // Corner lines
                    Box(
                        modifier = Modifier
                            .size(30.dp, 3.dp)
                            .align(Alignment.TopStart)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp, 30.dp)
                            .align(Alignment.TopStart)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp, 3.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp, 30.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp, 3.dp)
                            .align(Alignment.BottomStart)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp, 30.dp)
                            .align(Alignment.BottomStart)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp, 3.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color(0xFF0D47A1))
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp, 30.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color(0xFF0D47A1))
                    )
                }

                // Scanning animation
                var offset by remember { mutableStateOf(0f) }
                LaunchedEffect(isScanning) {
                    while (isScanning) {
                        offset = (offset + 10) % 250
                        delay(16)
                    }
                }

                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(3.dp)
                        .offset(y = offset.dp)
                        .background(Color(0xFF0D47A1))
                )
            }

            // Bottom info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Đưa mã QR vào giữa màn hình",
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("Chọn ảnh từ thư viện", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(onRequestPermission: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yêu cầu quyền Camera", fontWeight = FontWeight.Bold) },
        text = { Text("Ứng dụng cần quyền truy cập camera để quét mã QR.") },
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
                for (barcode in barcodes) {
                    onResult(barcode.rawValue)
                    break
                }
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