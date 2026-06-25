package com.example.citysync.ui.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.data.FavoritesManager
import com.example.citysync.ui.screens.reports.ReportData
import com.example.citysync.ui.screens.reports.TagData
import com.example.citysync.ui.theme.CitySyncTheme
import com.example.citysync.R

@Composable
fun FavoritesListScreen(
    onBack: () -> Unit,
    onNavigateToReportDetails: (String) -> Unit,
) {
    val favoriteReports = FavoritesManager.getFavoriteReports()
    val brandBlue = Color(0xFF0D4E89)
    val appBackground = Color(0xFFF4F6F9)

    Scaffold(
        containerColor = appBackground,
        topBar = {
            Surface(color = brandBlue, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
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
                    Text(
                        "Saved Favorites",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        if (favoriteReports.isEmpty()) {
            EmptyFavoritesView(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteReports) { report ->
                    ReportCard(report, onClick = { onNavigateToReportDetails(report.id) })
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: ReportData, onClick: () -> Unit = {}) {
    val subtitleColor = Color(0xFF718096)
    val titleColor = Color(0xFF1A202C)
    val dateTextColor = Color(0xFFA0AEC0)
    val borderSlate100 = Color(0xFFF1F5F9)

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderSlate100),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Using a placeholder if no image resource is provided
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (report.imageRes != 0) {
                    Image(
                        painter = painterResource(id = report.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
                        color = titleColor
                    )
                    Text(
                        text = report.location,
                        fontSize = 13.sp,
                        color = subtitleColor,
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
                            color = tag.bgColor,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = tag.name,
                                color = tag.textColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = report.date,
                    color = dateTextColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun EmptyFavoritesView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color(0xFFD1D9E0),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No favorited reports yet.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A202C),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the heart icon on any report detail view to save it here.",
            fontSize = 14.sp,
            color = Color(0xFF5A6B7C),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}
