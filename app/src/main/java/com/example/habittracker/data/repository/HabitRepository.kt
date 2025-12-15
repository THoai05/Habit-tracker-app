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
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitHistoryDao: HabitHistoryDao, // Có thể null nếu chưa dùng tới trong EditHabitActivity
    private val streakCacheDao: StreakCacheDao
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
    suspend fun toggleHabit(habitId: Int, date: String) = withContext(Dispatchers.IO) {
        // Dùng biến date được truyền vào thay vì DateUtils.today()
        val history = habitHistoryDao.getByHabitAndDate(habitId, date)

        if (history == null) {
            habitHistoryDao.insert(
                HabitHistory(
                    habitId = habitId,
                    date = date, // Lưu đúng ngày được chọn
                    isCompleted = true
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

    // Đánh dấu hoàn thành
    suspend fun addHistory(history: HabitHistory) = withContext(Dispatchers.IO) {
        habitHistoryDao.insert(history)
    }

    suspend fun calculateAndGetStreak(habitId: Int): Int {
        // 1. Lấy list ngày từ DB (dạng String "yyyy-MM-dd")
        val completedDatesStrings = habitHistoryDao.getCompletedDates(habitId)

        if (completedDatesStrings.isEmpty()) {
            updateStreakCache(habitId, 0)
            return 0
        }

        // 2. Chuyển đổi String sang LocalDate để tính toán cho chuẩn
        // và sắp xếp giảm dần (Mới nhất -> Cũ nhất)
        val sortedDates = completedDatesStrings.map { LocalDate.parse(it) }
            .sortedDescending()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val latestDate = sortedDates[0]

        // 3. CHECK QUAN TRỌNG: Kiểm tra xem chuỗi có còn hiệu lực không?
        // Nếu ngày hoàn thành gần nhất < Hôm qua (tức là hôm kia trở về trước)
        // -> Thì chuỗi đã đứt. Reset về 0.
        // (Logic này cho phép ngày hoàn thành là Tương lai hoặc Hôm nay)
        if (latestDate.isBefore(yesterday)) {
            updateStreakCache(habitId, 0)
            return 0
        }

        // 4. Đếm chuỗi liên tiếp
        var currentStreak = 1 // Ít nhất là 1 vì list không rỗng và đã qua vòng check ở trên

        for (i in 0 until sortedDates.size - 1) {
            val dateCurrent = sortedDates[i]
            val dateNext = sortedDates[i+1]

            // Tính khoảng cách giữa 2 ngày liền kề
            val diff = ChronoUnit.DAYS.between(dateNext, dateCurrent)

            if (diff == 1L) {
                // Nếu cách nhau đúng 1 ngày -> +1 Streak
                currentStreak++
            } else if (diff == 0L) {
                // Nếu trùng ngày (lỡ user hack DB lưu 2 lần) -> Bỏ qua, không làm gì
                continue
            } else {
                // Nếu cách nhau > 1 ngày -> Đứt chuỗi -> Dừng đếm
                break
            }
        }

        // 5. Lưu và trả về
        updateStreakCache(habitId, currentStreak)
        return currentStreak
    }

    private suspend fun updateStreakCache(habitId: Int, streakCount: Int) {
        // Lấy cache cũ để so sánh longestStreak (nếu bro muốn làm tính năng kỷ lục)
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