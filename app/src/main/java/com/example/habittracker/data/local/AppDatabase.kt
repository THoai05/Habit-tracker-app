package com.example.habittracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habittracker.data.local.dao.*
import com.example.habittracker.data.model.*
import android.content.Context
import androidx.room.Room
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Habit::class,
        HabitHistory::class,
        HabitNote::class,
        HabitGoal::class,
        HabitReminder::class,
        UserSettings::class,
        StreakCache::class
    ],
    version = 2, // tăng version từ 1 -> 2
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
                .fallbackToDestructiveMigration() // xóa DB cũ và tạo DB mới
                .build()
            INSTANCE = instance
            instance
        }
    }
}
