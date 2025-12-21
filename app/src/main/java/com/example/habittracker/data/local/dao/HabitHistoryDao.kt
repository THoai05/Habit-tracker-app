package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.HabitDayStatus
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

    @Query("SELECT date FROM habit_history WHERE habitId = :habitId AND isCompleted = 1 ORDER BY date DESC")
    suspend fun getCompletedDates(habitId: Int): List<String>

    @Query("""
        SELECT h.*, 
               CASE WHEN hh.isCompleted IS NOT NULL THEN hh.isCompleted ELSE 0 END as isCompleted,
               hh.date as historyDate
        FROM habits h
        LEFT JOIN habit_history hh ON h.id = hh.habitId AND hh.date = :targetDate
        WHERE h.isArchived = 0
    """)
    suspend fun getHabitsWithStatusByDate(targetDate: String): List<HabitDayStatus>

    // Hàm phụ để đếm streak cho thông báo "bạn đã làm x ngày liên tiếp"
    // Đếm ngược từ ngày hôm qua trở về trước
    @Query("""
        SELECT date FROM habit_history 
        WHERE habitId = :habitId AND isCompleted = 1 AND date < :currentDate 
        ORDER BY date DESC
    """)
    suspend fun getPastCompletedDates(habitId: Int, currentDate: String): List<String>

    @Query("SELECT * FROM habit_history WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getHistoryInRange(startDate: String, endDate: String): List<HabitHistory>
}

