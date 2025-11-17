package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.dao.HabitHistoryDao
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    // Thêm lịch sử hoàn thành trong ngày
    suspend fun addHabitHistory(history: HabitHistory) = withContext(Dispatchers.IO) {
        habitHistoryDao.insertHistory(history)
    }

    // Lấy lịch sử hoàn thành của 1 thói quen
    suspend fun getHabitHistory(habitId: Int): List<HabitHistory> = withContext(Dispatchers.IO) {
        habitHistoryDao.getHistoryByHabit(habitId)
    }
}
