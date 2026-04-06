package com.example.ncs3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ncs3.ui.screens.auth.ForgotPasswordScreen
import com.example.ncs3.ui.screens.auth.LoginScreen
import com.example.ncs3.ui.screens.auth.RegisterScreen
import com.example.ncs3.ui.screens.dashboard.DashboardScreen
import com.example.ncs3.ui.screens.onboarding.OnboardingScreen
import com.example.ncs3.ui.screens.splash.SplashScreen
import com.example.ncs3.ui.theme.MediCareTheme

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediCareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MedicareNavigation()
                }
            }
        }
    }
}

@Composable
fun MedicareNavigation() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController) {
                navController.navigate(Screen.Login.route)
            }
        }
        composable(Screen.Login.route) {
            LoginScreen(navController) {
                isLoggedIn = true
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
    }
}