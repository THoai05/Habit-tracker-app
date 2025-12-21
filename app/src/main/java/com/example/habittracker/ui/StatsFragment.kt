package com.example.habittracker.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.R
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.local.DatabaseProvider
// import com.example.habittracker.data.model.Habit // <-- Xóa cái này hoặc không dùng tới
import com.example.habittracker.data.model.HabitHistory // <-- QUAN TRỌNG: Import cái này
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatsFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var tabLayout: TabLayout
    private lateinit var tvSummary: TextView
    private lateinit var tvAdvice: TextView

    private lateinit var db: AppDatabase
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        barChart = view.findViewById(R.id.barChart)
        tabLayout = view.findViewById(R.id.tabLayoutStats)
        tvSummary = view.findViewById(R.id.tvSummary)
        tvAdvice = view.findViewById(R.id.tvAdvice)

        db = DatabaseProvider.getDatabase(requireContext())

        setupChartBasic()
        loadWeeklyData()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadWeeklyData()
                    1 -> loadMonthlyData()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        return view
    }

    private fun setupChartBasic() {
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.axisRight.isEnabled = false
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.axisMinimum = 0f
    }

    // --- SỬA LOGIC TUẦN ---
    private fun loadWeeklyData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val endDateStr = sdf.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -6)
            val startDateStr = sdf.format(calendar.time)

            // SỬA: Gọi habitHistoryDao() thay vì habitDao()
            val historyList = db.habitHistoryDao().getHistoryInRange(startDateStr, endDateStr)

            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()
            var totalCompleted = 0

            // Lưu ý: Logic tính Total Task ở đây chỉ đếm những cái ĐÃ ĐƯỢC GHI NHẬN trong history
            // (Tức là user đã tick hoặc hệ thống đã tạo record ngày hôm đó)
            var totalRecorded = 0

            val tempCal = Calendar.getInstance()
            tempCal.add(Calendar.DAY_OF_YEAR, -6)

            for (i in 0..6) {
                val dateStr = sdf.format(tempCal.time)
                val dayLabel = SimpleDateFormat("E", Locale("vi", "VN")).format(tempCal.time)

                // Lọc trong HabitHistory
                val historyToday = historyList.filter { it.date == dateStr }

                // Đếm số task đã hoàn thành (isCompleted = true)
                val completedCount = historyToday.count { it.isCompleted }

                entries.add(BarEntry(i.toFloat(), completedCount.toFloat()))
                labels.add(dayLabel)

                totalCompleted += completedCount
                totalRecorded += historyToday.size

                tempCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            withContext(Dispatchers.Main) {
                updateChart(entries, labels, Color.parseColor("#4CAF50"))
                updateFeedback(totalCompleted, totalRecorded, "tuần này")
            }
        }
    }

    // --- SỬA LOGIC THÁNG ---
    private fun loadMonthlyData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startDateStr = sdf.format(calendar.time)

            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, maxDay)
            val endDateStr = sdf.format(calendar.time)

            // SỬA: Gọi habitHistoryDao()
            val historyList = db.habitHistoryDao().getHistoryInRange(startDateStr, endDateStr)

            val entries = ArrayList<BarEntry>()
            val labels = arrayListOf("Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4+")
            var totalCompleted = 0
            var totalRecorded = 0

            val ranges = listOf(1..7, 8..14, 15..21, 22..31)

            for ((index, range) in ranges.withIndex()) {
                val historyInWeek = historyList.filter {
                    val day = try {
                        it.date.substring(8, 10).toInt()
                    } catch (e: Exception) { 0 }
                    day in range
                }

                val completedCount = historyInWeek.count { it.isCompleted }

                entries.add(BarEntry(index.toFloat(), completedCount.toFloat()))

                totalCompleted += completedCount
                totalRecorded += historyInWeek.size
            }

            withContext(Dispatchers.Main) {
                updateChart(entries, labels, Color.parseColor("#FF9800"))
                updateFeedback(totalCompleted, totalRecorded, "tháng này")
            }
        }
    }

    private fun updateChart(entries: ArrayList<BarEntry>, labels: List<String>, color: Int) {
        val dataSet = BarDataSet(entries, "Thống kê")
        dataSet.color = color
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK

        val data = BarData(dataSet)
        data.barWidth = 0.6f

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.data = data
        barChart.notifyDataSetChanged()
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun updateFeedback(completed: Int, total: Int, timeRange: String) {
        // Nếu total = 0 (chưa có dữ liệu history), tránh chia cho 0
        tvSummary.text = "Đã hoàn thành: $completed task ($timeRange)"

        val advice = if (total == 0) {
            "Chưa có dữ liệu ghi nhận."
        } else {
            val ratio = completed.toFloat() / total
            when {
                ratio >= 0.8 -> "Xuất sắc! Tiếp tục phát huy."
                ratio >= 0.5 -> "Khá tốt! Cố gắng hơn nữa nhé."
                else -> "Hãy cố gắng hoàn thành nhiều hơn."
            }
        }
        tvAdvice.text = advice
    }
}