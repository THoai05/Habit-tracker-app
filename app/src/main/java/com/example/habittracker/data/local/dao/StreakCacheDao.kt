package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.StreakCache

@Dao
interface StreakCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakCache)

    @Update
    suspend fun updateStreak(streak: StreakCache)

    @Query("SELECT * FROM streak_cache WHERE habitId = :habitId LIMIT 1")
    suspend fun getStreakByHabit(habitId: Int): StreakCache?

    @Query("""
        SELECT date FROM habit_history
        WHERE habitId = :habitId AND isCompleted = 1
        ORDER BY date DESC
    """)
    suspend fun getCompletedDates(habitId: Int): List<String>
}
