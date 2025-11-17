package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.HabitReminder

@Dao
interface HabitReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: HabitReminder): Long

    @Update
    suspend fun updateReminder(reminder: HabitReminder)

    @Delete
    suspend fun deleteReminder(reminder: HabitReminder)

    @Query("SELECT * FROM habit_reminders WHERE habitId = :habitId ORDER BY time ASC")
    suspend fun getRemindersByHabit(habitId: Int): List<HabitReminder>
}
