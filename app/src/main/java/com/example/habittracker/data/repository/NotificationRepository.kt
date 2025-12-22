package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.NotificationDao
import com.example.habittracker.data.model.NotificationEntity

class NotificationRepository(private val notificationDao: NotificationDao) {
    suspend fun getAllNotifications() = notificationDao.getAllNotifications()
    
    suspend fun addNotification(notification: NotificationEntity) = notificationDao.insert(notification)

    suspend fun getRecentNotification(habitId: Int, type: String, since: Long): NotificationEntity? {
        return notificationDao.getRecentNotification(habitId, type, since)
    }
}