package com.example.citysync.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconColor: Color
)