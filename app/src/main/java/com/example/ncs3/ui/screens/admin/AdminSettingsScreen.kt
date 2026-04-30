package com.example.ncs3.ui.screens.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.utils.SharedPrefs
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Language settings
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(SharedPrefs.getLanguage() ?: "Tiếng Việt") }
    val languages = listOf("Tiếng Việt", "English", "中文")

    // Theme settings
    var showThemeDialog by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(SharedPrefs.isDarkMode()) }
    val themes = listOf("Sáng", "Tối", "Hệ thống")

    // Notification settings
    var showNotificationDialog by remember { mutableStateOf(false) }
    var isNotificationEnabled by remember { mutableStateOf(SharedPrefs.isNotificationEnabled()) }

    // Data settings
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var isBackingUp by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }

    // Security settings
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showTwoFactorDialog by remember { mutableStateOf(false) }
    var isTwoFactorEnabled by remember { mutableStateOf(SharedPrefs.isTwoFactorEnabled()) }

    // App info
    var appVersion by remember { mutableStateOf(getAppVersion(context)) }
    var buildNumber by remember { mutableStateOf(getBuildNumber(context)) }

    // Cache size
    var cacheSize by remember { mutableStateOf(getCacheSize(context)) }

    // Language dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Chọn ngôn ngữ", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    SharedPrefs.saveLanguage(lang)
                                    showLanguageDialog = false
                                    // Restart activity to apply language change
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = {
                                    selectedLanguage = lang
                                    SharedPrefs.saveLanguage(lang)
                                    showLanguageDialog = false
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(lang, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Đóng", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    // Theme dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Chọn giao diện", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    themes.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isDarkMode = when (theme) {
                                        "Tối" -> true
                                        "Sáng" -> false
                                        else -> false
                                    }
                                    SharedPrefs.saveDarkMode(isDarkMode)
                                    showThemeDialog = false
                                    // Apply theme change
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = when {
                                    theme == "Tối" && isDarkMode -> true
                                    theme == "Sáng" && !isDarkMode -> true
                                    else -> false
                                },
                                onClick = {
                                    isDarkMode = when (theme) {
                                        "Tối" -> true
                                        "Sáng" -> false
                                        else -> false
                                    }
                                    SharedPrefs.saveDarkMode(isDarkMode)
                                    showThemeDialog = false
                                    (context as? androidx.activity.ComponentActivity)?.recreate()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0D47A1))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(theme, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Đóng", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    // Notification dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("Cài đặt thông báo", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nhận thông báo", fontSize = 14.sp)
                        Switch(
                            checked = isNotificationEnabled,
                            onCheckedChange = {
                                isNotificationEnabled = it
                                SharedPrefs.saveNotificationEnabled(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0D47A1))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Cho phép nhận thông báo về lịch hẹn, khuyến mãi và cập nhật",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("Lưu", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    // Two Factor dialog
    if (showTwoFactorDialog) {
        AlertDialog(
            onDismissRequest = { showTwoFactorDialog = false },
            title = { Text("Xác thực 2 lớp", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bật xác thực 2 lớp", fontSize = 14.sp)
                        Switch(
                            checked = isTwoFactorEnabled,
                            onCheckedChange = {
                                isTwoFactorEnabled = it
                                SharedPrefs.saveTwoFactorEnabled(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0D47A1))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tăng cường bảo mật tài khoản bằng mã xác thực",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTwoFactorDialog = false }) {
                    Text("Lưu", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    // Change password dialog
    if (showChangePasswordDialog) {
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Đổi mật khẩu", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Mật khẩu cũ") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu mới") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                            // TODO: Change password API call
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(20.dp),
                    enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == confirmPassword
                ) {
                    Text("Cập nhật", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    // Clear data dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa dữ liệu", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Bạn có chắc chắn muốn xóa toàn bộ dữ liệu người dùng?")
                    Text("Hành động này không thể hoàn tác.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        SharedPrefs.clearAll()
                        showClearDataDialog = false
                        (context as? androidx.activity.ComponentActivity)?.recreate()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    // Clear cache dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Xóa bộ nhớ đệm", fontWeight = FontWeight.Bold) },
            text = { Text("Xóa dữ liệu tạm thời để giải phóng bộ nhớ.") },
            confirmButton = {
                Button(
                    onClick = {
                        clearCache(context)
                        cacheSize = getCacheSize(context)
                        showClearCacheDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    // Reset dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Khôi phục cài đặt gốc", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Tất cả cài đặt sẽ được khôi phục về mặc định.")
                    Text("Dữ liệu cá nhân sẽ không bị ảnh hưởng.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        SharedPrefs.resetToDefault()
                        showResetDialog = false
                        (context as? androidx.activity.ComponentActivity)?.recreate()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Khôi phục", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Cài đặt hệ thống", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Quản lý cấu hình ứng dụng", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cài đặt chung
            item {
                Text("CHUNG", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9AA0A6))
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Language,
                    title = "Ngôn ngữ",
                    subtitle = selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Giao diện",
                    subtitle = if (isDarkMode) "Tối" else "Sáng",
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Thông báo",
                    subtitle = if (isNotificationEnabled) "Bật" else "Tắt",
                    onClick = { showNotificationDialog = true }
                )
            }

            // Cài đặt bảo mật
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("BẢO MẬT", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9AA0A6))
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Đổi mật khẩu",
                    subtitle = "Cập nhật mật khẩu đăng nhập",
                    onClick = { showChangePasswordDialog = true }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Security,
                    title = "Xác thực 2 lớp",
                    subtitle = if (isTwoFactorEnabled) "Đã bật" else "Đã tắt",
                    onClick = { showTwoFactorDialog = true }
                )
            }

            // Cài đặt dữ liệu
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("DỮ LIỆU", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9AA0A6))
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Backup,
                    title = "Sao lưu dữ liệu",
                    subtitle = "Sao lưu lên Cloud",
                    isLoading = isBackingUp,
                    onClick = {
                        scope.launch {
                            isBackingUp = true
                            // TODO: Backup data to Firebase/Firestore
                            kotlinx.coroutines.delay(1500)
                            isBackingUp = false
                            android.widget.Toast.makeText(context, "Sao lưu thành công", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Restore,
                    title = "Khôi phục dữ liệu",
                    subtitle = "Khôi phục từ bản sao lưu",
                    isLoading = isRestoring,
                    onClick = {
                        scope.launch {
                            isRestoring = true
                            // TODO: Restore data from Firebase/Firestore
                            kotlinx.coroutines.delay(1500)
                            isRestoring = false
                            android.widget.Toast.makeText(context, "Khôi phục thành công", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.DeleteSweep,
                    title = "Xóa bộ nhớ đệm",
                    subtitle = cacheSize,
                    onClick = { showClearCacheDialog = true }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.Delete,
                    title = "Xóa dữ liệu người dùng",
                    subtitle = "Xóa toàn bộ dữ liệu ứng dụng",
                    iconColor = Color(0xFFE53935),
                    textColor = Color(0xFFE53935),
                    onClick = { showClearDataDialog = true }
                )
            }

            item {
                SettingsMenuItem(
                    icon = Icons.Outlined.SettingsBackupRestore,
                    title = "Khôi phục cài đặt gốc",
                    subtitle = "Đặt lại tất cả cài đặt về mặc định",
                    iconColor = Color(0xFFFF9800),
                    textColor = Color(0xFFFF9800),
                    onClick = { showResetDialog = true }
                )
            }

            // Thông tin ứng dụng
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("THÔNG TIN", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9AA0A6))
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Phiên bản", fontSize = 14.sp, color = Color.Gray)
                            Text(appVersion, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Build", fontSize = 14.sp, color = Color.Gray)
                            Text(buildNumber, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Android SDK", fontSize = 14.sp, color = Color.Gray)
                            Text("Android ${Build.VERSION.SDK_INT}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://medicare.com/privacy"))
                            context.startActivity(intent)
                        }
                ) {
                    Text(
                        "Chính sách bảo mật",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF0D47A1)
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://medicare.com/terms"))
                            context.startActivity(intent)
                        }
                ) {
                    Text(
                        "Điều khoản sử dụng",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF0D47A1)
                    )
                }
            }

            // Nút đánh giá ứng dụng
            item {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.example.ncs3"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Icon(Icons.Outlined.Star, null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đánh giá ứng dụng", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = Color(0xFF0D47A1),
    textColor: Color = Color(0xFF1A1A2E),
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = iconColor)
                } else {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            if (!isLoading) {
                Icon(Icons.Outlined.ChevronRight, null, tint = Color(0xFF9AA0A6))
            }
        }
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }
}

fun getBuildNumber(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.longVersionCode.toString()
    } catch (e: Exception) {
        "1"
    }
}

fun getCacheSize(context: Context): String {
    val cacheDir = context.cacheDir
    val size = cacheDir.listFiles()?.sumOf { it.length() } ?: 0
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        else -> "${size / (1024 * 1024)} MB"
    }
}

fun clearCache(context: Context) {
    try {
        val cacheDir = context.cacheDir
        cacheDir.listFiles()?.forEach { it.delete() }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}