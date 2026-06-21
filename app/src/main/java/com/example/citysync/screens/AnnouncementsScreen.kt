package com.example.citysync.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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

@Composable
fun AnnouncementsScreen(
    initialCategory: String? = null,
    onBack: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory ?: "All") }

    val categories = listOf("All", "Infrastructure", "Utilities", "Events", "Policy", "Health")

    val allAnnouncements = remember {
        listOf(
            AnnouncementData(
                id = 1,
                title = "Road Maintenance Schedule",
                description = "Major repairs on Main St. will cause temporary closures from June 15-17. Please plan alternative routes.",
                date = "June 9, 2026",
                category = "Infrastructure",
                isPinned = true,
                iconType = AnnouncementIconType.WARNING
            ),
            AnnouncementData(
                id = 2,
                title = "Water Service Interruption Notice",
                description = "Scheduled maintenance at District 4 water pumps. Service will be suspended from 8 AM to 5 PM tomorrow.",
                date = "June 10, 2026",
                category = "Utilities",
                isPinned = true,
                iconType = AnnouncementIconType.WARNING
            ),
            AnnouncementData(
                id = 3,
                title = "Community Health Fair 2026",
                description = "Join us at the City Park for free check-ups, vaccinations, and wellness workshops for all residents.",
                date = "June 12, 2026",
                category = "Health",
                isPinned = false,
                iconType = AnnouncementIconType.MEGAPHONE
            ),
            AnnouncementData(
                id = 4,
                title = "New City Ordinance: Waste Segregation",
                description = "The municipal council has approved Ordinance No. 45-B requiring strict waste segregation at source.",
                date = "June 8, 2026",
                category = "Policy",
                isPinned = false,
                iconType = AnnouncementIconType.INFO
            ),
            AnnouncementData(
                id = 5,
                title = "Upcoming Town Hall Meeting",
                description = "Discuss the new urban development plan with city officials this Friday at 6 PM in the Municipal Hall.",
                date = "June 7, 2026",
                category = "Events",
                isPinned = false,
                iconType = AnnouncementIconType.MEGAPHONE
            )
        )
    }

    val filteredAnnouncements = allAnnouncements.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        containerColor = NotifBg,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                // Top Header Blue Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NotifBlue)
                        .statusBarsPadding()
                        .padding(vertical = 20.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "Announcements",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Search Input Bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(16.dp)
                )

                // Filter Pills Navigation Row
                FilterPillsRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val pinned = filteredAnnouncements.filter { it.isPinned }
            val regular = filteredAnnouncements.filter { !it.isPinned }

            if (pinned.isNotEmpty()) {
                items(pinned) { announcement ->
                    AnnouncementCard(announcement = announcement)
                }
            }

            if (regular.isNotEmpty()) {
                items(regular) { announcement ->
                    AnnouncementCard(announcement = announcement)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NotifFilterInactive,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = NotifTextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(
                        "Search announcements...",
                        color = NotifTextMuted,
                        fontSize = 14.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = NotifTextDark),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun FilterPillsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                color = if (isSelected) NotifBlue else NotifFilterInactive,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable { onCategorySelected(category) }
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else NotifTextMuted,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: AnnouncementData) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .animateContentSize() // Smooth transition for expansion
    ) {
        // Pinned Header
        if (announcement.isPinned) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NotifBlue)
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "PINNED",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Block
            val (iconBg, iconTint, icon) = when (announcement.iconType) {
                AnnouncementIconType.WARNING -> Triple(Color(0xFFFFEBEE), CrimsonRed, Icons.Default.Warning)
                AnnouncementIconType.MEGAPHONE -> Triple(BlueTint, NotifBlue, Icons.Default.Campaign)
                AnnouncementIconType.INFO -> Triple(OrangeTint, OrangeDark, Icons.Default.Info)
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Metadata & Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NotifTextDark
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = NotifTextTimestamp,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = announcement.date,
                        fontSize = 12.sp,
                        color = NotifTextTimestamp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = NotifFilterInactive,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = announcement.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = NotifTextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                // Media Placeholder (Full width graphics crop)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = announcement.description,
                    fontSize = 14.sp,
                    color = NotifTextMuted,
                    lineHeight = 20.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2 // Truncate when collapsed
                )

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Detailed Information Framework
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NotifFilterInactive, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Official Update Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NotifBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "This is a detailed advisory from the city council. Residents in the affected sectors are advised to follow safety protocols and stay updated via official radio channels. For emergencies, contact the municipal hotline immediately.",
                            fontSize = 13.sp,
                            color = NotifTextDark,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = NotifBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Affected: District 1, 2, and Central Business Area", fontSize = 11.sp, color = NotifTextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (isExpanded) "Show Less \u2191" else "Read More \u2192",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NotifBlue,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp).clickable { isExpanded = !isExpanded }
            )
        }
    }
}

// StandardBottomNavBar used instead of AnnouncementsBottomNavBar

data class AnnouncementData(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val category: String,
    val isPinned: Boolean,
    val iconType: AnnouncementIconType
)

enum class AnnouncementIconType {
    WARNING, MEGAPHONE, INFO
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun AnnouncementsScreenPreview() {
    CitySyncTheme {
        AnnouncementsScreen()
    }
}
