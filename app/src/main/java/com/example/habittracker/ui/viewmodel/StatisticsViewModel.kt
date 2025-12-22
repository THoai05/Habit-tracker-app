package com.example.habittracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.local.dao.HabitHistoryDao
import com.example.habittracker.data.model.HabitDayStatus
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch

// Class ViewModel chính
class StatisticsViewModel(private val historyDao: HabitHistoryDao) : ViewModel() {

    // LiveData chứa kết quả thống kê
    private val _statsResult = MutableLiveData<StatsResult>()
    val statsResult: LiveData<StatsResult> = _statsResult
    val chartData = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val summaryText = MutableLiveData<String>()

    // Data class để gói kết quả trả về cho UI
    data class StatsResult(
        val completed: Int,
        val total: Int,
        val feedback: String
    )

    // Hàm load dữ liệu (Activity sẽ gọi hàm này)
    fun loadStatistics(date: String) {
        viewModelScope.launch {
            // Gọi hàm DAO để lấy danh sách
            // Nếu báo đỏ ở đây, hãy chắc chắn bạn đã thêm hàm getHabitsWithStatusByDate vào DAO
            val list = historyDao.getHabitsWithStatusByDate(date)

            val total = list.size
            val completed = list.count { it.isCompleted }

            // Tạo câu feedback
            val feedback = generateFeedback(list, completed, total)

            // Bắn kết quả ra UI
            _statsResult.value = StatsResult(completed, total, feedback)
        }
    }

    private fun generateFeedback(list: List<HabitDayStatus>, completed: Int, total: Int): String {
        if (total == 0) return "Chưa có thói quen nào được đặt cho ngày này."
        if (completed == total) return "Tuyệt vời! Bạn đã hoàn thành tất cả mục tiêu. 🎉"
        if (completed == 0) return "Hãy bắt đầu ngay! Bạn chưa hoàn thành task nào cả."

        val remaining = total - completed
        if (remaining == 1) {
            val missingTask = list.first { !it.isCompleted }
            return "Chỉ còn thiếu '${missingTask.habit.name}' thôi. Cố lên!"
        }

        return "Bạn đã hoàn thành $completed/$total. Còn $remaining thói quen đang chờ."
    }


}



class StatisticsViewModelFactory(private val dao: HabitHistoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}