package com.example.citysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.citysync.screens.*
import com.example.citysync.ui.theme.CitySyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CitySyncTheme {
                StandardViewport {
                    var currentScreen by remember { mutableStateOf("splash") }
                    var selectedAnnouncementCategory by remember { mutableStateOf<String?>(null) }
                    var shouldFocusComment by remember { mutableStateOf(false) }

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
                            onNavigateToEmergency = { 
                                currentScreen = "emergency" 
                            }
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
                            onNavigateToProfile = { currentScreen = "profile" },
                            onNavigateToReportDetails = { 
                                shouldFocusComment = false
                                currentScreen = "report_details" 
                            },
                            onCommentClick = {
                                shouldFocusComment = true
                                currentScreen = "report_details"
                            },
                            onContactSupport = { currentScreen = "contact_support" }
                        )
                        "report_details" -> ReportDetailsScreen(
                            initialFocusComment = shouldFocusComment,
                            onBack = { currentScreen = "community" },
                            onTrackStatus = { currentScreen = "track_report" },
                            onNavigateToHome = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onContactSupport = { currentScreen = "contact_support" }
                        )
                        "track_report" -> TrackReportScreen(
                            onBack = { currentScreen = "report_details" },
                            onNavigateToHome = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "contact_support" -> ContactSupportScreen(
                            onBack = { currentScreen = "dashboard" }, // Simple back to dash or use a history stack
                            onNavigateToHome = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                        "notifications" -> NotificationsScreen(
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onNavigateToSettings = { currentScreen = "settings" }
                        )
                        "settings" -> ProfileFlow(
                            onBack = { currentScreen = "notifications" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onLogout = { currentScreen = "signin" },
                            initialSubView = ProfileSubView.SETTINGS
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
                            onBack = { currentScreen = "dashboard" },
                            onNavigateToReports = { currentScreen = "reports" },
                            onNavigateToCommunity = { currentScreen = "community" },
                            onNavigateToNotifications = { currentScreen = "notifications" },
                            onNavigateToProfile = { currentScreen = "profile" }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Enforces absolute viewport consistency across all application views.
 * Every screen is wrapped in a rigid, uniform container constrained to a standard max-width.
 */
@Composable
fun StandardViewport(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E7EB)), // Neutral outer background (Slate-200)
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth() // Enforce w-full within the max-width
                .fillMaxHeight()
                .shadow(elevation = 16.dp, shape = RectangleShape),
            color = Color(0xFFF4F6F9), // Standardized application background
            shape = RectangleShape
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}
