package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.HabitHistory

@Dao
interface HabitHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HabitHistory): Long

    @Update
    suspend fun updateHistory(history: HabitHistory)

    @Delete
    suspend fun deleteHistory(history: HabitHistory)

    @Query("SELECT * FROM habit_history WHERE habitId = :habitId ORDER BY date ASC")
    suspend fun getHistoryByHabit(habitId: Int): List<HabitHistory>

    @Query("SELECT * FROM habit_history WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getHistoryByDate(habitId: Int, date: String): HabitHistory?
}
