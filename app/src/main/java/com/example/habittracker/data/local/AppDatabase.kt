package com.example.habittracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habittracker.data.local.dao.*
import com.example.habittracker.data.model.*
import android.content.Context
import androidx.room.Room

@Database(
    entities = [
        User::class,
        Habit::class,
        HabitHistory::class,
        HabitNote::class,
        HabitGoal::class,
        HabitReminder::class,
        UserSettings::class,
        StreakCache::class,
        NotificationEntity::class
    ],
    version = 4, // Tăng lên 4 để ép Room phải reset hoàn toàn
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun habitHistoryDao(): HabitHistoryDao
    abstract fun habitNoteDao(): HabitNoteDao
    abstract fun habitGoalDao(): HabitGoalDao
    abstract fun habitReminderDao(): HabitReminderDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun streakCacheDao(): StreakCacheDao
    abstract fun notificationDao(): NotificationDao
}

object DatabaseProvider {

    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "habit_tracker_db"
            )
                .fallbackToDestructiveMigration() // Xóa hết dữ liệu cũ để tránh lỗi integrity
                .build()
            INSTANCE = instance
            instance
        }
    }
}