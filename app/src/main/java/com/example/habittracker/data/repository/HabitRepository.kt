package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.dao.HabitHistoryDao
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.habittracker.utils.DateUtils

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitHistoryDao: HabitHistoryDao
) {

    // Lấy danh sách thói quen của user
    suspend fun getHabits(userId: Int): List<Habit> = withContext(Dispatchers.IO) {
        habitDao.getHabitsByUser(userId)
    }

    // Tạo thói quen mới
    suspend fun addHabit(habit: Habit): Long = withContext(Dispatchers.IO) {
        habitDao.insertHabit(habit)
    }

    // Cập nhật thói quen
    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    // Xóa thói quen
    suspend fun deleteHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }

    suspend fun toggleHabitToday(habitId: Int) {
        val today = DateUtils.today()

        val history = habitHistoryDao.getByHabitAndDate(habitId, today)

        if (history == null) {
            habitHistoryDao.insert(
                HabitHistory(
                    habitId = habitId,
                    date = today,
                    isCompleted = true
                )
            )
        } else {
            habitHistoryDao.delete(history)
        }
    }

    suspend fun isCompletedToday(habitId: Int): Boolean {
        return habitHistoryDao
            .getByHabitAndDate(habitId, DateUtils.today()) != null
    }
}
