package com.example.citysync.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*

enum class EmergencyView {
    HUB, REQUEST_FORM, DIALER, ADD_CONTACT
}

enum class ActionType(val label: String, val icon: ImageVector) {
    CRIME("Report Crime", Icons.Default.Shield),
    MEDICAL("Medical Help", Icons.Default.MedicalServices),
    FIRE("Fire Alert", Icons.Default.LocalFireDepartment),
    LOCATION("Send Location", Icons.Default.Place)
}

data class HotlineData(
    val title: String,
    val subtitle: String,
    val number: String,
    val icon: ImageVector,
    val iconColor: Color,
    val actionType: ActionType,
    val buttonColor: Color = Color(0xFF0D4E89)
)

data class EmergencyContact(val name: String, val relationship: String, val phone: String)

@Composable
fun EmergencyFlow(
    onBack: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var currentView by remember { mutableStateOf(EmergencyView.HUB) }
    var selectedAction by remember { mutableStateOf(ActionType.MEDICAL) }
    var activeHotline by remember { mutableStateOf<HotlineData?>(null) }
    var contacts by remember { mutableStateOf(listOf(EmergencyContact("Raiden Villapando", "You", "+63 912 345 6789"))) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F9))) {
        AnimatedContent(
            targetState = currentView,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "EmergencyTransition"
        ) { view ->
            when (view) {
                EmergencyView.HUB -> EmergencyHub(
                    onBack = onBack,
                    onActionSelect = { action -> selectedAction = action; currentView = EmergencyView.REQUEST_FORM },
                    onCallHotline = { hotline -> activeHotline = hotline; currentView = EmergencyView.DIALER },
                    onChatHotline = { action -> selectedAction = action; currentView = EmergencyView.REQUEST_FORM },
                    onAddContact = { currentView = EmergencyView.ADD_CONTACT },
                    contacts = contacts,
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToCommunity = onNavigateToCommunity,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToProfile = onNavigateToProfile
                )
                EmergencyView.REQUEST_FORM -> EmergencyRequestForm(
                    action = selectedAction,
                    onBack = { currentView = EmergencyView.HUB },
                    onSubmit = { currentView = EmergencyView.HUB }
                )
                EmergencyView.DIALER -> activeHotline?.let {
                    ActiveDialerOverlay(hotline = it, onHangUp = { currentView = EmergencyView.HUB })
                }
                EmergencyView.ADD_CONTACT -> AddContactView(
                    onBack = { currentView = EmergencyView.HUB },
                    onSave = { newContact -> 
                        contacts = contacts + newContact
                        currentView = EmergencyView.HUB 
                    },
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToCommunity = onNavigateToCommunity,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToProfile = onNavigateToProfile
                )
            }
        }
    }
}

