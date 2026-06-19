package com.example.ncs3

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.example.ncs3.data.repository.MedicareRepository
import com.example.ncs3.service.DataSeeder
import com.example.ncs3.ui.screens.account.AccountScreen
import com.example.ncs3.ui.screens.admin.AdminDashboardScreen
import com.example.ncs3.ui.screens.admin.AdminSettingsScreen
import com.example.ncs3.ui.screens.admin.ManageDoctorsScreen
import com.example.ncs3.ui.screens.admin.ManagePatientsScreen
import com.example.ncs3.ui.screens.admin.ReportsScreen
import com.example.ncs3.ui.screens.appointment.AppointmentDetailScreen
import com.example.ncs3.ui.screens.appointment.AppointmentScreen
import com.example.ncs3.ui.screens.auth.ForgotPasswordScreen
import com.example.ncs3.ui.screens.auth.LoginScreen
import com.example.ncs3.ui.screens.auth.RegisterScreen
import com.example.ncs3.ui.screens.auth.RoleSelectScreen
import com.example.ncs3.ui.screens.booking.BookingScreen
import com.example.ncs3.ui.screens.booking.PaymentScreen
import com.example.ncs3.ui.screens.booking.PaymentSuccessScreen
import com.example.ncs3.ui.screens.chatbot.AIChatbotScreen
import com.example.ncs3.ui.screens.dashboard.DashboardScreen
import com.example.ncs3.ui.screens.doctor.*
import com.example.ncs3.ui.screens.history.HistoryScreen
import com.example.ncs3.ui.screens.map.MapScreen
import com.example.ncs3.ui.screens.medicine.CheckoutScreen
import com.example.ncs3.ui.screens.medicine.MedicineScreen
import com.example.ncs3.ui.screens.medicine.MedicineStoreScreen
import com.example.ncs3.ui.screens.notification.NotificationScreen
import com.example.ncs3.ui.screens.onboarding.OnboardingScreen
import com.example.ncs3.ui.screens.profile.ProfileScreen
import com.example.ncs3.ui.screens.rating.RatingScreen
import com.example.ncs3.ui.screens.review.ReviewScreen
import com.example.ncs3.ui.screens.search.SearchScreen
import com.example.ncs3.ui.screens.settings.SettingsScreen
import com.example.ncs3.ui.screens.specialty.SpecialtyDetailScreen
import com.example.ncs3.ui.screens.specialty.SpecialtyListScreen
import com.example.ncs3.ui.screens.splash.SplashScreen
import com.example.ncs3.utils.RegistrationData
import com.example.ncs3.utils.SharedPrefs
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lightScheme = lightColorScheme(
                primary = Color(0xFF0D47A1),
                secondary = Color(0xFF00BCD4),
                tertiary = Color(0xFFFF6B4A),
                background = Color(0xFFF5F7FA),
                surface = Color.White,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color(0xFF1A2C3E),
                onSurface = Color(0xFF1A2C3E)
            )

            MaterialTheme(
                colorScheme = lightScheme,
                typography = MaterialTheme.typography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var isLoggedIn by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("patient") }
    var hasSeenOnboarding by remember { mutableStateOf(false) }

    // Seed Data
    val globalScope = rememberCoroutineScope()
    LaunchedEffect(userId) {
        globalScope.launch {
            try {
                val seeder = DataSeeder(FirebaseFirestore.getInstance())
                seeder.seedAllData()
            } catch (e: Exception) {
                println("❌ Gặp lỗi khi chạy dữ liệu tự động: ${e.message}")
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ===================== SPLASH & ONBOARDING =====================
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    if (hasSeenOnboarding) {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    hasSeenOnboarding = true
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // ===================== AUTH SCREENS =====================
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { uid ->
                    isLoggedIn = true
                    userId = uid
                    val role = SharedPrefs.getUserRole()
                    when (role) {
                        "admin" -> {
                            navController.navigate("admin_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        "doctor" -> {
                            navController.navigate("doctor_dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegistrationData = { email, password, fullName, phone ->
                    RegistrationData.email = email
                    RegistrationData.password = password
                    RegistrationData.fullName = fullName
                    RegistrationData.phone = phone
                    navController.navigate("role_select")
                }
            )
        }

        composable("role_select") {
            RoleSelectScreen(navController = navController)
        }

        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController)
        }

        // ===================== PATIENT SCREENS =====================
        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                isLoggedIn = isLoggedIn,
                userId = userId,
                userRole = userRole,
                onLogout = {
                    isLoggedIn = false
                    userId = ""
                    userRole = "patient"
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable("appointments") {
            AppointmentScreen(
                navController = navController,
                userId = userId
            )
        }

        composable("appointment") {
            AppointmentScreen(
                navController = navController,
                userId = userId
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                isLoggedIn = isLoggedIn,
                userId = userId
            )
        }

        composable("booking") {
            BookingScreen(
                navController = navController,
                doctorId = null,
                userId = userId
            )
        }

        composable("booking/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            BookingScreen(
                navController = navController,
                userId = userId,
                doctorId = doctorId
            )
        }

        composable("doctors") {
            DoctorScreen(navController = navController)
        }

        composable("doctor_detail/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorDetailScreen(
                navController = navController,
                doctorId = doctorId,
                userId = userId
            )
        }

        composable("specialties") {
            SpecialtyListScreen(navController = navController)
        }

        composable("specialty_detail/{specialtyId}") { backStackEntry ->
            val specialtyId = backStackEntry.arguments?.getString("specialtyId") ?: ""
            SpecialtyDetailScreen(
                navController = navController,
                specialtyId = specialtyId,
                isLoggedIn = isLoggedIn,
                userId = userId
            )
        }

        composable("search") {
            SearchScreen(navController = navController)
        }

        composable("notification") {
            NotificationScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(navController = navController)
        }

        composable("history") {
            HistoryScreen(navController = navController)
        }

        composable("appointment_detail/{appointmentId}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            AppointmentDetailScreen(
                navController = navController,
                appointmentId = appointmentId
            )
        }

        composable("account") {
            AccountScreen(
                navController = navController,
                isLoggedIn = isLoggedIn,
                onLogout = {
                    isLoggedIn = false
                    userId = ""
                    userRole = "patient"
                    navController.navigate("login") {
                        popUpTo("account") { inclusive = true }
                    }
                }
            )
        }

        // ===================== MEDICINE SCREENS =====================
        composable("medicine_store") {
            MedicineStoreScreen(
                navController = navController,
                userId = userId
            )
        }

        composable("medicine") {
            MedicineScreen(
                navController = navController,
                isLoggedIn = isLoggedIn,
                userId = userId
            )
        }

        // ===================== PAYMENT SCREENS =====================
        composable("payment") {
            val viewModel: com.example.ncs3.ui.viewmodels.booking.BookingViewModel = hiltViewModel()
            PaymentScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate("payment_success")
                }
            )
        }

        composable("payment_success") {
            PaymentSuccessScreen(
                navController = navController,
                onDone = {
                    navController.navigate("appointment") {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ===================== MAP SCREEN =====================
        composable("map") {
            MapScreen(navController = navController)
        }

        // ===================== AI CHATBOT =====================
        composable("ai_chatbot") {
            AIChatbotScreen(navController = navController)
        }

        // ===================== REVIEW & RATING =====================
        composable("review") {
            ReviewScreen(
                navController = navController,
                userId = userId,
                userName = ""
            )
        }

        composable("rating/{doctorId}/{doctorName}/{patientId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            val doctorName = backStackEntry.arguments?.getString("doctorName") ?: ""
            RatingScreen(
                navController = navController,
                doctorId = doctorId,
                doctorName = doctorName,
                patientId = userId
            )
        }

        // ===================== CHECKOUT =====================
        composable("checkout") {
            CheckoutScreen(
                navController = navController,
                userId = userId
            )
        }

        // ===================== DOCTOR SCREENS =====================
        composable("doctor_dashboard") {
            val scope = rememberCoroutineScope()
            val repository = remember { MedicareRepository() }
            var doctorName by remember { mutableStateOf("") }

            LaunchedEffect(userId) {
                scope.launch {
                    val doctor = repository.getDoctorById(userId)
                    doctorName = doctor?.name ?: "Bác sĩ"
                }
            }

            DoctorDashboardScreen(
                navController = navController,
                doctorId = userId,
                doctorName = doctorName
            )
        }

        composable("doctor_profile") {
            DoctorProfileScreen(
                navController = navController,
                doctorId = userId
            )
        }

        composable("doctor_schedule") {
            DoctorScheduleScreen(
                navController = navController,
                doctorId = userId
            )
        }

        composable("doctor_appointments") {
            DoctorAppointmentsScreen(
                navController = navController,
                doctorId = userId
            )
        }

        // ===================== ADMIN SCREENS =====================
        composable("admin_dashboard") {
            AdminDashboardScreen(navController = navController)
        }

        composable("manage_doctors") {
            ManageDoctorsScreen(navController = navController)
        }

        composable("manage_patients") {
            ManagePatientsScreen(navController = navController)
        }

        composable("reports") {
            ReportsScreen(navController = navController)
        }

        composable("admin_settings") {
            AdminSettingsScreen(navController = navController)
        }

        // ===================== PAYMENT SCREENS =====================
        composable("payment") {
            val viewModel: com.example.ncs3.ui.viewmodels.booking.BookingViewModel = hiltViewModel()
            PaymentScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate("payment_success")
                }
            )
        }

        composable("payment_success") {
            PaymentSuccessScreen(
                navController = navController,
                onDone = {
                    navController.navigate("appointment") {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}