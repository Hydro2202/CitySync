package com.example.citysync.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Report(
    @SerialName("id")
    val id: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("location")
    val location: String,
    @SerialName("status")
    val status: String,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)
