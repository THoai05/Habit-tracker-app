package com.example.habittracker.ui.statistics

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.R
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.ui.viewmodel.StatisticsViewModel
import com.example.habittracker.ui.viewmodel.StatisticsViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatisticsActivity : AppCompatActivity() {

    private lateinit var viewModel: StatisticsViewModel

    // UI Components
    private lateinit var tvCurrentDate: TextView
    private lateinit var btnPrevDay: ImageButton
    private lateinit var btnNextDay: ImageButton
    private lateinit var progressBarDaily: ProgressBar
    private lateinit var tvRatio: TextView
    private lateinit var tvFeedback: TextView

    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        setupViewModel()
        initViews()
        setupListeners()

        // Load dữ liệu lần đầu (Hôm nay)
        loadData()
    }

    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
        val factory = StatisticsViewModelFactory(db.habitHistoryDao())
        viewModel = ViewModelProvider(this, factory)[StatisticsViewModel::class.java]

        // Lắng nghe kết quả từ ViewModel
        viewModel.statsResult.observe(this) { result ->
            updateUI(result.completed, result.total, result.feedback)
        }
    }

    private fun initViews() {
        tvCurrentDate = findViewById(R.id.tvCurrentDate)
        btnPrevDay = findViewById(R.id.btnPrevDay)
        btnNextDay = findViewById(R.id.btnNextDay)
        progressBarDaily = findViewById(R.id.progressBarDaily)
        tvRatio = findViewById(R.id.tvRatio)
        tvFeedback = findViewById(R.id.tvFeedback)

        // Setup Toolbar
        supportActionBar?.title = "Thống kê & Đánh giá"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupListeners() {
        btnPrevDay.setOnClickListener {
            selectedDate = selectedDate.minusDays(1)
            loadData()
        }

        btnNextDay.setOnClickListener {
            selectedDate = selectedDate.plusDays(1)
            loadData()
        }
    }

    private fun loadData() {
        // Cập nhật text ngày tháng
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateStr = selectedDate.format(formatter)

        if (selectedDate == LocalDate.now()) {
            tvCurrentDate.text = "$dateStr (Hôm nay)"
        } else {
            tvCurrentDate.text = dateStr
        }

        // Gửi yêu cầu lấy dữ liệu
        viewModel.loadStatistics(dateStr)
    }

    private fun updateUI(completed: Int, total: Int, feedback: String) {
        tvRatio.text = "$completed/$total"
        tvFeedback.text = feedback

        // Tính % cho Progress Bar
        val percentage = if (total > 0) (completed * 100) / total else 0
        progressBarDaily.progress = percentage

        // Đổi màu cột: Xanh nếu xong hết, Đỏ/Cam nếu chưa xong
        val colorCode = if (total > 0 && completed == total) {
            "#4CAF50" // Xanh lá
        } else {
            "#FF5722" // Cam đậm
        }
        progressBarDaily.progressTintList = ColorStateList.valueOf(Color.parseColor(colorCode))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}