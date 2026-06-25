package com.example.citysync.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Report(
    @SerialName("id")
    val id: String? = null,
    @SerialName("user_id")
    val reportedBy: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("tags")
    val tags: String, // Combined category and priority for now
    @SerialName("status")
    val status: String = "Assigned",
    @SerialName("location")
    val location: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("reference")
    val reference: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)
