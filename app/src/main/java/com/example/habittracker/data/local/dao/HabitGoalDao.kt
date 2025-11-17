package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.HabitGoal

@Dao
interface HabitGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: HabitGoal): Long

    @Update
    suspend fun updateGoal(goal: HabitGoal)

    @Delete
    suspend fun deleteGoal(goal: HabitGoal)

    @Query("SELECT * FROM habit_goals WHERE habitId = :habitId LIMIT 1")
    suspend fun getGoalByHabit(habitId: Int): HabitGoal?
}
