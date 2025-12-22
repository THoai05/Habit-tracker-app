package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // "ADD", "COMPLETE", "UPCOMING", "OVERDUE"
    val timestamp: Long = System.currentTimeMillis(),
    val habitId: Int? = null
)