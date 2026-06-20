package com.example.citysync.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.theme.*

enum class EmergencySubView {
    HUB, SMS, CALL, FORM
}

enum class EmergencyType(val label: String, val icon: ImageVector, val color: Color) {
    CRIME("Report Crime", Icons.Default.Shield, Color(0xFFE3F2FD)),
    MEDICAL("Medical Help", Icons.Default.MedicalServices, Color(0xFFFCE8E6)),
    FIRE("Fire Emergency", Icons.Default.LocalFireDepartment, Color(0xFFFFF3E0)),
    DISASTER("Natural Disaster", Icons.Default.Flood, Color(0xFFE8F5E9))
}

data class EmergencyContact(val name: String, val relationship: String, val phone: String)
data class ChatMessage(val text: String, val isUser: Boolean, val timestamp: String)

@Composable
fun EmergencyFlow(onBack: () -> Unit = {}) {
    var currentView by remember { mutableStateOf(EmergencySubView.HUB) }
    var activeEmergencyType by remember { mutableStateOf(EmergencyType.MEDICAL) }
    var contacts by remember { mutableStateOf(listOf(EmergencyContact("Jane Doe", "Spouse", "+63 912 345 6789"))) }

    AnimatedContent(
        targetState = currentView,
        transitionSpec = {
            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
        }, label = "EmergencyViewTransition"
    ) { subView ->
        when (subView) {
            EmergencySubView.HUB -> EmergencyHubView(
                onBack = onBack,
                onSelectSMS = { type -> activeEmergencyType = type; currentView = EmergencySubView.SMS },
                onSelectCall = { type -> activeEmergencyType = type; currentView = EmergencySubView.CALL },
                onAddContact = { currentView = EmergencySubView.FORM },
                contacts = contacts
            )
            EmergencySubView.SMS -> EmergencySMSChatView(
                type = activeEmergencyType,
                onBack = { currentView = EmergencySubView.HUB }
            )
            EmergencySubView.CALL -> ActiveCallOverlay(
                type = activeEmergencyType,
                onHangUp = { currentView = EmergencySubView.HUB }
            )
            EmergencySubView.FORM -> AddEmergencyContactView(
                onBack = { currentView = EmergencySubView.HUB },
                onSave = { newContact ->
                    contacts = contacts + newContact
                    currentView = EmergencySubView.HUB
                }
            )
        }
    }
}

@Composable
fun EmergencyHubView(
    onBack: () -> Unit,
    onSelectSMS: (EmergencyType) -> Unit,
    onSelectCall: (EmergencyType) -> Unit,
    onAddContact: () -> Unit,
    contacts: List<EmergencyContact>
) {
    Scaffold(
        containerColor = NotifBg,
        topBar = { EmergencyHeader("Emergency Assistance", onBack = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SOS Button
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val infiniteTransition = rememberInfiniteTransition(label = "SOSPulse")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = "SOSScale"
                    )

                    Surface(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(scale)
                            .clickable { onSelectCall(EmergencyType.MEDICAL) },
                        shape = CircleShape,
                        color = ProfileSignOutRed,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("SOS", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                "TAP FOR IMMEDIATE HELP",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Text(
                    "Quick Emergency Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NotifTextDark
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmergencyType.values().forEach { type ->
                        EmergencyActionCard(
                            type = type,
                            onCall = { onSelectCall(type) },
                            onSMS = { onSelectSMS(type) }
                        )
                    }
                }
            }

            // Trusted Contacts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Trusted Contacts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NotifTextDark
                    )
                    IconButton(onClick = onAddContact) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Contact", tint = NotifBlue)
                    }
                }
            }

            items(contacts) { contact ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NotifFilterInactive),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NotifTextMuted)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NotifTextDark)
                            Text(contact.relationship, fontSize = 12.sp, color = NotifTextMuted)
                        }
                        IconButton(onClick = { /* Call Specific Contact */ }) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = NotifBlue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyActionCard(type: EmergencyType, onCall: () -> Unit, onSMS: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(type.color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(type.icon, contentDescription = null, tint = if (type == EmergencyType.MEDICAL) Color.Red else NotifBlue)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(type.label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NotifTextDark)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NotifFilterInactive, contentColor = NotifTextDark)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onSMS,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NotifFilterInactive, contentColor = NotifTextDark)
                ) {
                    Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Text / SMS", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun EmergencySMSChatView(type: EmergencyType, onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val chatHistory = remember {
        mutableStateListOf(
            ChatMessage("Emergency Dispatcher connected. Please state your emergency.", false, "10:00 AM"),
            ChatMessage("I need medical assistance at 123 Main St.", true, "10:01 AM")
        )
    }

    Scaffold(
        containerColor = NotifBg,
        topBar = {
            Surface(color = NotifBlue, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("SMS: ${type.label}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 16.dp) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type emergency message...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 100.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = NotifFilterInactive,
                            focusedBorderColor = NotifBlue
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                chatHistory.add(ChatMessage(messageText, true, "Now"))
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NotifBlue)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatHistory) { message ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart) {
                    Surface(
                        color = if (message.isUser) NotifBlue else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isUser) 16.dp else 0.dp,
                            bottomEnd = if (message.isUser) 0.dp else 16.dp
                        )
                    ) {
                        Text(
                            text = message.text,
                            color = if (message.isUser) Color.White else NotifTextDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveCallOverlay(type: EmergencyType, onHangUp: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val infiniteTransition = rememberInfiniteTransition(label = "CallPulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "PulseAlpha"
            )

            Text(
                "Calling Emergency Hotline...",
                color = Color.White.copy(alpha = alpha),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "555-0199",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                "(MOCK SIMULATOR NUMBER)",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Dialer Controls
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                DialerButton(Icons.Default.MicOff, "Mute")
                DialerButton(Icons.Default.Grid4x4, "Keypad")
                DialerButton(Icons.Default.VolumeUp, "Speaker")
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Hangup
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onHangUp() },
                shape = CircleShape,
                color = Color(0xFFE53E3E)
            ) {
                Icon(
                    Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun DialerButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.padding(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun AddEmergencyContactView(onBack: () -> Unit, onSave: (EmergencyContact) -> Unit) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        containerColor = NotifBg,
        topBar = { EmergencyHeader("Add Emergency Contact", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    EmergencyFormField("Contact Name *", "E.g., Jane Doe", name) { name = it }
                    EmergencyFormField("Relationship *", "E.g., Spouse, Parent, Guardian", relationship) { relationship = it }
                    EmergencyFormField("Phone Number *", "E.g., +63 9XX XXX XXXX", phone) { phone = it }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank() && relationship.isNotBlank() && phone.isNotBlank()) {
                        onSave(EmergencyContact(name, relationship, phone))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NotifBlue)
            ) {
                Text("Save Contact", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun EmergencyFormField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NotifTextDark)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = NotifTextMuted, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = NotifFilterInactive,
                focusedBorderColor = NotifBlue
            ),
            singleLine = true
        )
    }
}

@Composable
fun EmergencyHeader(title: String, onBack: () -> Unit) {
    Surface(color = NotifBlue, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyHubPreview() {
    CitySyncTheme {
        EmergencyFlow()
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencySMSPreview() {
    CitySyncTheme {
        EmergencySMSChatView(EmergencyType.MEDICAL) {}
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyCallPreview() {
    CitySyncTheme {
        ActiveCallOverlay(EmergencyType.MEDICAL) {}
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyFormPreview() {
    CitySyncTheme {
        AddEmergencyContactView({}, {})
    }
}
