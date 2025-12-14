package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.dao.HabitHistoryDao
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import com.example.habittracker.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitHistoryDao: HabitHistoryDao // Có thể null nếu chưa dùng tới trong EditHabitActivity
) {

    // Lấy danh sách thói quen của user
    suspend fun getHabits(userId: Int): List<Habit> = withContext(Dispatchers.IO) {
        habitDao.getHabitsByUser(userId)
    }

    // --- MỚI THÊM: Lấy chi tiết 1 habit để Edit ---
    suspend fun getHabitById(id: Int): Habit? = withContext(Dispatchers.IO) {
        habitDao.getHabitById(id)
    }
    // ----------------------------------------------

    // Tạo thói quen mới
    suspend fun insertHabit(habit: Habit): Long = withContext(Dispatchers.IO) {
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

    // Toggle check/uncheck (Dùng cho màn hình Home sau này)
    suspend fun toggleHabitToday(habitId: Int) = withContext(Dispatchers.IO) {
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

    suspend fun isCompletedToday(habitId: Int): Boolean = withContext(Dispatchers.IO) {
        habitHistoryDao.getByHabitAndDate(habitId, DateUtils.today()) != null
    }

    suspend fun getHistory(habitId: Int, date: String): HabitHistory? = withContext(Dispatchers.IO) {
        habitHistoryDao.getByHabitAndDate(habitId, date)
    }

    // Đánh dấu hoàn thành
    suspend fun addHistory(history: HabitHistory) = withContext(Dispatchers.IO) {
        habitHistoryDao.insert(history)
    }
}