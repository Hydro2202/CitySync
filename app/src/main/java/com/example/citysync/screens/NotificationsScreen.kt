package com.example.citysync.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*

enum class NotificationType {
    REPORT_UPDATE,
    GOVERNMENT_ANNOUNCEMENT,
    NEW_COMMENT,
    COMMUNITY_ALERT
}

data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val description: String,
    val timestamp: String,
    val isRead: Boolean
)

@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val NotifBlueLocal = Color(0xFF0D4E89)
    val NotifBgLocal = Color(0xFFF4F6F9)
    val NotifTextDarkLocal = Color(0xFF1A202C)
    val NotifTextMutedLocal = Color(0xFF5A6B7C)
    val NotifTextTimestampLocal = Color(0xFFA0AEC0)
    val NotifFilterInactiveLocal = Color(0xFFF1F3F5)
    val NotifUnreadDotLocal = Color(0xFF007AFF)

    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem(1, NotificationType.REPORT_UPDATE, "Report Update", "Your report #1024 has been marked as resolved by the city engineer.", "2 hours ago", false),
                NotificationItem(2, NotificationType.GOVERNMENT_ANNOUNCEMENT, "Government Announcement", "Scheduled road maintenance on Main St. starting tomorrow 8 AM.", "5 hours ago", false),
                NotificationItem(3, NotificationType.NEW_COMMENT, "New Comment", "John Doe commented on your report about the broken streetlight.", "1 day ago", true),
                NotificationItem(4, NotificationType.COMMUNITY_ALERT, "Community Alert", "Heavy rainfall expected in your area. Please stay safe and alert.", "2 days ago", false)
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Updates", "Announcements", "Alerts")

    val filteredNotifications = when (selectedFilter) {
        "Updates" -> notifications.filter { it.type == NotificationType.REPORT_UPDATE || it.type == NotificationType.NEW_COMMENT }
        "Announcements" -> notifications.filter { it.type == NotificationType.GOVERNMENT_ANNOUNCEMENT }
        "Alerts" -> notifications.filter { it.type == NotificationType.COMMUNITY_ALERT }
        else -> notifications
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        containerColor = NotifBgLocal,
        topBar = {
            Column {
                NotificationHeader(NotifBlueLocal)
                NotificationSubHeader(
                    unreadCount = unreadCount,
                    onMarkAllRead = {
                        notifications = notifications.map { it.copy(isRead = true) }
                    },
                    NotifTextDarkLocal,
                    NotifTextMutedLocal,
                    NotifBlueLocal
                )
                FilterPillsRow(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    NotifBlueLocal,
                    NotifFilterInactiveLocal,
                    NotifTextMutedLocal
                )
            }
        },
        bottomBar = {
            StandardBottomNavBar(
                selectedTab = NavTab.ALERTS,
                onNavigateToHome = onBack,
                onNavigateToReports = onNavigateToReports,
                onNavigateToCommunity = onNavigateToCommunity,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredNotifications, key = { it.id }) { item ->
                var isVisible by remember { mutableStateOf(true) }
                
                AnimatedVisibility(
                    visible = isVisible,
                    exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                ) {
                    NotificationCard(
                        item = item,
                        onMarkAsRead = {
                            notifications = notifications.map {
                                if (it.id == item.id) it.copy(isRead = true) else it
                            }
                        },
                        onDelete = {
                            isVisible = false
                        },
                        NotifTextDarkLocal,
                        NotifUnreadDotLocal,
                        NotifTextMutedLocal,
                        NotifTextTimestampLocal,
                        NotifBlueLocal
                    )
                }

                LaunchedEffect(isVisible) {
                    if (!isVisible) {
                        kotlinx.coroutines.delay(300)
                        notifications = notifications.filter { it.id != item.id }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHeader(bgColor: Color) {
    Surface(
        color = bgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Notifications",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* Settings */ }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationSubHeader(
    unreadCount: Int,
    onMarkAllRead: () -> Unit,
    textDark: Color,
    textMuted: Color,
    blueColor: Color
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "All Notifications",
                    color = textDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$unreadCount unread",
                    color = textMuted,
                    fontSize = 14.sp
                )
            }
            Text(
                "Mark all read",
                color = blueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onMarkAllRead() }
            )
        }
    }
}

@Composable
fun FilterPillsRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    activeBg: Color,
    inactiveBg: Color,
    inactiveText: Color
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    color = if (isSelected) activeBg else inactiveBg,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.clickable { onFilterSelected(filter) }
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else inactiveText,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    textDark: Color,
    unreadDot: Color,
    textMuted: Color,
    textTimestamp: Color,
    blueColor: Color
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            NotificationIconBadge(type = item.type)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.title,
                        color = textDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!item.isRead) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(unreadDot)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.description,
                    color = textMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.timestamp,
                    color = textTimestamp,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(IntrinsicSize.Min).fillMaxHeight()
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = textTimestamp,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                if (!item.isRead) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Mark as read",
                        color = blueColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onMarkAsRead() }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationIconBadge(type: NotificationType) {
    val NotifPurpleBgLocal = Color(0xFFF3E8FF)
    val NotifPurpleIconLocal = Color(0xFF7E22CE)
    val NotifBlueLocal = Color(0xFF0D4E89)

    val (bgColor, iconColor, icon) = when (type) {
        NotificationType.REPORT_UPDATE -> Triple(GreenTint, GreenDark, Icons.Default.CheckCircle)
        NotificationType.GOVERNMENT_ANNOUNCEMENT -> Triple(BlueTint, NotifBlueLocal, Icons.Default.Notifications)
        NotificationType.NEW_COMMENT -> Triple(NotifPurpleBgLocal, NotifPurpleIconLocal, Icons.Default.ChatBubble)
        NotificationType.COMMUNITY_ALERT -> Triple(OrangeTint, OrangeDark, Icons.Default.Warning)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

// StandardBottomNavBar used instead of NotificationsBottomNavBar


@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    CitySyncTheme {
        NotificationsScreen()
    }
}
