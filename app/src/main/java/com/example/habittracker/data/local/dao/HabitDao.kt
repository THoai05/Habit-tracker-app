package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getHabitsByUser(userId: Int): List<Habit>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getHabitById(habitId: Int): Habit?

    @Query("SELECT * FROM Habits")
    suspend fun getAllHabits(): List<Habit>

    @Query("SELECT * FROM habit_history WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getHabitsInDateRange(startDate: String, endDate: String): List<HabitHistory>
}
