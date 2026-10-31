package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // "SHAYARI", "AUDIO", "REEL"
    val content: String,
    val language: String, // "Hindi" or "Hinglish"
    val category: String, // "Heartbreak", "Deep Romantic", "One-sided Love", "4-Line"
    val tone: String = "Sad",
    val voiceName: String? = null,
    val musicTrack: String? = null,
    val backgroundId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
