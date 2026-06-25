package com.example.citysync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.citysync.data.AuthManager
import com.example.citysync.ui.screens.auth.*
import com.example.citysync.ui.screens.profile.*
import com.example.citysync.ui.screens.reports.*
import com.example.citysync.ui.screens.community.*
import com.example.citysync.ui.screens.alerts.*
import com.example.citysync.ui.screens.dashboard.*
import com.example.citysync.ui.theme.CitySyncTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle Edge-to-Edge correctly
        enableEdgeToEdge()
        
        setContent {
            // Force light mode for now to ensure visibility on all emulators
            CitySyncTheme(darkTheme = false, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authManager = remember { AuthManager() }
                    var currentScreen by remember { mutableStateOf("splash") }
                    var selectedAnnouncementCategory by remember { mutableStateOf<String?>(null) }
                    var shouldFocusComment by remember { mutableStateOf(false) }
                    var toastMessage by remember { mutableStateOf<String?>(null) }
                    val scope = rememberCoroutineScope()

                    fun showToast(message: String) {
                        toastMessage = message
                        scope.launch {
                            delay(2000)
                            toastMessage = null
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentScreen) {
                            "splash" -> {
                                SplashScreen()
                                LaunchedEffect(Unit) {
                                    delay(1500)
                                    try {
                                        val currentUser = authManager.getCurrentUser()
                                        currentScreen = if (currentUser != null) "dashboard" else "onboarding"
                                    } catch (e: Exception) {
                                        currentScreen = "onboarding"
                                    }
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
                                onSignUpComplete = { 
                                    showToast("Account created! Please sign in.")
                                    currentScreen = "signin" 
                                },
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
                                },
                                onNavigateToReportDetails = {
                                    shouldFocusComment = false
                                    currentScreen = "report_details"
                                }
                            )
                            "reports" -> ReportsScreen(
                                onBack = { currentScreen = "dashboard" },
                                onNavigateToReportWizard = { currentScreen = "report_wizard" },
                                onNavigateToCommunity = { currentScreen = "community" },
                                onNavigateToNotifications = { currentScreen = "notifications" },
                                onNavigateToProfile = { currentScreen = "profile" },
                                onNavigateToReportDetails = { 
                                    shouldFocusComment = false
                                    currentScreen = "report_details" 
                                }
                            )
                            "report_wizard" -> ReportWizardScreen(
                                onBack = { currentScreen = "dashboard" },
                                onComplete = { 
                                    shouldFocusComment = false
                                    currentScreen = "reports" 
                                    showToast("Report submitted successfully!")
                                }
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
                                onNavigateToProfile = { currentScreen = "profile" },
                                onContactSupport = { currentScreen = "contact_support" }
                            )
                            "contact_support" -> ContactSupportScreen(
                                onBack = { currentScreen = "dashboard" },
                                onMessageSent = {
                                    currentScreen = "dashboard"
                                    showToast("Support message sent successfully!")
                                }
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
                                onNavigateToReportDetails = { 
                                    shouldFocusComment = false
                                    currentScreen = "report_details" 
                                },
                                onLogout = { 
                                    scope.launch {
                                        authManager.signOut()
                                        currentScreen = "signin" 
                                    }
                                },
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
                                onNavigateToReportDetails = { 
                                    shouldFocusComment = false
                                    currentScreen = "report_details" 
                                },
                                onLogout = { 
                                    scope.launch {
                                        authManager.signOut()
                                        currentScreen = "signin" 
                                    }
                                }
                            )
                            "emergency" -> EmergencyFlow(
                                onBack = { currentScreen = "dashboard" },
                                onNavigateToReports = { currentScreen = "reports" },
                                onNavigateToCommunity = { currentScreen = "community" },
                                onNavigateToNotifications = { currentScreen = "notifications" },
                                onNavigateToProfile = { currentScreen = "profile" }
                            )
                        }

                        // Global Toast Overlay
                        toastMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 96.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Surface(
                                    color = Color(0xFF1F2937),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
