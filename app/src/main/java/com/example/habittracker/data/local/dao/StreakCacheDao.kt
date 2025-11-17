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
}
