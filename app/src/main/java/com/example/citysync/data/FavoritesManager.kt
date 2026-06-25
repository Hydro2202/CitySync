package com.example.citysync.data

import androidx.compose.runtime.mutableStateListOf
import com.example.citysync.ui.screens.reports.ReportData
import com.example.citysync.ui.screens.reports.TagData
import androidx.compose.ui.graphics.Color
import com.example.citysync.R

object FavoritesManager {
    val favoriteReportIds = mutableStateListOf<String>()

    // Mock data for reports that can be favorited
    val allMockReports = listOf(
        ReportData(
            id = "REP-2026-001234",
            title = "Broken Streetlight on Main Street",
            location = "Main St, Brgy. 1",
            status = "In Progress",
            statusBg = Color(0xFFFFEFE2),
            statusText = Color(0xFFB76E00),
            tags = listOf(
                TagData("Lighting", Color(0xFFF1F3F5), Color(0xFF718096)),
                TagData("High Priority", Color(0xFFFCE8E6), Color(0xFFC5221F))
            ),
            date = "June 4, 2026",
            timestamp = 1717459200000L,
            imageRes = R.drawable.brokenlight
        ),
        ReportData(
            id = "REP-2026-001239",
            title = "Damaged Sidewalk",
            location = "Oak Street, Brgy. 4",
            status = "Closed",
            statusBg = Color(0xFFF1F3F5),
            statusText = Color(0xFF5A6B7C),
            tags = listOf(
                TagData("Infrastructure", Color(0xFFF1F3F5), Color(0xFF718096)),
                TagData("Low Priority", Color(0xFFEBF8FF), Color(0xFF2B6CB0))
            ),
            date = "May 28, 2026",
            timestamp = 1716854400000L,
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
                TagData("High Priority", Color(0xFFFCE8E6), Color(0xFFC5221F))
            ),
            date = "May 30, 2026",
            timestamp = 1717027200000L,
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
                TagData("Medium Priority", Color(0xFFFFF3CD), Color(0xFF856404))
            ),
            date = "June 2, 2026",
            timestamp = 1717286400000L,
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
                TagData("High Priority", Color(0xFFFCE8E6), Color(0xFFC5221F))
            ),
            date = "June 3, 2026",
            timestamp = 1717372800000L,
            imageRes = R.drawable.street
        )
    )

    fun isFavorite(id: String): Boolean {
        return favoriteReportIds.contains(id)
    }

    fun toggleFavorite(id: String) {
        if (favoriteReportIds.contains(id)) {
            favoriteReportIds.remove(id)
        } else {
            favoriteReportIds.add(id)
        }
    }
    
    fun getFavoriteReports(): List<ReportData> {
        return allMockReports.filter { favoriteReportIds.contains(it.id) }
    }
}
