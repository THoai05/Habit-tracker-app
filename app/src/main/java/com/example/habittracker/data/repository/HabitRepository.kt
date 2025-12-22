package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.dao.HabitHistoryDao
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import com.example.habittracker.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.habittracker.data.local.dao.StreakCacheDao
import com.example.habittracker.data.model.StreakCache
import com.example.habittracker.data.local.dao.NotificationDao
import com.example.habittracker.data.model.NotificationEntity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitHistoryDao: HabitHistoryDao,
    private val streakCacheDao: StreakCacheDao,
    private val notificationDao: NotificationDao? = null // Thêm tham số này
) {

    suspend fun getHabits(userId: Int): List<Habit> = withContext(Dispatchers.IO) {
        habitDao.getHabitsByUser(userId)
    }

    suspend fun getHabitById(id: Int): Habit? = withContext(Dispatchers.IO) {
        habitDao.getHabitById(id)
    }

    suspend fun insertHabit(habit: Habit): Long = withContext(Dispatchers.IO) {
        val id = habitDao.insertHabit(habit)
        if (id > 0) {
            notificationDao?.insert(
                NotificationEntity(
                    title = "Thêm thói quen thành công!",
                    message = "Thói quen \"${habit.name}\" đã được thêm vào danh sách.",
                    type = "ADD",
                    habitId = id.toInt()
                )
            )
        }
        id
    }

    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }

    suspend fun toggleHabit(habitId: Int, date: String) = withContext(Dispatchers.IO) {
        val history = habitHistoryDao.getByHabitAndDate(habitId, date)
        val habit = habitDao.getHabitById(habitId)

        if (history == null) {
            habitHistoryDao.insert(
                HabitHistory(
                    habitId = habitId,
                    date = date,
                    isCompleted = true
                )
            )
            // Thêm thông báo khi hoàn thành
            notificationDao?.insert(
                NotificationEntity(
                    title = "Đã hoàn thành thói quen!",
                    message = "Bạn đã hoàn thành \"${habit?.name}\" cho ngày $date.",
                    type = "COMPLETE",
                    habitId = habitId
                )
            )
        } else {
            habitHistoryDao.delete(history)
        }
    }

    suspend fun isCompletedOnDate(habitId: Int, date: String): Boolean = withContext(Dispatchers.IO) {
        habitHistoryDao.getByHabitAndDate(habitId, date) != null
    }

    suspend fun getHistory(habitId: Int, date: String): HabitHistory? = withContext(Dispatchers.IO) {
        habitHistoryDao.getByHabitAndDate(habitId, date)
    }

    suspend fun addHistory(history: HabitHistory) = withContext(Dispatchers.IO) {
        habitHistoryDao.insert(history)
    }

    suspend fun calculateAndGetStreak(habitId: Int): Int {
        val completedDatesStrings = habitHistoryDao.getCompletedDates(habitId)

        if (completedDatesStrings.isEmpty()) {
            updateStreakCache(habitId, 0)
            return 0
        }

        val sortedDates = completedDatesStrings.map { LocalDate.parse(it) }
            .sortedDescending()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val latestDate = sortedDates[0]

        if (latestDate.isBefore(yesterday)) {
            updateStreakCache(habitId, 0)
            return 0
        }

        var currentStreak = 1

        for (i in 0 until sortedDates.size - 1) {
            val dateCurrent = sortedDates[i]
            val dateNext = sortedDates[i+1]
            val diff = ChronoUnit.DAYS.between(dateNext, dateCurrent)

            if (diff == 1L) {
                currentStreak++
            } else if (diff == 0L) {
                continue
            } else {
                break
            }
        }

        updateStreakCache(habitId, currentStreak)
        return currentStreak
    }

    private suspend fun updateStreakCache(habitId: Int, streakCount: Int) {
        val oldCache = streakCacheDao.getStreakByHabit(habitId)
        val longest = if (oldCache != null && streakCount > oldCache.longestStreak) {
            streakCount
        } else {
            oldCache?.longestStreak ?: streakCount
        }

        val newCache = StreakCache(
            habitId = habitId,
            currentStreak = streakCount,
            longestStreak = longest,
            lastCheckedDate = DateUtils.getCurrentDate()
        )
        streakCacheDao.insertStreak(newCache)
    }
}