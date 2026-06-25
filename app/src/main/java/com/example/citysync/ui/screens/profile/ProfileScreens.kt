package com.example.citysync.ui.screens.profile

import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.data.AuthManager
import com.example.citysync.data.repository.ReportRepository
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.user.UserInfo

enum class ProfileSubView {
    MAIN, EDIT, SETTINGS, PRIVACY, SECURITY, HELP, CONTACT_SUPPORT, TERMS, PRIVACY_POLICY, FAVORITES
}

data class UserProfileData(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String,
    val location: String = "Manila, Philippines",
    val memberSince: String = "January 2026",
    val totalReports: String = "27",
    val resolvedReports: String = "18",
    val pendingReports: String = "9",
    val reportsThisMonth: String = "0"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFlow(
    onBack: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToReportDetails: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    initialSubView: ProfileSubView = ProfileSubView.MAIN
) {
    val NotifBlueLocal = Color(0xFF0D4E89)
    val NotifBgLocal = Color(0xFFF4F6F9)
    val NotifTextDarkLocal = Color(0xFF1A202C)
    val NotifTextMutedLocal = Color(0xFF5A6B7C)
    val NotifTextTimestampLocal = Color(0xFFA0AEC0)
    val ProfileSignOutRedLocal = Color(0xFFDC2626)
    val ProfileHelpBgLocal = Color(0xFFEBF8FF)
    val ProfileEmailMutedLocal = Color(0xFF7A8B9C)

    val authManager = remember { AuthManager() }
    val reportRepository = remember { ReportRepository() }
    
    val context = LocalContext.current
    var currentSubView by remember { mutableStateOf(initialSubView) }
    var userData by remember { 
        mutableStateOf(UserProfileData(
            firstName = "New",
            lastName = "Citizen",
            email = "",
            phone = "No phone linked",
            address = "Manila, Philippines",
            totalReports = "0",
            resolvedReports = "0",
            pendingReports = "0"
        ))
    }

    LaunchedEffect(Unit) {
        val user = authManager.getCurrentUser()
        if (user != null) {
            val fullName = user.userMetadata?.get("full_name")?.toString()?.removeSurrounding("\"") ?: "New Citizen"
            val phone = user.userMetadata?.get("phone")?.toString()?.removeSurrounding("\"") ?: "No phone linked"
            val address = user.userMetadata?.get("address")?.toString()?.removeSurrounding("\"") ?: "Manila, Philippines"
            
            // Fetch real-time counters
            val reports = reportRepository.getReports(user.id)
            val total = reports.size
            val resolved = reports.count { it.status.equals("Resolved", ignoreCase = true) }
            val pending = reports.count { 
                it.status.equals("Assigned", ignoreCase = true) || 
                it.status.equals("In Progress", ignoreCase = true) || 
                it.status.equals("Under Review", ignoreCase = true) 
            }
            val thisMonth = reportRepository.countReportsThisMonth(reports)

            userData = userData.copy(
                firstName = fullName.substringBefore(" "),
                lastName = fullName.substringAfter(" ", ""),
                email = user.email ?: "",
                phone = phone,
                address = address,
                totalReports = total.toString(),
                resolvedReports = resolved.toString(),
                pendingReports = pending.toString(),
                reportsThisMonth = thisMonth.toString()
            )
        }
    }
    
    var isSigningOut by remember { mutableStateOf(false) }
    var isSavingProfile by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    var selectedModalTitle by remember { mutableStateOf("") }
    val modalSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentSubView,
            transitionSpec = {
                // Instant transition by setting duration to 0
                fadeIn(animationSpec = tween(0)) togetherWith fadeOut(animationSpec = tween(0))
            }, label = "ProfileViewTransition"
        ) { subView ->
            when (subView) {
                ProfileSubView.MAIN -> MainProfileView(
                    userData = userData,
                    onBack = onBack,
                    onEditClick = { currentSubView = ProfileSubView.EDIT },
                    onSettingsClick = { currentSubView = ProfileSubView.SETTINGS },
                    onFavoritesClick = { currentSubView = ProfileSubView.FAVORITES },
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToCommunity = onNavigateToCommunity,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "CitySync Profile")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out my CitySync profile! I've submitted ${userData.totalReports} reports and helped resolve ${userData.resolvedReports} issues. Download CitySync today!"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Profile"))
                    },
                    onSignOut = {
                        scope.launch {
                            isSigningOut = true
                            delay(1500)
                            onLogout() // Redirect to sign-in
                        }
                    },
                    notifBlue = NotifBlueLocal,
                    notifBg = NotifBgLocal,
                    notifTextDark = NotifTextDarkLocal,
                    notifTextMuted = NotifTextMutedLocal,
                    notifTextTimestamp = NotifTextTimestampLocal,
                    signOutRed = ProfileSignOutRedLocal,
                    emailMuted = ProfileEmailMutedLocal
                )
                ProfileSubView.EDIT -> EditProfileView(
                    initialData = userData,
                    isSaving = isSavingProfile,
                    onBack = { currentSubView = ProfileSubView.MAIN },
                    onSave = { newData ->
                        scope.launch {
                            isSavingProfile = true
                            try {
                                authManager.updateProfile(
                                    firstName = newData.firstName,
                                    lastName = newData.lastName,
                                    email = newData.email,
                                    phone = newData.phone,
                                    address = newData.address
                                )
                                userData = newData
                                snackbarHostState.showSnackbar("Profile updated successfully")
                                currentSubView = ProfileSubView.MAIN
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    "Failed to update profile: ${e.localizedMessage ?: e.message ?: "Unknown error"}"
                                )
                            } finally {
                                isSavingProfile = false
                            }
                        }
                    },
                    notifBlue = NotifBlueLocal,
                    notifBg = NotifBgLocal,
                    notifTextDark = NotifTextDarkLocal,
                    notifTextMuted = NotifTextMutedLocal
                )
                ProfileSubView.SETTINGS -> SettingsView(
                    userData = userData,
                    onBack = { currentSubView = ProfileSubView.MAIN },
                    onEditProfileClick = { currentSubView = ProfileSubView.EDIT },
                    onNavigateToPrivacy = { currentSubView = ProfileSubView.PRIVACY },
                    onNavigateToSecurity = { currentSubView = ProfileSubView.SECURITY },
                    onNavigateToHelp = { currentSubView = ProfileSubView.HELP },
                    onNavigateToContactSupport = { currentSubView = ProfileSubView.CONTACT_SUPPORT },
                    onNavigateToTerms = { currentSubView = ProfileSubView.TERMS },
                    onNavigateToPrivacyPolicy = { currentSubView = ProfileSubView.PRIVACY_POLICY },
                    onShowModal = { title -> selectedModalTitle = title; showModal = true },
                    notifBlue = NotifBlueLocal,
                    notifBg = NotifBgLocal,
                    notifTextDark = NotifTextDarkLocal,
                    notifTextMuted = NotifTextMutedLocal,
                    helpBg = ProfileHelpBgLocal,
                    emailMuted = ProfileEmailMutedLocal
                )
                ProfileSubView.PRIVACY -> PrivacySettingsScreen(
                    onBack = { currentSubView = ProfileSubView.SETTINGS },
                    onAccountDeleted = onLogout
                )
                ProfileSubView.SECURITY -> SecuritySettingsScreen(
                    onBack = { currentSubView = ProfileSubView.SETTINGS }
                )
                ProfileSubView.HELP -> HelpSupportScreen(
                    onBack = { currentSubView = ProfileSubView.SETTINGS },
                    onContactSupport = { currentSubView = ProfileSubView.CONTACT_SUPPORT }
                )
                ProfileSubView.CONTACT_SUPPORT -> ContactSupportScreen(
                    onBack = { currentSubView = ProfileSubView.SETTINGS },
                    onMessageSent = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Message sent successfully!")
                            currentSubView = ProfileSubView.SETTINGS
                        }
                    }
                )
                ProfileSubView.TERMS -> TermsOfServiceScreen(
                    onBack = { currentSubView = ProfileSubView.SETTINGS }
                )
                ProfileSubView.PRIVACY_POLICY -> PrivacyPolicyScreen(
                    onBack = { currentSubView = ProfileSubView.SETTINGS }
                )
                ProfileSubView.FAVORITES -> FavoritesListScreen(
                    onBack = { currentSubView = ProfileSubView.MAIN },
                    onNavigateToReportDetails = onNavigateToReportDetails
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
        )

        if (showModal) {
            ModalBottomSheet(
                onDismissRequest = { showModal = false },
                sheetState = modalSheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                SettingsModalContent(
                    title = selectedModalTitle,
                    onClose = { showModal = false },
                    notifBlue = NotifBlueLocal,
                    notifTextDark = NotifTextDarkLocal,
                    notifTextMuted = NotifTextMutedLocal
                )
            }
        }

        if (isSigningOut) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = NotifBlueLocal)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Signing out...", color = NotifTextDarkLocal, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun MainProfileView(
    userData: UserProfileData,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onShare: () -> Unit,
    onSignOut: () -> Unit,
    notifBlue: Color,
    notifBg: Color,
    notifTextDark: Color,
    notifTextMuted: Color,
    notifTextTimestamp: Color,
    signOutRed: Color,
    emailMuted: Color
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Handle image selection
    }

    Scaffold(
        containerColor = notifBg,
        topBar = {
            ProfileHeader(
                title = "Profile",
                bgColor = notifBlue,
                showShare = true,
                onShare = onShare
            )
        },
        bottomBar = { 
            StandardBottomNavBar(
                selectedTab = NavTab.PROFILE,
                onNavigateToHome = onBack,
                onNavigateToReports = onNavigateToReports,
                onNavigateToCommunity = onNavigateToCommunity,
                onNavigateToNotifications = onNavigateToNotifications
            ) 
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Summary Card
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar Group
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clickable { launcher.launch("image/*") }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(notifBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${userData.firstName.take(1)}${userData.lastName.take(1)}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(notifBlue)
                                        .border(2.dp, Color.White, CircleShape)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // User Bio Info
                            Column {
                                Text("${userData.firstName} ${userData.lastName}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = notifTextDark)
                                Text(userData.email, fontSize = 14.sp, color = emailMuted)
                                Text(userData.address, fontSize = 12.sp, color = notifTextTimestamp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = notifTextTimestamp, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Member since ${userData.memberSince}", fontSize = 12.sp, color = notifTextTimestamp)
                                }
                            }
                        }
                        
                        // Edit Button
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = notifTextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Analytics Metrics Row
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricItem(userData.totalReports, "Total", Icons.Default.Description, Color(0xFFE0F2F1), notifBlue, notifTextDark, notifTextMuted)
                        VerticalDivider()
                        MetricItem(userData.resolvedReports, "Resolved", Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF2E7D32), notifTextDark, notifTextMuted)
                        VerticalDivider()
                        MetricItem(userData.pendingReports, "Pending", Icons.Default.AccessTime, Color(0xFFFFF3E0), Color(0xFFEF6C00), notifTextDark, notifTextMuted)
                    }
                }
            }

            // Navigation Options
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NavigationRow(
                        icon = Icons.Default.Description,
                        label = "My Reports",
                        badge = if (userData.totalReports != "0") userData.totalReports else null,
                        onClick = onNavigateToReports,
                        notifBlue = notifBlue,
                        textDark = notifTextDark
                    )
                    NavigationRow(
                        icon = Icons.Default.Favorite,
                        label = "My Favorites",
                        onClick = onFavoritesClick,
                        notifBlue = notifBlue,
                        textDark = notifTextDark
                    )
                    NavigationRow(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        onClick = onSettingsClick,
                        notifBlue = notifBlue,
                        textDark = notifTextDark
                    )
                }
            }

            // Report Summary Data Card
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Report Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = notifTextDark)
                        Spacer(modifier = Modifier.height(16.dp))
                        SummaryRow("Reports this month", userData.reportsThisMonth, notifTextMuted, notifTextDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow("Average resolution time", "4.2 days", notifTextMuted, notifTextDark)
                    }
                }
            }

            // Sign Out Button
            item {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, signOutRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = signOutRed)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileView(
    initialData: UserProfileData,
    isSaving: Boolean = false,
    onBack: () -> Unit,
    onSave: (UserProfileData) -> Unit,
    notifBlue: Color,
    notifBg: Color,
    notifTextDark: Color,
    notifTextMuted: Color
) {
    var firstName by remember { mutableStateOf(initialData.firstName) }
    var lastName by remember { mutableStateOf(initialData.lastName) }
    var email by remember { mutableStateOf(initialData.email) }
    var phone by remember { mutableStateOf(initialData.phone) }
    var address by remember { mutableStateOf(initialData.address) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // In a real app, you would handle the URI and upload/persist it
        // For this UI mockup, we'll just simulate a successful selection
    }

    Scaffold(
        containerColor = notifBg,
        topBar = { ProfileHeader("Edit Profile", notifBlue, showBack = true, onBack = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Selection
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { launcher.launch("image/*") }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(notifBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${firstName.take(1)}${lastName.take(1)}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(notifBlue)
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Change Photo",
                        color = notifBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { launcher.launch("image/*") }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // Form Inputs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProfileTextField("First Name", firstName, onValueChange = { firstName = it }, notifBlue, notifTextMuted, Modifier.weight(1f))
                        ProfileTextField("Last Name", lastName, onValueChange = { lastName = it }, notifBlue, notifTextMuted, Modifier.weight(1f))
                    }
                    ProfileTextField("Email Address", email, onValueChange = { email = it }, notifBlue, notifTextMuted, icon = Icons.Default.Email)
                    ProfileTextField("Phone Number", phone, onValueChange = { phone = it }, notifBlue, notifTextMuted, icon = Icons.Default.Phone)
                    ProfileTextField("Address", address, onValueChange = { address = it }, notifBlue, notifTextMuted, icon = Icons.Default.LocationOn)
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            // Save Button
            item {
                Button(
                    onClick = { 
                        onSave(initialData.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phone = phone,
                            address = address
                        )) 
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = notifBlue)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    userData: UserProfileData,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToContactSupport: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onShowModal: (String) -> Unit,
    notifBlue: Color,
    notifBg: Color,
    notifTextDark: Color,
    notifTextMuted: Color,
    helpBg: Color,
    emailMuted: Color
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = notifBg,
        topBar = { ProfileHeader("Settings", notifBlue, showBack = true, onBack = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top User Card Mini-Preview
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(notifBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${userData.firstName.take(1)}${userData.lastName.take(1)}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${userData.firstName} ${userData.lastName}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = notifTextDark)
                                Text(userData.email, fontSize = 12.sp, color = emailMuted)
                            }
                            IconButton(onClick = onEditProfileClick, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = notifBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onEditProfileClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = notifBlue)
                        ) {
                            Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // PREFERENCES
            item {
                SettingsSection("PREFERENCES") {
                    SettingsToggleRow(
                        Icons.Default.Notifications, 
                        "Notifications", 
                        notificationsEnabled, 
                        onToggle = { notificationsEnabled = it },
                        notifBlue, 
                        notifTextDark
                    )
                }
            }

            // PRIVACY & SECURITY
            item {
                SettingsSection("PRIVACY & SECURITY") {
                    SettingsNavRow(Icons.Default.Lock, "Privacy Settings", notifBlue, notifTextDark, onClick = onNavigateToPrivacy)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SettingsNavRow(Icons.Default.Shield, "Security", notifBlue, notifTextDark, onClick = onNavigateToSecurity)
                }
            }

            // SUPPORT & LEGAL
            item {
                SettingsSection("SUPPORT & LEGAL") {
                    SettingsNavRow(Icons.Default.Help, "Help & Support", notifBlue, notifTextDark, onClick = onNavigateToHelp)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SettingsNavRow(Icons.Default.ChatBubble, "Contact Support", notifBlue, notifTextDark, onClick = onNavigateToContactSupport)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SettingsNavRow(Icons.Default.Description, "Terms of Service", notifBlue, notifTextDark, onClick = onNavigateToTerms)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SettingsNavRow(Icons.Default.GppGood, "Privacy Policy", notifBlue, notifTextDark, onClick = onNavigateToPrivacyPolicy)
                }
            }

            // App Information Metadata Card
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("App Information", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = notifTextDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        AppInfoRow("App Name", "CitySync", notifTextMuted)
                        AppInfoRow("Version", "1.0.0", notifTextMuted)
                        AppInfoRow("Build", "2026.06.10", notifTextMuted)
                    }
                }
            }

            // Need Help Widget
            item {
                Surface(
                    color = helpBg,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Need Help?", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = notifBlue)
                        Text("Contact our support team for assistance.", fontSize = 13.sp, color = notifTextMuted)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToContactSupport,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = notifBlue),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Contact Support", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
    }
}

@Composable
fun SettingsModalContent(
    title: String,
    onClose: () -> Unit,
    notifBlue: Color,
    notifTextDark: Color,
    notifTextMuted: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = notifTextDark)
        Spacer(modifier = Modifier.height(24.dp))
        
        when (title) {
            "Terms of Service" -> {
                LegalContentItem("1. Acceptance of Terms", "By accessing CitySync, you agree to comply with local municipality reporting guidelines and all relevant government regulations regarding public infrastructure maintenance and safety.")
                Spacer(modifier = Modifier.height(16.dp))
                LegalContentItem("2. User Conduct", "Users are prohibited from submitting misleading, duplicate, or fraudulent civic infrastructure maintenance reports. Misuse of the platform may lead to account suspension.")
            }
            "Privacy Policy" -> {
                LegalContentItem("Data Collection", "We map geographical location tracking pins and image attachments solely to verify valid civic repair reports. This data is essential for municipal dispatch services.")
                Spacer(modifier = Modifier.height(16.dp))
                LegalContentItem("Data Protection", "Personal account information, contact telephone metrics, and emails are securely hashed and never shared with third-party networks or advertising entities.")
            }
            "Help & Support", "Contact Support" -> {
                var supportMessage by remember { mutableStateOf("") }
                Text("How can we help you?", fontSize = 14.sp, color = notifTextMuted)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = supportMessage,
                    onValueChange = { supportMessage = it },
                    placeholder = { Text("Enter your message or issue description here...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = notifBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = notifBlue)
                ) {
                    Text("Submit Ticket", fontWeight = FontWeight.Bold)
                }
            }
            else -> {
                Text("This section contains configuration and details regarding $title. Please contact support if you have specific questions.", color = notifTextMuted, lineHeight = 22.sp)
            }
        }
    }
}

@Composable
fun LegalContentItem(header: String, body: String) {
    Column {
        Text(header, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(body, fontSize = 14.sp, color = Color(0xFF5A6B7C), lineHeight = 22.sp)
    }
}

// ── Sub-components ──────────────────────────────────────────────────────────

@Composable
fun ProfileHeader(
    title: String,
    bgColor: Color,
    showBack: Boolean = false,
    showShare: Boolean = false,
    onBack: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Surface(color = bgColor, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (showShare) {
                IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(count: String, label: String, icon: ImageVector, iconBg: Color, iconColor: Color, textDark: Color, textMuted: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
        Text(label, fontSize = 12.sp, color = textMuted)
    }
}

@Composable
fun VerticalDivider() {
    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFF1F5F9)))
}

@Composable
fun NavigationRow(icon: ImageVector, label: String, badge: String? = null, onClick: () -> Unit, notifBlue: Color, textDark: Color) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = notifBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = textDark, modifier = Modifier.weight(1f))
            if (badge != null) {
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badge, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, textMuted: Color, textDark: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = textMuted)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
    }
}

