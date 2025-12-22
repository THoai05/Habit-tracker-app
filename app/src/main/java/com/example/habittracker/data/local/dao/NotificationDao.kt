package com.example.habittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.habittracker.data.model.NotificationEntity

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE habitId = :habitId AND type = :type AND timestamp > :since LIMIT 1")
    suspend fun getRecentNotification(habitId: Int, type: String, since: Long): NotificationEntity?

    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}