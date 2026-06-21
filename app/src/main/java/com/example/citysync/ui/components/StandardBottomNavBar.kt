package com.example.citysync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citysync.ui.theme.DeepNavy
import com.example.citysync.ui.theme.DesignTokens
import com.example.citysync.ui.theme.TextMuted

enum class NavTab {
    HOME, REPORTS, COMMUNITY, ALERTS, PROFILE
}

@Composable
fun StandardBottomNavBar(
    selectedTab: NavTab,
    onNavigateToHome: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val items = listOf(
        NavItemData("Home", Icons.Outlined.Home, NavTab.HOME, onNavigateToHome),
        NavItemData("Reports", Icons.Outlined.Description, NavTab.REPORTS, onNavigateToReports),
        NavItemData("Community", Icons.Outlined.Groups, NavTab.COMMUNITY, onNavigateToCommunity),
        NavItemData("Alerts", Icons.Outlined.Notifications, NavTab.ALERTS, onNavigateToNotifications),
        NavItemData("Profile", Icons.Outlined.AccountCircle, NavTab.PROFILE, onNavigateToProfile)
    )

    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1f) // Ensures exactly 20% width for each of the 5 items
                        .clickable { item.onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isSelected = item.tab == selectedTab
                        
                        // Indicator
                        Box(
                            modifier = Modifier
                                .width(DesignTokens.BottomNavIndicatorWidth)
                                .height(DesignTokens.BottomNavIndicatorHeight)
                                .background(
                                    if (isSelected) DeepNavy else Color.Transparent,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) DeepNavy else TextMuted,
                            modifier = Modifier.size(DesignTokens.BottomNavIconSize)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            item.label,
                            fontSize = DesignTokens.BottomNavLabelSize,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) DeepNavy else TextMuted,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private data class NavItemData(
    val label: String,
    val icon: ImageVector,
    val tab: NavTab,
    val onClick: () -> Unit
)
