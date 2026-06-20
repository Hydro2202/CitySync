package com.example.citysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.citysync.screens.*
import com.example.citysync.ui.theme.CitySyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CitySyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("splash") }
                    var selectedAnnouncementCategory by remember { mutableStateOf<String?>(null) }

                    when (currentScreen) {
                        "splash" -> {
                            SplashScreen()
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(2000) // Display splash for 2 seconds
                                currentScreen = "onboarding"
                            }
                        }
                        "onboarding" -> OnboardingScreen(onFinished = { currentScreen = "signin" })
                        "signin" -> SignInScreen(
                            onSignInSuccess = { currentScreen = "dashboard" },
                            onNavigateToSignUp = { currentScreen = "signup" },
                            onNavigateToForgotPassword = { currentScreen = "forgot_password" }
                        )
                        "forgot_password" -> ForgotPasswordScreen(onBack = { currentScreen = "signin" })
                        "signup" -> SignUpWizardScreen(
                            onSignUpComplete = { currentScreen = "signin" },
                            onSignInRedirect = { currentScreen = "signin" }
                        )
                        "dashboard" -> DashboardScreen(
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToReportWizard = { currentScreen = "report_wizard" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onNavigateToAnnouncements = { 
                                selectedAnnouncementCategory = null
                                currentScreen = "announcements" 
                            },
                            onNavigateToAnnouncementDetail = { category ->
                                selectedAnnouncementCategory = category
                                currentScreen = "announcements"
                            },
                            onNavigateToEmergency = { currentScreen = "emergency" }
                        )
                        "reports" -> ReportsScreen(
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReportWizard = { currentScreen = "report_wizard" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "report_wizard" -> ReportWizardScreen(
                            onBack = { currentScreen = "dashboard" },
                            onComplete = { currentScreen = "reports" }
                        )
                        "community" -> CommunityFeedScreen(
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "notifications" -> NotificationsScreen(
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "announcements" -> AnnouncementsScreen(
                            initialCategory = selectedAnnouncementCategory,
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "profile" -> ProfileFlow(
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onLogout = { currentScreen = "signin" }
                        )
                        "emergency" -> EmergencyFlow(
                            onBack = { currentScreen = "dashboard" }
                        )
                    }
                }
            }
        }
    }
}