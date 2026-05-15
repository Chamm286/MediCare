package com.example.ncs3.ui.screens.auth

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
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
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenHeight < 700.dp
    val isTablet = screenWidth > 600.dp

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    // Animation
    val logoScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    val fadeIn by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
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
                            completePostLoginActions(user.uid, user.email ?: email, firestore, onLoginSuccess, context)
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
                        completePostLoginActions(user.uid, email, firestore, onLoginSuccess, context)
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
            title = { Text("Lỗi đăng nhập", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Đóng", color = Color(0xFF0D47A1))
                }
            }
        )
    }

    // ========== UI NÂNG CẤP ==========
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFE),
                        Color(0xFFF0F4FA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isSmallScreen) 20.dp else 32.dp)
                .alpha(fadeIn),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isSmallScreen) 40.dp else 60.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(if (isSmallScreen) 70.dp else 80.dp)
                    .scale(logoScale)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFF1565C0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏥", fontSize = if (isSmallScreen) 36.sp else 42.sp)
            }

            Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 20.dp))

            Text(
                "Welcome Back!",
                fontSize = if (isSmallScreen) 24.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A2C3E),
                letterSpacing = 0.5.sp
            )
            Text(
                "Sign in to continue",
                fontSize = 13.sp,
                color = Color(0xFF6B7A8A)
            )

            Spacer(modifier = Modifier.height(if (isSmallScreen) 32.dp else 40.dp))

            // Card đăng nhập
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isSmallScreen) 20.dp else 24.dp)
                ) {
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("hello@example.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = Color(0xFF0D47A1),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0D47A1),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF0D47A1)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("••••••••") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF0D47A1),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7A8A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0D47A1),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF0D47A1)
                        ),
                        singleLine = true
                    )

                    // Options row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
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
                                    checkedColor = Color(0xFF0D47A1),
                                    uncheckedColor = Color(0xFFBDBDBD)
                                ),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remember me", fontSize = 12.sp, color = Color(0xFF546E7A))
                        }

                        TextButton(
                            onClick = { navController.navigate("forgot_password") },
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                "Forgot Password?",
                                fontSize = 12.sp,
                                color = Color(0xFF0D47A1),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login button
                    Button(
                        onClick = { performLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isSmallScreen) 48.dp else 52.dp)
                            .scale(buttonScale),
                        shape = RoundedCornerShape(26.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0D47A1)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Sign In",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )
                        Text(
                            "or",
                            fontSize = 12.sp,
                            color = Color(0xFF9AA0A6),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google button
                    OutlinedButton(
                        onClick = { signInWithGoogle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isSmallScreen) 48.dp else 52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                "G",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4285F4)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Sign in with Google",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF5C5C5C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign up link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Don't have an account? ",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7A8A)
                        )
                        Text(
                            "Sign Up",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1),
                            modifier = Modifier.clickable { navController.navigate("register") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

fun completePostLoginActions(
    userId: String,
    userEmail: String,
    firestore: FirebaseFirestore,
    onLoginSuccess: (String) -> Unit,
    context: android.content.Context
) {
    SharedPrefs.saveUserEmail(userEmail)
    SharedPrefs.saveUserId(userId)
    SharedPrefs.saveLoggedIn(true)

    firestore.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val role = document.getString("role") ?: "patient"
                SharedPrefs.saveUserRole(role)
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess(userId)
            } else {
                val newUser = hashMapOf(
                    "email" to userEmail,
                    "role" to "patient",
                    "fullName" to "",
                    "phone" to "",
                    "createdAt" to System.currentTimeMillis(),
                    "avatar" to "",
                    "isActive" to true
                )
                firestore.collection("users").document(userId).set(newUser)
                    .addOnSuccessListener {
                        SharedPrefs.saveUserRole("patient")
                        Toast.makeText(context, "Welcome to MediCare!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(userId)
                    }
                    .addOnFailureListener {
                        SharedPrefs.saveUserRole("patient")
                        onLoginSuccess(userId)
                    }
            }
        }
        .addOnFailureListener {
            SharedPrefs.saveUserRole("patient")
            onLoginSuccess(userId)
        }
}