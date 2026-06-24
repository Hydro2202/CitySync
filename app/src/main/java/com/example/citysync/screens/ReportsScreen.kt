package com.example.citysync.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.citysync.R
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.CitySyncTheme

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ReportsScreenPreview() {
    CitySyncTheme {
        ReportsScreen()
    }
}

// Precise Colors from Prompt
private val BrandBlue = Color(0xFF0D4E89)
private val AppBackground = Color(0xFFF4F6F9)
private val InputBackground = Color(0xFFF1F3F5)
private val PlaceholderColor = Color(0xFF8E9AA8)
private val InactivePillText = Color(0xFF5A6B7C)
private val MetaTextColor = Color(0xFF7A8B9C)
private val CardBorderColor = Color(0xFFF1F5F9)
private val TitleColor = Color(0xFF1A202C)
private val SubtitleColor = Color(0xFF718096)
private val DateTextColor = Color(0xFFA0AEC0)
private val BorderSlate100 = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit = {},
    onNavigateToReportWizard: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToReportDetails: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var sortLatest by remember { mutableStateOf(true) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allReports = remember {
        listOf(
            ReportData(
                id = "REP-2026-001239",
                title = "Damaged Sidewalk",
                location = "Oak Street, Brgy. 4",
                status = "Closed",
                statusBg = Color(0xFFF1F3F5),
                statusText = Color(0xFF5A6B7C),
                tags = listOf(
                    TagData("Infrastructure", Color(0xFFF1F3F5), Color(0xFF718096)),
                    TagData("Low", Color(0xFFEBF8FF), Color(0xFF2B6CB0))
                ),
                date = "May 28, 2026",
                timestamp = 1716854400000L, // May 28
                imageRes = R.drawable.street
            ),
            ReportData(
                id = "REP-2026-001236",
                title = "Broken Traffic Light",
                location = "Intersection Ave, Brgy. 2",
                status = "Resolved",
                statusBg = Color(0xFFE6F4EA),
                statusText = Color(0xFF137333),
                tags = listOf(
                    TagData("Traffic", Color(0xFFF1F3F5), Color(0xFF718096)),
                    TagData("High", Color(0xFFFCE8E6), Color(0xFFC5221F))
                ),
                date = "May 30, 2026",
                timestamp = 1717027200000L, // May 30
                imageRes = R.drawable.brokentraffic
            ),
            ReportData(
                id = "REP-2026-001237",
                title = "Overflowing Garbage Bin",
                location = "Greenwood Park, Brgy. 7",
                status = "Assigned",
                statusBg = Color(0xFFF3E8FF),
                statusText = Color(0xFF6B21A8),
                tags = listOf(
                    TagData("Waste", Color(0xFFF1F3F5), Color(0xFF718096)),
                    TagData("Medium", Color(0xFFFFF3CD), Color(0xFF856404))
                ),
                date = "June 2, 2026",
                timestamp = 1717286400000L, // June 2
                imageRes = R.drawable.garbage
            ),
            ReportData(
                id = "REP-2026-001238",
                title = "Pothole on National Highway",
                location = "Zone 4, National Highway",
                status = "Under Review",
                statusBg = Color(0xFFFFEFE2),
                statusText = Color(0xFFB76E00),
                tags = listOf(
                    TagData("Roads", Color(0xFFF1F3F5), Color(0xFF718096)),
                    TagData("High", Color(0xFFFCE8E6), Color(0xFFC5221F))
                ),
                date = "June 3, 2026",
                timestamp = 1717372800000L, // June 3
                imageRes = R.drawable.street
            ),
            ReportData(
                id = "REP-2026-001234",
                title = "Broken Streetlight on Main Street",
                location = "Main St, Brgy. 1",
                status = "In Progress",
                statusBg = Color(0xFFFFEFE2),
                statusText = Color(0xFFB76E00),
                tags = listOf(
                    TagData("Lighting", Color(0xFFF1F3F5), Color(0xFF718096)),
                    TagData("High", Color(0xFFFCE8E6), Color(0xFFC5221F))
                ),
                date = "June 4, 2026",
                timestamp = 1717459200000L, // June 4
                imageRes = R.drawable.brokenlight
            )
        )
    }

    val filteredReports = remember(selectedFilter, sortLatest, searchQuery) {
        val filtered = allReports.filter { report ->
            val matchesFilter = if (selectedFilter == "All") {
                true
            } else {
                report.status.equals(selectedFilter, ignoreCase = true)
            }
            
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                report.title.contains(searchQuery, ignoreCase = true) || 
                report.location.contains(searchQuery, ignoreCase = true)
            }
            
            matchesFilter && matchesSearch
        }
        
        if (sortLatest) {
            filtered.sortedByDescending { it.timestamp }
        } else {
            filtered.sortedBy { it.timestamp }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = AppBackground,
            topBar = {
                Column {
                    // Blue Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandBlue)
                            .statusBarsPadding()
                            .padding(vertical = 24.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "My Reports",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Search Bar Section
                    Surface(
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search reports...", color = PlaceholderColor, fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PlaceholderColor, modifier = Modifier.size(20.dp)) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear", tint = PlaceholderColor, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = InputBackground,
                                    unfocusedContainerColor = InputBackground,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Filter Pills
                            val filters = listOf("All", "In Progress", "Under Review", "Resolved")
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(end = 20.dp)
                            ) {
                                items(filters) { filter ->
                                    FilterPill(
                                        label = filter,
                                        isActive = selectedFilter == filter,
                                        onClick = { selectedFilter = filter }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Meta Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${filteredReports.size} reports",
                                    color = MetaTextColor,
                                    fontSize = 14.sp
                                )
                                Box {
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D9E0)),
                                        modifier = Modifier.clickable { isSortMenuExpanded = true }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Sort: ${if (sortLatest) "Latest" else "Oldest"}",
                                                fontSize = 13.sp,
                                                color = TitleColor
                                            )
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = isSortMenuExpanded,
                                        onDismissRequest = { isSortMenuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Latest First") },
                                            onClick = {
                                                sortLatest = true
                                                isSortMenuExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Oldest First") },
                                            onClick = {
                                                sortLatest = false
                                                isSortMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                StandardBottomNavBar(
                    selectedTab = NavTab.REPORTS,
                    onNavigateToHome = onBack,
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredReports) { report ->
                    ReportCard(report, onClick = onNavigateToReportDetails)
                }
            }
        }

        // FAB Positioned fixed at the bottom right (bottom-24 right-6)
        // bottom-24 is 96dp, right-6 is 24dp
        FloatingActionButton(
            onClick = { onNavigateToReportWizard() },
            containerColor = BrandBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 24.dp)
                .size(56.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Report", modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun FilterPill(label: String, isActive: Boolean, onClick: () -> Unit = {}) {
    Surface(
        color = if (isActive) BrandBlue else InputBackground,
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else InactivePillText,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun ReportCard(report: ReportData, onClick: () -> Unit = {}) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate100),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (report.imageRes != null) {
                Image(
                    painter = painterResource(id = report.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TitleColor
                    )
                    Text(
                        text = report.location,
                        fontSize = 13.sp,
                        color = SubtitleColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Surface(
                    color = report.statusBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = report.status,
                        color = report.statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    report.tags.forEach { tag ->
                        Surface(
                            color = tag.bg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = tag.label,
                                color = tag.text,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = report.date,
                    color = DateTextColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// StandardBottomNavBar used instead of ReportsBottomNavBar

data class ReportData(
    val id: String = "",
    val title: String,
    val location: String,
    val status: String,
    val statusBg: Color,
    val statusText: Color,
    val tags: List<TagData>,
    val date: String,
    val timestamp: Long = 0L,
    val imageRes: Int? = null
)

data class TagData(
    val label: String,
    val bg: Color,
    val text: Color
)

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean
)
