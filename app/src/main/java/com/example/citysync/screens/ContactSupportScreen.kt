package com.example.citysync.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.CitySyncTheme
import com.example.citysync.ui.theme.DeepNavy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var name by remember { mutableStateOf("Raiden Villapando") }
    var email by remember { mutableStateOf("raiden.villapando@email.com") }
    var selectedCategory by remember { mutableStateOf("General Inquiry") }
    var message by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val categories = listOf(
        "General Inquiry",
        "Technical Issue",
        "Report Problem",
        "Account Help",
        "Feedback",
        "Other"
    )

    fun showToast(msg: String) {
        toastMessage = msg
        scope.launch {
            delay(2000)
            toastMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFFF4F6F9),
            topBar = {
                Surface(color = Color(0xFF0D4E89), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { onBack() },
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Contact Support",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            bottomBar = {
                StandardBottomNavBar(
                    selectedTab = NavTab.PROFILE, // Or neutral
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToCommunity = onNavigateToCommunity,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToProfile = onNavigateToProfile
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Your Name Input
                SupportField(label = "Your Name") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = supportTextFieldColors()
                    )
                }

                // Email Address Input
                SupportField(label = "Email Address") {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = supportTextFieldColors()
                    )
                }

                // Category Selection Input
                SupportField(label = "Category") {
                    Box {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { isDropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(selectedCategory, color = Color(0xFF1A202C), fontSize = 14.sp)
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .background(Color.White)
                        ) {
                            categories.forEach { category ->
                                val isSelected = category == selectedCategory
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            category, 
                                            color = if (isSelected) Color(0xFF0D4E89) else Color(0xFF1A202C),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    onClick = {
                                        selectedCategory = category
                                        isDropdownExpanded = false
                                    },
                                    modifier = Modifier.background(
                                        if (isSelected) Color(0xFFE3F2FD) else Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }

                // Message Textarea Input
                SupportField(label = "Message") {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { 
                            Text(
                                "Describe your issue or question in detail...", 
                                color = Color.LightGray,
                                fontSize = 14.sp
                            ) 
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = supportTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Submission Footer CTA Button
                Button(
                    onClick = { 
                        if (message.isNotBlank()) {
                            showToast("Message sent successfully!")
                            scope.launch {
                                delay(2000)
                                onBack()
                            }
                        } else {
                            showToast("Please enter a message.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D4E89)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // Toast Message Overlay
        toastMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    color = Color(0xFF1F2937),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 8.dp
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

@Composable
fun SupportField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A202C)
        )
        content()
    }
}

@Composable
fun supportTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = Color(0xFFE2E8F0),
    focusedBorderColor = Color(0xFF0D4E89),
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White
)

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ContactSupportPreview() {
    CitySyncTheme {
        ContactSupportScreen()
    }
}
