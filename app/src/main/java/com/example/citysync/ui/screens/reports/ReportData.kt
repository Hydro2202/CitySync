package com.example.citysync.ui.screens.reports

import androidx.compose.ui.graphics.Color

data class TagData(
    val name: String,
    val bgColor: Color,
    val textColor: Color
)

data class ReportData(
    val id: String,
    val title: String,
    val location: String,
    val status: String,
    val statusBg: Color,
    val statusText: Color,
    val tags: List<TagData>,
    val date: String,
    val timestamp: Long,
    val imageRes: Int
)
