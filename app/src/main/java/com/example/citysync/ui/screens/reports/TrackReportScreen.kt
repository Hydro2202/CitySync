package com.example.citysync.ui.screens.reports

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.R
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*

@Composable
fun TrackReportScreen(
    reportId: String = "REP-2026-001234",
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onContactSupport: () -> Unit = {}
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
                    Text("Track Report", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            StandardBottomNavBar(
                selectedTab = NavTab.REPORTS,
                onNavigateToHome = onNavigateToHome,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Highlight Progress Card
            item {
                Surface(
                    color = Color(0xFF0D4E89),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("Reference Number", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                Text(reportId, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(color = Color(0xFFFFF7ED), shape = RoundedCornerShape(50)) {
                                Text(
                                    "In Progress",
                                    color = Color(0xFFEA580C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progress", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("67%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.67f)
                                    .fillMaxHeight()
                                    .background(Color.White)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Estimated completion: June 10, 2026", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                }
            }

            // Report Meta-Card Stub
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.brokenlight),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Broken Streetlight on Main Street", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A202C))
                            Text("Main Street cor. 5th Avenue, Brgy. 5", fontSize = 13.sp, color = Color(0xFF718096))
                        }
                    }
                }
            }

            // Status Timeline Component Block
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Report Timeline", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A202C))
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        TrackTimelineItem(
                            title = "Submitted",
                            subtitle = "Your report has been successfully submitted to the city government.",
                            timestamp = "June 4, 2026 — 10:30 AM",
                            icon = Icons.Default.Check,
                            iconBg = Color(0xFF137333),
                            isCompleted = true
                        )
                        TrackTimelineItem(
                            title = "Under Review",
                            subtitle = "City officials are reviewing your report and verifying the information.",
                            timestamp = "June 5, 2026 — 2:15 PM",
                            icon = Icons.Default.AccessTime,
                            iconBg = Color(0xFF137333),
                            isCompleted = true
                        )
                        TrackTimelineItem(
                            title = "Assigned",
                            subtitle = "Report has been assigned to City Maintenance Team for resolution.",
                            timestamp = "June 6, 2026 — 9:45 AM",
                            icon = Icons.Default.Person,
                            iconBg = Color(0xFF137333),
                            isCompleted = true
                        )
                        TrackTimelineItem(
                            title = "In Progress",
                            subtitle = "Maintenance team is currently working on resolving the issue.",
                            timestamp = "June 7, 2026 — 11:20 AM",
                            icon = Icons.Default.Build,
                            iconBg = Color(0xFFEA580C),
                            isActive = true,
                            isCompleted = false
                        )
                        TrackTimelineItem(
                            title = "Resolved",
                            subtitle = "The issue has been successfully resolved.",
                            timestamp = null,
                            icon = Icons.Default.Check,
                            iconBg = Color.LightGray,
                            isCompleted = false,
                            isPending = true
                        )
                        TrackTimelineItem(
                            title = "Closed",
                            subtitle = "Report is closed. Thank you for helping improve our city!",
                            timestamp = null,
                            icon = Icons.Default.Close,
                            iconBg = Color.LightGray,
                            isCompleted = false,
                            isPending = true,
                            isLast = true
                        )
                    }
                }
            }

            // Need Help Support Callout
            item {
                Surface(
                    color = Color(0xFFEBF8FF),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFFBEE3F8))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Need Help?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2B6CB0))
                        Text(
                            "If you have questions about your report, contact our support team.",
                            fontSize = 14.sp,
                            color = Color(0xFF2C5282),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onContactSupport,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D4E89)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Contact Support", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun TrackTimelineItem(
    title: String,
    subtitle: String,
    timestamp: String?,
    icon: ImageVector,
    iconBg: Color,
    isCompleted: Boolean = false,
    isActive: Boolean = false,
    isPending: Boolean = false,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(if (isCompleted) Color(0xFF137333) else Color(0xFFE2E8F0))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPending) Color.LightGray else Color(0xFF1A202C)
                )
                if (isActive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = Color(0xFFFFF7ED), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "Current",
                            color = Color(0xFFEA580C),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                subtitle,
                fontSize = 13.sp,
                color = if (isPending) Color.LightGray else Color(0xFF718096),
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (timestamp != null) {
                Text(
                    timestamp,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun TrackReportPreview() {
    CitySyncTheme {
        TrackReportScreen()
    }
}
