package com.example.citysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onBack: () -> Unit = {}
) {
    var showPasswordSheet by remember { mutableStateOf(false) }
    var twoFactorEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var loginAlertsEnabled by remember { mutableStateOf(true) }
    
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Security Settings",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0D4E89)
                )
            )
        },
        containerColor = Color(0xFFF4F6F9)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Score Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D4E89)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Account Security", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text("Raiden Villapando", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("60%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = Color(0xFF4ADE80),
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        "Enable 2FA to improve your security score.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }

            // Security Actions
            SecuritySectionTitle("General Security")
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SecurityActionRow(
                        icon = Icons.Outlined.Lock,
                        title = "Change Password",
                        onClick = { showPasswordSheet = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SecurityToggleRow(
                        icon = Icons.Outlined.Security,
                        title = "Two-Factor Authentication",
                        checked = twoFactorEnabled,
                        onCheckedChange = { twoFactorEnabled = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                    SecurityToggleRow(
                        icon = Icons.Default.Fingerprint,
                        title = "Biometric Login",
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it }
                    )
                }
            }

            SecuritySectionTitle("Alerts")
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                SecurityToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Login Alerts",
                    checked = loginAlertsEnabled,
                    onCheckedChange = { loginAlertsEnabled = it }
                )
            }

            // Active Sessions
            SecuritySectionTitle("Active Sessions")
            SessionCard(
                icon = Icons.Outlined.PhoneAndroid,
                device = "Android Phone",
                location = "Naga City, PH · Now",
                isCurrent = true
            )
            SessionCard(
                icon = Icons.Outlined.Laptop,
                device = "Chrome on Windows",
                location = "Manila, PH · 2 days ago",
                onRevoke = {}
            )

            // Recent Activity
            SecuritySectionTitle("Recent Login Activity")
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ActivityRow("Successful login", "Android Phone", "Today, 10:24 AM", true)
                    ActivityRow("Failed login attempt", "Chrome on Windows", "Yesterday, 08:15 PM", false)
                    ActivityRow("Successful login", "Android Phone", "Oct 24, 2026", true)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showPasswordSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPasswordSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            ChangePasswordContent(onClose = { showPasswordSheet = false })
        }
    }
}

@Composable
fun SecuritySectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF718096),
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
fun SecurityActionRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF0D4E89), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF1A202C))
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFCBD5E0))
    }
}

@Composable
fun SecurityToggleRow(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF0D4E89), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF1A202C))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0D4E89)
            )
        )
    }
}

@Composable
fun SessionCard(icon: ImageVector, device: String, location: String, isCurrent: Boolean = false, onRevoke: (() -> Unit)? = null) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(device, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                            Text("This device", color = Color(0xFF166534), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(location, fontSize = 13.sp, color = Color(0xFF64748B))
            }
            if (onRevoke != null) {
                Text(
                    "Revoke",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onRevoke() }
                )
            }
        }
    }
}

@Composable
fun ActivityRow(title: String, device: String, time: String, success: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(8.dp).background(if (success) Color(0xFF22C55E) else Color.Red, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text("$device · $time", fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun ChangePasswordContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Change Password", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Current Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Confirm New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D4E89)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Update Password", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Preview
@Composable
fun SecuritySettingsScreenPreview() {
    CitySyncTheme {
        SecuritySettingsScreen()
    }
}
