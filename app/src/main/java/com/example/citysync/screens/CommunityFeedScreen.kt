package com.example.citysync.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.R
import com.example.citysync.ui.components.NavTab
import com.example.citysync.ui.components.StandardBottomNavBar
import com.example.citysync.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommunityFeedScreen(
    onBack: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToReportDetails: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var openMenuId by remember { mutableStateOf<Int?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val posts = remember {
        mutableStateListOf(
            PostData(
                id = 1,
                author = "Maria Santos",
                authorInitials = "MS",
                authorBg = Color(0xFFE3F2FD),
                authorText = Color(0xFF0D4E89),
                timestamp = "2 hours ago",
                location = "Main St. Intersection",
                title = "Broken Traffic Light at Intersection",
                description = "The traffic light at the main intersection has been malfunctioning since yesterday. Very dangerous!",
                category = "Traffic",
                status = "Under Review",
                statusColor = Color(0xFFEF6C00),
                statusBg = Color(0xFFFFF3E0),
                likes = 24,
                commentsCount = 8,
                isHot = true,
                isTrending = true,
                imageRes = R.drawable.brokentraffic
            ),
            PostData(
                id = 2,
                author = "Ricardo Cruz",
                authorInitials = "RC",
                authorBg = Color(0xFFE8F5E9),
                authorText = Color(0xFF2E7D32),
                timestamp = "5 hours ago",
                location = "Greenwood Park",
                title = "Overflowing Garbage Bins",
                description = "Several garbage bins in the park area are overflowing. Needs immediate collection to maintain cleanliness.",
                category = "Waste Management",
                status = "Assigned",
                statusColor = Color(0xFF8E24AA),
                statusBg = Color(0xFFF3E5F5),
                likes = 15,
                commentsCount = 3,
                isNearby = true,
                imageRes = R.drawable.garbage
            )
        )
    }

    val filteredPosts = remember(selectedFilter, posts.toList()) {
        when (selectedFilter) {
            "Trending" -> posts.filter { it.isTrending || it.likes > 20 }
            "Nearby" -> posts.filter { it.isNearby }
            else -> posts
        }
    }

    fun handleShare(post: PostData) {
        toastMessage = "Link copied to clipboard!"
        scope.launch {
            delay(2000)
            toastMessage = null
        }
    }

    fun showToast(message: String) {
        toastMessage = message
        scope.launch {
            delay(2000)
            toastMessage = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFFF4F6F9),
            topBar = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepNavy)
                            .statusBarsPadding()
                            .padding(vertical = 24.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Community Feed",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 16.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterPill(
                                label = "All Reports",
                                isActive = selectedFilter == "All",
                                onClick = { selectedFilter = "All" }
                            )
                        }
                        item {
                            FilterPill(
                                label = "Trending",
                                isActive = selectedFilter == "Trending",
                                icon = Icons.Default.TrendingUp,
                                onClick = { selectedFilter = "Trending" }
                            )
                        }
                        item {
                            FilterPill(
                                label = "Nearby",
                                isActive = selectedFilter == "Nearby",
                                onClick = { selectedFilter = "Nearby" }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                StandardBottomNavBar(
                    selectedTab = NavTab.COMMUNITY,
                    onNavigateToHome = onBack,
                    onNavigateToReports = onNavigateToReports,
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
                items(filteredPosts, key = { it.id }) { post ->
                        CommunityPostCard(
                            post = post,
                            isMenuOpen = openMenuId == post.id,
                            onMenuToggle = { openMenuId = if (openMenuId == post.id) null else post.id },
                            onMenuDismiss = { openMenuId = null },
                            onClick = { onNavigateToReportDetails("REP-2026-00123${post.id}") },
                            onCommentIconClick = { onCommentClick("REP-2026-00123${post.id}") },
                            onShare = { handleShare(post) },
                            onSave = { showToast("Post saved successfully!") },
                            onReport = { showToast("Post reported for review.") },
                            onContactSupport = onContactSupport,
                            onLikeClick = {
                            val index = posts.indexOfFirst { it.id == post.id }
                            if (index != -1) {
                                val currentPost = posts[index]
                                val newIsLiked = !currentPost.isLiked
                                posts[index] = currentPost.copy(
                                    isLiked = newIsLiked,
                                    likes = if (newIsLiked) currentPost.likes + 1 else currentPost.likes - 1
                                )
                            }
                        }
                    )
                }
            }
        }

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
fun CommunityPostCard(
    post: PostData,
    isMenuOpen: Boolean,
    onMenuToggle: () -> Unit,
    onMenuDismiss: () -> Unit,
    onClick: () -> Unit = {},
    onCommentIconClick: () -> Unit = {},
    onShare: () -> Unit = {},
    onSave: () -> Unit = {},
    onReport: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onLikeClick: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(post.authorBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        post.authorInitials,
                        color = post.authorText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        post.author,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1A202C)
                    )
                    Text(
                        "${post.timestamp} · ${post.location}",
                        fontSize = 12.sp,
                        color = Color(0xFF7A8B9C)
                    )
                }
                
                if (post.isHot) {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFFEF6C00),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("HOT", color = Color(0xFFEF6C00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Box {
                    IconButton(
                        onClick = onMenuToggle,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF7A8B9C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = isMenuOpen,
                        onDismissRequest = onMenuDismiss,
                        modifier = Modifier
                            .background(Color.White)
                            .width(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share Post", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Share, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                            onClick = { onShare(); onMenuDismiss() }
                        )
                        DropdownMenuItem(
                            text = { Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.BookmarkBorder, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                            onClick = { onSave(); onMenuDismiss() }
                        )
                        DropdownMenuItem(
                            text = { Text("Report Post", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Flag, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                            onClick = { onReport(); onMenuDismiss() }
                        )
                        DropdownMenuItem(
                            text = { Text("Contact Support", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.HeadsetMic, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                            onClick = { onContactSupport(); onMenuDismiss() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A202C)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                post.description,
                fontSize = 14.sp,
                color = Color(0xFF5A6B7C),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color(0xFFF1F3F5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        post.category,
                        color = Color(0xFF5A6B7C),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = post.statusBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        post.status,
                        color = post.statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (post.imageRes != null) {
                Image(
                    painter = painterResource(id = post.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F3F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Image,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color.Red else Color(0xFF7A8B9C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(post.likes.toString(), color = Color(0xFF7A8B9C), fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    IconButton(
                        onClick = onCommentIconClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color(0xFF7A8B9C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(post.commentsCount.toString(), color = Color(0xFF7A8B9C), fontSize = 14.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onShare() }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF7A8B9C),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = Color(0xFF7A8B9C), fontSize = 14.sp)
                }
            }
        }
    }
}

data class PostData(
    val id: Int,
    val author: String,
    val authorInitials: String,
    val authorBg: Color,
    val authorText: Color,
    val timestamp: String,
    val location: String,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val likes: Int,
    val commentsCount: Int,
    val isHot: Boolean = false,
    val isLiked: Boolean = false,
    val isTrending: Boolean = false,
    val isNearby: Boolean = false,
    val imageRes: Int? = null
)

@Composable
fun FilterPill(label: String, isActive: Boolean, icon: ImageVector? = null, onClick: () -> Unit = {}) {
    Surface(
        color = if (isActive) DeepNavy else Color(0xFFF1F3F5),
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isActive) Color.White else Color(0xFF5A6B7C),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                color = if (isActive) Color.White else Color(0xFF5A6B7C),
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun CommunityFeedScreenPreview() {
    CitySyncTheme {
        CommunityFeedScreen()
    }
}
