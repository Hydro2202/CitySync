package com.example.citysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*

@Preview(showBackground = true, widthDp = 360)
@Composable
fun DashboardScreenPreview() {
    CitySyncTheme {
        DashboardScreen()
    }
}

@Composable
fun DashboardScreen(
    onNavigateToReports: () -> Unit = {},
    onNavigateToReportWizard: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnnouncements: () -> Unit = {},
    onNavigateToAnnouncementDetail: (String) -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToReportDetails: () -> Unit = {}
) {
    Scaffold(
        containerColor = NotifBg,
        bottomBar = {
            StandardBottomNavBar(
                selectedTab = NavTab.HOME,
                onNavigateToReports = onNavigateToReports,
                onNavigateToCommunity = onNavigateToCommunity,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepNavy)
                        .statusBarsPadding()
                        .padding(
                            start = DesignTokens.DashboardHeaderPaddingH,
                            end = DesignTokens.DashboardHeaderPaddingH,
                            top = DesignTokens.DashboardHeaderPaddingTop,
                            bottom = DesignTokens.DashboardHeaderPaddingBottom
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(DesignTokens.DashboardAvatarSize)
                                    .clip(CircleShape)
                                    .background(Color(0xFFB0BEC5))
                            )
                            Spacer(modifier = Modifier.width(DesignTokens.DashboardAvatarTextGap))
                            Column {
                                Text(
                                    "Good day,",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = DesignTokens.DashboardGreetingSize,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 20.sp
                                )
                                Text(
                                    "Raiden",
                                    color = Color.White,
                                    fontSize = DesignTokens.DashboardNameSize,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                        BadgedBox(
                            modifier = Modifier.clickable { onNavigateToNotifications() },
                            badge = {
                                Badge(
                                    containerColor = CrimsonRed,
                                    modifier = Modifier
                                        .size(DesignTokens.DashboardBadgeSize)
                                        .offset(x = 2.dp, y = (-2).dp)
                                )
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(DesignTokens.DashboardNotificationIconSize)
                            )
                        }
                    }
                }
            }

            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignTokens.DashboardHeaderPaddingH)
                        .offset(y = DesignTokens.DashboardStatsOverlap),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = DesignTokens.DashboardStatsElevation),
                    shape = RoundedCornerShape(DesignTokens.DashboardStatsCorner)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = DesignTokens.DashboardStatsPaddingV),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricItem(
                            icon = Icons.Outlined.Schedule,
                            count = "3",
                            label = "Active",
                            iconBg = BlueTint,
                            iconColor = DeepNavy
                        )
                        VerticalDivider(
                            modifier = Modifier.height(DesignTokens.DashboardStatsDividerHeight),
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )
                        MetricItem(
                            icon = Icons.Outlined.CheckCircle,
                            count = "12",
                            label = "Resolved",
                            iconBg = GreenTint,
                            iconColor = GreenDark
                        )
                        VerticalDivider(
                            modifier = Modifier.height(DesignTokens.DashboardStatsDividerHeight),
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )
                        MetricItem(
                            icon = Icons.Outlined.Warning,
                            count = "2",
                            label = "Pending",
                            iconBg = OrangeTint,
                            iconColor = OrangeDark
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(
                        horizontal = DesignTokens.DashboardHeaderPaddingH,
                        vertical = 8.dp
                    )
                ) {
                    Text(
                        "Services",
                        fontWeight = FontWeight.Bold,
                        fontSize = DesignTokens.DashboardSectionTitleSize,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(DesignTokens.DashboardSectionTitleGap))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ServiceItem(Icons.Outlined.PhotoCamera, "Report", BlueTint, DeepNavy, onClick = onNavigateToReportWizard)
                        ServiceItem(Icons.Outlined.Description, "My Reports", GreenTint, GreenDark, onClick = onNavigateToReports)
                        ServiceItem(Icons.Outlined.Campaign, "Announcements", OrangeTint, OrangeDark, onClick = onNavigateToAnnouncements)
                        ServiceItem(Icons.Outlined.Groups, "Community", Color(0xFFF3E5F5), Color(0xFF8E24AA), onClick = onNavigateToCommunity)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = DesignTokens.DashboardHeaderPaddingH,
                            vertical = 12.dp
                        )
                        .clickable { onNavigateToEmergency() },
                    colors = CardDefaults.cardColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(DesignTokens.DashboardEmergencyCorner)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignTokens.DashboardCardPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(DesignTokens.DashboardEmergencyIconBox)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(DesignTokens.DashboardEmergencyIconBoxCorner)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Call,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Emergency Assistance",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                                Text(
                                    "Tap for quick access to emergency services",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            item { SectionHeader("Recent Reports", onViewAllClick = onNavigateToReports) }
            item {
                ReportListItem(
                    title = "Broken Streetlight",
                    meta = "Main St, Brgy. 5 • 2 days ago",
                    status = "In Progress",
                    badgeBg = OrangeTint,
                    badgeText = OrangeDark,
                    onClick = onNavigateToReportDetails
                )
            }
            item {
                ReportListItem(
                    title = "Pothole on Highway",
                    meta = "National Highway • 5 days ago",
                    status = "Under Review",
                    badgeBg = BlueTint,
                    badgeText = DeepNavy,
                    onClick = onNavigateToReportDetails
                )
            }

            item { SectionHeader("Announcements", onViewAllClick = onNavigateToAnnouncements) }
            item {
                AnnouncementCard(
                    title = "Road Maintenance Schedule",
                    description = "Main Street closed for repairs June 15-17.",
                    timeAgo = "1 day ago",
                    showIcon = true,
                    onClick = { onNavigateToAnnouncementDetail("Infrastructure") }
                )
            }
            item {
                AnnouncementCard(
                    title = "Water Service Interruption",
                    description = "Scheduled maintenance June 12, 8 AM – 5 PM.",
                    timeAgo = "2 days ago",
                    showIcon = false,
                    onClick = { onNavigateToAnnouncementDetail("Utilities") }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun MetricItem(
    icon: ImageVector,
    count: String,
    label: String,
    iconBg: Color,
    iconColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(DesignTokens.DashboardStatsIconCircle)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(DesignTokens.DashboardStatsIconSize)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            count,
            fontWeight = FontWeight.Bold,
            fontSize = DesignTokens.DashboardStatsCountSize,
            color = TextDark,
            lineHeight = 26.sp
        )
        Text(
            label,
            fontSize = DesignTokens.DashboardStatsLabelSize,
            color = TextMuted,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun ServiceItem(icon: ImageVector, label: String, bgTint: Color, iconColor: Color, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Card(
            modifier = Modifier.size(DesignTokens.DashboardServiceCardSize),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(DesignTokens.DashboardServiceCardCorner)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(DesignTokens.DashboardServiceIconBox)
                        .background(bgTint, RoundedCornerShape(DesignTokens.DashboardServiceIconBoxCorner)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(DesignTokens.DashboardServiceIconSize)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(DesignTokens.DashboardServiceLabelGap))
        Text(
            label,
            fontSize = DesignTokens.DashboardServiceLabelSize,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTokens.DashboardHeaderPaddingH, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = DesignTokens.DashboardSectionHeaderSize,
            color = TextDark
        )
        Text(
            "View All >",
            color = DeepNavy,
            fontSize = DesignTokens.DashboardViewAllSize,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }
}

@Composable
fun ReportListItem(
    title: String,
    meta: String,
    status: String,
    badgeBg: Color,
    badgeText: Color,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = DesignTokens.DashboardHeaderPaddingH,
                vertical = DesignTokens.DashboardCardGap
            )
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(DesignTokens.DashboardCardCorner),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.DashboardCardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = DesignTokens.DashboardReportTitleSize,
                    color = TextDark,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    meta,
                    color = TextMuted,
                    fontSize = DesignTokens.DashboardReportMetaSize,
                    lineHeight = 16.sp
                )
            }
            Surface(color = badgeBg, shape = RoundedCornerShape(50)) {
                Text(
                    text = status,
                    color = badgeText,
                    fontSize = DesignTokens.DashboardBadgeTextSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    title: String,
    description: String,
    timeAgo: String,
    showIcon: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = DesignTokens.DashboardHeaderPaddingH,
                vertical = DesignTokens.DashboardCardGap
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(DesignTokens.DashboardCardCorner),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.DashboardCardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = CrimsonRed,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = DesignTokens.DashboardReportTitleSize,
                    color = TextDark,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(timeAgo, color = TextMuted, fontSize = DesignTokens.DashboardReportMetaSize, lineHeight = 16.sp)
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// StandardBottomNavBar used instead of DashboardBottomNavBar
