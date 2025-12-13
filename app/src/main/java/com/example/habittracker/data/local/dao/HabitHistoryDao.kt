package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.HabitHistory

@Dao
interface HabitHistoryDao {

    @Query("""
        SELECT * FROM habit_history
        WHERE habitId = :habitId AND date = :date
        LIMIT 1
    """)
    suspend fun getByHabitAndDate(
        habitId: Int,
        date: String
    ): HabitHistory?

    @Insert
    suspend fun insert(history: HabitHistory)

    @Delete
    suspend fun delete(history: HabitHistory)

    @Query("""
        SELECT COUNT(*) FROM habit_history
        WHERE habitId = :habitId AND isCompleted = 1
    """)
    suspend fun countCompletedDays(habitId: Int): Int
}

