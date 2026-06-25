package com.example.citysync.data

object ReportPriority {
    const val HIGH = "High Priority"
    const val MEDIUM = "Medium Priority"
    const val LOW = "Low Priority"

    fun forCategory(category: String): String = when (category) {
        "Public Safety", "Traffic", "Water & Drainage" -> HIGH
        "Roads & Infrastructure", "Lighting" -> MEDIUM
        else -> LOW
    }
}