@Composable
fun EmergencyHub(
    onBack: () -> Unit,
    onActionSelect: (ActionType) -> Unit,
    onCallHotline: (HotlineData) -> Unit,
    onChatHotline: (ActionType) -> Unit,
    onAddContact: () -> Unit,
    contacts: List<EmergencyContact>,
    onNavigateToReports: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF4F6F9),
        topBar = {
            Surface(color = Color(0xFF0D4E89), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Emergency Assistance", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            StandardBottomNavBar(
                selectedTab = NavTab.HOME,
                onNavigateToHome = onBack,
                onNavigateToReports = onNavigateToReports,
                onNavigateToCommunity = onNavigateToCommunity,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Red Alert Banner
            item {
                Surface(
                    color = Color(0xFFDC2626),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Emergency Alert", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Get immediate help", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { onCallHotline(HotlineData("General Emergency", "Immediate help", "911", Icons.Default.Call, Color(0xFF0D4E89), ActionType.MEDICAL)) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Call 911", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            OutlinedButton(
                                onClick = { onActionSelect(ActionType.LOCATION) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Text Alert", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Column {
                    Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A202C))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionType.entries.forEach { action ->
                            QuickActionCard(action = action, onClick = { onActionSelect(action) })
                        }
                    }
                }
            }

            // Emergency Hotlines
            item {
                Column {
                    Text("Emergency Hotlines", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A202C))
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        HotlineCard(
                            data = HotlineData("Police Emergency", "For crimes, accidents, and urgent police matters", "911", Icons.Default.Shield, Color(0xFF0D4E89), ActionType.CRIME),
                            onCall = onCallHotline,
                            onChat = onChatHotline
                        )
                        HotlineCard(
                            data = HotlineData("Fire Department", "For fires and rescue operations", "(02) 426-0219", Icons.Default.LocalFireDepartment, Color(0xFFDC2626), ActionType.FIRE),
                            onCall = onCallHotline,
                            onChat = onChatHotline
                        )
                        HotlineCard(
                            data = HotlineData("Medical Emergency", "For medical emergencies and ambulance", "(02) 143", Icons.Default.MedicalServices, Color(0xFF2E7D32), ActionType.MEDICAL),
                            onCall = onCallHotline,
                            onChat = onChatHotline
                        )
                        HotlineCard(
                            data = HotlineData("Disaster Response", "For natural disasters and calamities", "(02) 911-1406", Icons.Default.Warning, Color(0xFFEF6C00), ActionType.LOCATION),
                            onCall = onCallHotline,
                            onChat = onChatHotline
                        )
                    }
                }
            }

            // Warning Banner
            item {
                Surface(
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Only use emergency services for genuine emergencies. Misuse may result in penalties and delays for real emergencies.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Emergency Contacts
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Emergency Contacts", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A202C))
                            Text("+ Add", color = Color(0xFF0D4E89), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { onAddContact() })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        contacts.forEach { contact ->
                            ContactItem(contact)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // Dashed Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { onAddContact() }
                                .drawBehind {
                                    drawRoundRect(
                                        color = Color.LightGray,
                                        style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add an emergency contact", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(action: ActionType, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(100.dp).clickable { onClick() },
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(action.icon, contentDescription = null, tint = Color(0xFF0D4E89), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(action.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun HotlineCard(data: HotlineData, onCall: (HotlineData) -> Unit, onChat: (ActionType) -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(44.dp).background(Color(0xFFF4F6F9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(data.icon, contentDescription = null, tint = data.iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(data.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A202C))
                Text(data.subtitle, fontSize = 12.sp, color = Color(0xFF7A8B9C), lineHeight = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onCall(data) },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = data.buttonColor),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(data.number, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(40.dp).clickable { onChat(data.actionType) },
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ContactItem(contact: EmergencyContact) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFF0D4E89), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(contact.name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A202C))
            Text("${contact.phone} · ${contact.relationship}", fontSize = 12.sp, color = Color(0xFF7A8B9C))
        }
        Surface(
            modifier = Modifier.size(32.dp).clickable { /* Call */ },
            color = Color(0xFFE8F5E9),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EmergencyRequestForm(action: ActionType, onBack: () -> Unit, onSubmit: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFFF4F6F9),
        topBar = {
            Surface(color = Color(0xFF0D4E89), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Emergency Request", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            // Dynamic Context Banner
            Surface(
                color = Color(0xFFFFF1F2),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).background(Color(0xFFDC2626), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(action.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(action.label, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A202C))
                        Text("Fill in details to send your emergency alert", color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                    IconButton(onClick = onBack, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormInput("Your Name", "Raiden Villapando")
                    FormInput("Phone Number", "+63 912 345 6789")
                    FormInput("Location / Address", "Manila, Philippines")
                    Column {
                        Text("Situation Details (optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("Briefly describe the emergency situation...", color = Color.LightGray, fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(120.dp).padding(top = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFF1F5F9))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Send Emergency Alert", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "By submitting this alert, emergency services will be notified. Only use for genuine emergencies.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FormInput(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFF1F5F9), focusedBorderColor = Color(0xFFF1F5F9))
        )
    }
}

@Composable
fun ActiveDialerOverlay(hotline: HotlineData, onHangUp: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A202C)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(120.dp).background(Color(0xFFDC2626), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(hotline.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(hotline.subtitle, color = Color.LightGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(hotline.number, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 32.sp, letterSpacing = 1.sp)
            Text("(SIMULATED CALL)", color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)

            Spacer(modifier = Modifier.height(100.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                DialerControl(Icons.Default.MicOff, "Mute")
                Surface(
                    modifier = Modifier.size(72.dp).clickable { onHangUp() },
                    color = Color(0xFFDC2626),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                DialerControl(Icons.Default.VolumeUp, "Speaker")
            }
        }
    }
}

@Composable
fun DialerControl(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(56.dp), color = Color.White.copy(alpha = 0.1f), shape = CircleShape) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}

@Composable
fun AddContactView(
    onBack: () -> Unit,
    onSave: (EmergencyContact) -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rel by remember { mutableStateOf("Friend") }
    var phone by remember { mutableStateOf("") }
    var altPhone by remember { mutableStateOf("") }
    var isRelExpanded by remember { mutableStateOf(false) }

    val relationships = listOf("Friend", "Family", "Spouse", "Parent", "Other")

    Scaffold(
        containerColor = Color(0xFFF4F6F9),
        topBar = {
            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Add Emergency Contact", color = Color(0xFF1A202C), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
            }
        },
        bottomBar = {
            StandardBottomNavBar(
                selectedTab = NavTab.HOME,
                onNavigateToHome = onBack,
                onNavigateToReports = onNavigateToReports,
                onNavigateToCommunity = onNavigateToCommunity,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                FormFieldCard("Full Name *", "Contact full name", name) { name = it }
                
                // Relationship Dropdown
                Column {
                    Text("Relationship *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A202C))
                    Box(modifier = Modifier.padding(top = 4.dp)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { isRelExpanded = true },
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(rel, color = Color(0xFF1A202C), fontSize = 14.sp)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
                            }
                        }
                        DropdownMenu(
                            expanded = isRelExpanded,
                            onDismissRequest = { isRelExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                        ) {
                            relationships.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r) },
                                    onClick = {
                                        rel = r
                                        isRelExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                FormFieldCard("Mobile Number *", "+63 9XX XXX XXXX", phone) { phone = it }
                FormFieldCard("Alternative Number (optional)", "+63 9XX XXX XXXX", altPhone) { altPhone = it }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { if (name.isNotBlank() && phone.isNotBlank()) onSave(EmergencyContact(name, rel, phone)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D4E89)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Contact", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FormFieldCard(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFF1F5F9))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A202C))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color(0xFF7A8B9C), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyModulePreview() {
    CitySyncTheme {
        EmergencyFlow()
    }
}
