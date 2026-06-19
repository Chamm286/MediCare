package com.example.ncs3.ui.screens.auth

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ncs3.R
import com.example.ncs3.utils.SharedPrefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenHeight < 700.dp
    val scrollState = rememberScrollState()

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val brandPrimary = Color(0xFF1E6091)
    val brandGradient = listOf(Color(0xFF1A5276), Color(0xFF2980B9))
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val borderLight = Color(0xFFE2E8F0)
    val bgInput = Color(0xFFF8FAFC)

    val fadeIn by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = EaseInOutQuart),
        label = "fadeIn"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "buttonScale"
    )

    // Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrEmpty()) {
                isLoading = false
                errorMessage = "Không thể xác thực với Google"
                showErrorDialog = true
                return@rememberLauncherForActivityResult
            }
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        auth.currentUser?.let { user ->
                            // 👉 LẤY THÔNG TIN TỪ GOOGLE
                            val displayName = account.displayName ?: ""
                            val avatarUrl = account.photoUrl?.toString() ?: ""
                            val email = account.email ?: ""

                            // 👉 LƯU VÀO FIRESTORE
                            val userData = hashMapOf(
                                "email" to email,
                                "fullName" to displayName,
                                "avatar" to avatarUrl,
                                "role" to "patient",
                                "isActive" to true,
                                "createdAt" to System.currentTimeMillis()
                            )
                            firestore.collection("users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // 👉 LƯU VÀO SHAREDPREFS
                                    SharedPrefs.saveUserName(displayName)
                                    SharedPrefs.saveUserAvatar(avatarUrl)
                                    SharedPrefs.saveUserEmail(email)
                                    SharedPrefs.saveUserRole("patient")
                                    SharedPrefs.saveUserId(user.uid)
                                    SharedPrefs.saveLoggedIn(true)

                                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess(user.uid)
                                }
                                .addOnFailureListener {
                                    // Vẫn lưu SharedPrefs nếu Firestore lỗi
                                    SharedPrefs.saveUserName(displayName)
                                    SharedPrefs.saveUserAvatar(avatarUrl)
                                    SharedPrefs.saveUserEmail(email)
                                    SharedPrefs.saveUserRole("patient")
                                    SharedPrefs.saveUserId(user.uid)
                                    SharedPrefs.saveLoggedIn(true)
                                    onLoginSuccess(user.uid)
                                }
                        }
                    } else {
                        errorMessage = "Đăng nhập Google thất bại"
                        showErrorDialog = true
                    }
                }
        } catch (e: ApiException) {
            isLoading = false
            errorMessage = "Đăng nhập Google thất bại"
            showErrorDialog = true
        }
    }

    fun signInWithGoogle() {
        isLoading = true
        googleSignInClient.signOut().addOnCompleteListener {
            googleLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    fun performLogin() {
        when {
            email.isEmpty() -> {
                errorMessage = "Vui lòng nhập email"
                showErrorDialog = true
                return
            }
            password.isEmpty() -> {
                errorMessage = "Vui lòng nhập mật khẩu"
                showErrorDialog = true
                return
            }
            password.length < 6 -> {
                errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
                showErrorDialog = true
                return
            }
        }
        isLoading = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (rememberMe) {
                            SharedPrefs.saveUserEmail(email)
                            SharedPrefs.saveUserPassword(password)
                            SharedPrefs.saveRememberMe(true)
                        }
                        // 👉 LẤY THÔNG TIN USER TỪ FIRESTORE
                        firestore.collection("users").document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val fullName = document.getString("fullName") ?: ""
                                    val avatar = document.getString("avatar") ?: ""
                                    val role = document.getString("role") ?: "patient"

                                    SharedPrefs.saveUserName(fullName)
                                    SharedPrefs.saveUserAvatar(avatar)
                                    SharedPrefs.saveUserRole(role)
                                    SharedPrefs.saveUserId(user.uid)
                                    SharedPrefs.saveLoggedIn(true)
                                } else {
                                    SharedPrefs.saveUserId(user.uid)
                                    SharedPrefs.saveLoggedIn(true)
                                }
                                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(user.uid)
                            }
                            .addOnFailureListener {
                                SharedPrefs.saveUserId(user.uid)
                                SharedPrefs.saveLoggedIn(true)
                                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(user.uid)
                            }
                    }
                } else {
                    errorMessage = "Email hoặc mật khẩu không đúng"
                    showErrorDialog = true
                }
            }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Thông báo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textPrimary) },
            text = { Text(errorMessage, fontSize = 14.sp, color = textSecondary) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Đóng", color = brandPrimary, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .alpha(fadeIn)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isSmallScreen) 40.dp else 60.dp))

            Image(
                painter = painterResource(id = R.drawable.anh1),
                contentDescription = "Medicare Logo",
                modifier = Modifier
                    .size(if (isSmallScreen) 90.dp else 100.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ĐĂNG NHẬP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = textPrimary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "MediCare",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = brandPrimary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "Đăng nhập hệ thống quản lý y tế số thông minh",
                fontSize = 13.sp,
                color = textSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(if (isSmallScreen) 30.dp else 40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email đăng nhập", color = textSecondary.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = {
                    Icon(Icons.Outlined.Email, null, tint = brandPrimary, modifier = Modifier.size(20.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brandPrimary,
                    unfocusedBorderColor = borderLight,
                    focusedContainerColor = bgInput,
                    unfocusedContainerColor = bgInput,
                    cursorColor = brandPrimary
                ),
                singleLine = true,
                isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mật khẩu bảo mật", color = textSecondary.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, null, tint = brandPrimary, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brandPrimary,
                    unfocusedBorderColor = borderLight,
                    focusedContainerColor = bgInput,
                    unfocusedContainerColor = bgInput,
                    cursorColor = brandPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = brandPrimary,
                            uncheckedColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ghi nhớ đăng nhập",
                        fontSize = 13.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Quên mật khẩu?",
                    fontSize = 13.sp,
                    color = brandPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate("forgot_password") }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { performLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(colors = brandGradient),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                    } else {
                        Text(text = "ĐĂNG NHẬP", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(borderLight))
                Text(
                    text = "HOẶC",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(borderLight))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { signInWithGoogle() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, borderLight),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Google", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                }

                OutlinedButton(
                    onClick = { /* Facebook Login */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, borderLight),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_facebook),
                            contentDescription = "Facebook Icon",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Facebook", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Bạn chưa có tài khoản? ", fontSize = 14.sp, color = textSecondary)
                Text(
                    text = "Đăng ký ngay",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandPrimary,
                    modifier = Modifier.clickable { navController.navigate("register") }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}