@Composable
fun ProfileTextField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit,
    notifBlue: Color, 
    textMuted: Color, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textMuted, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = if (icon != null) { { Icon(icon, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) } } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = notifBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector, 
    label: String, 
    checked: Boolean, 
    onToggle: (Boolean) -> Unit,
    notifBlue: Color, 
    textDark: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = notifBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 15.sp, color = textDark, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = notifBlue,
                uncheckedTrackColor = Color(0xFFCBD5E1),
                uncheckedThumbColor = Color.White,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingsNavRow(icon: ImageVector, label: String, notifBlue: Color, textDark: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = notifBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 15.sp, color = textDark, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun AppInfoRow(label: String, value: String, textMuted: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = textMuted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

// StandardBottomNavBar used instead of ProfileBottomNavBar

@Preview(showBackground = true)
@Composable
fun MainProfileViewPreview() {
    val blue = Color(0xFF0D4E89)
    val bg = Color(0xFFF4F6F9)
    val dark = Color(0xFF1A202C)
    val muted = Color(0xFF5A6B7C)
    val timestamp = Color(0xFFA0AEC0)
    val red = Color(0xFFDC2626)
    val emailMuted = Color(0xFF7A8B9C)
    
    val data = UserProfileData("Raiden", "Villapando", "raiden@email.com", "123", "Manila")
    
    CitySyncTheme {
        MainProfileView(
            userData = data,
            onBack = {},
            onEditClick = {},
            onSettingsClick = {},
            onFavoritesClick = {},
            onNavigateToReports = {},
            onNavigateToCommunity = {},
            onNavigateToNotifications = {},
            onShare = {},
            onSignOut = {},
            notifBlue = blue,
            notifBg = bg,
            notifTextDark = dark,
            notifTextMuted = muted,
            notifTextTimestamp = timestamp,
            signOutRed = red,
            emailMuted = emailMuted
        )
    }
}
