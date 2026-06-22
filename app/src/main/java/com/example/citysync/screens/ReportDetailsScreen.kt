package com.example.citysync.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ReportComment(
    val author: String,
    val initials: String,
    val timestamp: String,
    val message: String,
    val isOfficial: Boolean = false
)

@Composable
fun ReportDetailsScreen(
    reportId: String = "REP-2026-001234",
    initialFocusComment: Boolean = false,
    onBack: () -> Unit = {},
    onTrackStatus: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }
    var isCommenting by remember { mutableStateOf(initialFocusComment) }
    var isTracking by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun showToast(message: String) {
        toastMessage = message
        scope.launch {
            delay(2000)
            toastMessage = null
        }
    }

    val comments = remember {
        mutableStateListOf(
            ReportComment(
                author = "City Maintenance Team",
                initials = "CM",
                timestamp = "June 7, 2026",
                message = "We have dispatched a team to assess and repair the streetlight.",
                isOfficial = true
            )
        )
    }

    LaunchedEffect(initialFocusComment) {
        if (initialFocusComment) {
            isCommenting = true
        }
    }

    // Rigid mobile container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
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
                        IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Report Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Box {
                            IconButton(onClick = { isMenuExpanded = true }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = isMenuExpanded,
                                onDismissRequest = { isMenuExpanded = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Share Post", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Icon(Icons.Default.Share, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                                    onClick = { 
                                        isMenuExpanded = false
                                        showToast("Link copied to clipboard!")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Icon(Icons.Default.BookmarkBorder, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                                    onClick = { 
                                        isMenuExpanded = false
                                        showToast("Report saved to your profile!")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Report Post", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Icon(Icons.Default.OutlinedFlag, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                                    onClick = { 
                                        isMenuExpanded = false
                                        showToast("Report submitted for review.")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Contact Support", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Icon(Icons.Default.HeadsetMic, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                                    onClick = { 
                                        isMenuExpanded = false
                                        onContactSupport()
                                    }
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Column {
                    // Sticky Action Buttons Row
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF1F5F9), RectangleShape),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showToast("Link copied to clipboard!") },
                                modifier = Modifier.weight(0.45f).height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.LightGray)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF1A202C), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Report", color = Color(0xFF1A202C), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = onTrackStatus,
                                modifier = Modifier
                                    .weight(0.55f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTracking) Color(0xFF137333) else Color(0xFF0D4E89)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isTracking) Icons.Default.Check else Icons.Default.OutlinedFlag,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isTracking) "Tracking" else "Track Status", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                    
                    StandardBottomNavBar(
                        selectedTab = NavTab.REPORTS,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToReports = onNavigateToReports,
                        onNavigateToCommunity = onNavigateToCommunity,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToProfile = onNavigateToProfile
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primary Info Card
                item {
                    Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), shadowElevation = 1.dp) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Broken Streetlight on Main Street", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A202C), modifier = Modifier.weight(1f))
                                Surface(color = Color(0xFFFFF7ED), shape = RoundedCornerShape(50)) {
                                    Text("In Progress", color = Color(0xFFEA580C), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                BadgePill("Lighting", Color(0xFFF1F3F5), Color(0xFF5A6B7C))
                                BadgePill("High Priority", Color(0xFFFCE8E6), Color(0xFFC5221F))
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(16.dp))
                            MetaRow(Icons.Default.CalendarToday, "Reported on", "June 4, 2026")
                            MetaRow(Icons.Default.Person, "Reported by", "Raiden Villapando")
                            MetaRow(Icons.Default.Place, "Location", "Main Street cor. 5th Avenue, Barangay 5, Manila")
                            MetaRow(Icons.Default.Flag, "Reference", reportId)
                        }
                    }
                }

                // Description Card
                item {
                    SectionCard("Description") {
                        Text(
                            "The streetlight near the corner of Main Street and 5th Avenue has been malfunctioning for the past week. It flickers intermittently during the night, creating a safety hazard for pedestrians and motorists.",
                            fontSize = 14.sp, color = Color(0xFF5A6B7C), lineHeight = 22.sp
                        )
                    }
                }

                // Photos Card
                item {
                    SectionCard("Photos") {
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) {
                                Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF1F3F5)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                                }
                            }
                        }
                    }
                }

                // Location Card Section
                item {
                    Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), shadowElevation = 1.dp) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF0D4E89), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Surface(
                                color = Color(0xFFF1F3F5),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    "Main Street cor. 5th Avenue, Barangay 5, Manila",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 13.sp,
                                    color = Color(0xFF1A202C)
                                )
                            }
                        }
                    }
                }

                // Progress Updates Timeline Card
                item {
                    Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), shadowElevation = 1.dp) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Progress Updates", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A202C))
                                Text("Full Timeline", color = Color(0xFF0D4E89), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            TimelineItem("In Progress", "June 7, 2026", "A maintenance team has been assigned to this report.", true)
                            TimelineItem("Assigned", "June 6, 2026", null, false)
                        }
                    }
                }

                // Functional Comments Section
                item {
                    Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), shadowElevation = 1.dp) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Comments (${comments.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A202C))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            comments.forEach { comment ->
                                CommentBubble(
                                    author = comment.author,
                                    initials = comment.initials,
                                    timestamp = comment.timestamp,
                                    message = comment.message,
                                    isOfficial = comment.isOfficial
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            if (isCommenting) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = commentInput,
                                        onValueChange = { commentInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        placeholder = { Text("Write a comment...", fontSize = 14.sp, color = Color.LightGray) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF0D4E89),
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedButton(
                                            onClick = { isCommenting = false; commentInput = "" },
                                            modifier = Modifier.weight(1f).height(40.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                        ) {
                                            Icon(Icons.Default.Close, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Cancel", color = Color(0xFF1A202C), fontSize = 13.sp)
                                        }
                                        Button(
                                            onClick = {
                                                if (commentInput.isNotBlank()) {
                                                    comments.add(
                                                        ReportComment(
                                                            author = "Raiden Villapando",
                                                            initials = "RV",
                                                            timestamp = "Just now",
                                                            message = commentInput
                                                        )
                                                    )
                                                    commentInput = ""
                                                    isCommenting = false
                                                }
                                            },
                                            modifier = Modifier.weight(1f).height(40.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D4E89))
                                        ) {
                                            Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Post", color = Color.White, fontSize = 13.sp)
                                        }
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clickable { isCommenting = true },
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Add a comment...", color = Color.Gray, fontSize = 14.sp)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Footer Support Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onContactSupport() },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.HeadsetMic, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Contact Support", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        // Toast Message Overlay
        toastMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 140.dp), // Positioned above bottom bars
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
fun BadgePill(label: String, bgColor: Color, textColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(6.dp)) {
        Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun MetaRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0D4E89), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF718096), fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, color = Color(0xFF1A202C), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A202C))
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun TimelineItem(status: String, date: String, note: String?, isLast: Boolean) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (note != null) Color(0xFF0D4E89) else Color.LightGray))
            if (!isLast) {
                Box(modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            Text("$status · $date", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A202C))
            if (note != null) {
                Text(note, fontSize = 13.sp, color = Color(0xFF718096), modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun CommentBubble(author: String, initials: String, timestamp: String, message: String, isOfficial: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isOfficial) Color(0xFF0D4E89) else Color(0xFF64748B)),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(author, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A202C))
                Spacer(modifier = Modifier.width(8.dp))
                Text(timestamp, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = Color(0xFFF0F2F5), shape = RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp)) {
                Text(message, modifier = Modifier.padding(12.dp), fontSize = 14.sp, color = Color(0xFF4A5568))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ReportDetailsPreview() {
    CitySyncTheme {
        ReportDetailsScreen()
    }
}
