package com.example.employeeleaveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf // أضفنا الاستيراد الصحيح للأرقام هنا
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.employeeleaveapp.ui.theme.EmployeeLeaveAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmployeeLeaveAppTheme {
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

    // الآن أصبح التعريف متوافقاً تماماً مع المكتبة وبدون أي تنبيهات
    var administrativeBalance by remember { mutableIntStateOf(15) }

    NavHost(navController = navController, startDestination = "login") {

        // 1. شاشة تسجيل الدخول
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // 2. شاشة لوحة تحكم الموظف الرئيسية
        composable("dashboard") {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
            ) {
                MainDashboard(
                    currentBalance = administrativeBalance,
                    onNavigateToRequest = { navController.navigate("request_leave") }
                )
            }
        }

        // 3. شاشة تقديم طلب الإجازة الجديد
        composable("request_leave") {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
            ) {
                RequestLeaveScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLeaveSubmitted = { daysRequested ->
                        if (administrativeBalance >= daysRequested) {
                            administrativeBalance -= daysRequested
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}