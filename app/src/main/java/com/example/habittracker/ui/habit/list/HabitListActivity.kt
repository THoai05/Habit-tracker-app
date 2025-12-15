package com.example.habittracker.ui.habit.list

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.adapter.DateAdapter
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import com.example.habittracker.ui.habit.viewmodel.HabitViewModel
import com.example.habittracker.ui.habit.viewmodel.HabitViewModelFactory
import com.example.habittracker.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitListActivity : AppCompatActivity() {

    private lateinit var viewModel: HabitViewModel
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var dateAdapter: DateAdapter

    // Biến lưu ngày đang được chọn (Mặc định là Hôm nay)
    private var currentSelectedDate: String = DateUtils.getCurrentDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_list)

        setupViewModel()
        setupDateRecyclerView() // Setup lịch ngang (Ngày tháng)
        setupHabitRecyclerView() // Setup danh sách thói quen
        setupObservers() // Lắng nghe dữ liệu thay đổi
        setupEvents() // Các nút bấm khác

        // Load dữ liệu lần đầu cho ngày hôm nay
        viewModel.loadHabits(userId = 1, date = currentSelectedDate)
    }

    override fun onResume() {
        super.onResume()
        // Khi quay lại từ màn hình Edit/Add, reload lại list theo ngày đang chọn
        viewModel.loadHabits(userId = 1, date = currentSelectedDate)
    }

    // --- 1. Cấu hình ViewModel ---
    private fun setupViewModel() {
        // Lấy các DAO từ DatabaseProvider
        val db = DatabaseProvider.getDatabase(this)
        val repository = HabitRepository(
            db.habitDao(),
            db.habitHistoryDao(),
            db.streakCacheDao() // Nhớ cái StreakCacheDao lúc nãy nhé
        )
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]
    }

    // --- 2. Cấu hình Lịch Ngang (Date Selector) ---
    private fun setupDateRecyclerView() {
        val rvDate = findViewById<RecyclerView>(R.id.rvDateSelector) // Bro cần thêm ID này vào XML

        // Tạo danh sách 7 ngày (3 ngày trước + hôm nay + 3 ngày sau)
        val dates = generateDateList()

        dateAdapter = DateAdapter(dates) { selectedDate ->
            // KHI USER BẤM CHỌN NGÀY
            if (currentSelectedDate != selectedDate) {
                currentSelectedDate = selectedDate
                // Reload list habit theo ngày mới
                viewModel.loadHabits(userId = 1, date = currentSelectedDate)
                Toast.makeText(this, "Đang xem ngày: $selectedDate", Toast.LENGTH_SHORT).show()
            }
        }

        rvDate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvDate.adapter = dateAdapter

        // Scroll tới ngày hôm nay (ở giữa)
        rvDate.scrollToPosition(dates.size / 2)
    }

    // --- 3. Cấu hình Danh sách Thói quen ---
    private fun setupHabitRecyclerView() {
        val rvHabits = findViewById<RecyclerView>(R.id.recyclerViewHabits)

        habitAdapter = HabitAdapter(
            habits = mutableListOf(),
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                viewModel.deleteHabit(habit) {
                    Toast.makeText(this, "Đã xóa thói quen", Toast.LENGTH_SHORT).show()
                }
            },
            onCheckClick = { habit ->
                // QUAN TRỌNG: Truyền ngày đang chọn vào để ViewModel xử lý
                viewModel.toggleHabit(habit, currentSelectedDate)
            }
        )

        rvHabits.layoutManager = LinearLayoutManager(this)
        rvHabits.adapter = habitAdapter
    }

    // --- 4. Lắng nghe dữ liệu từ ViewModel ---
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.habits.collectLatest { list ->
                // Khi list thay đổi (do load lại hoặc do toggle check), cập nhật Adapter
                habitAdapter.updateList(list)
            }
        }
    }

    private fun setupEvents() {
        findViewById<TextView>(R.id.btnAddHabit).setOnClickListener {
            startActivity(Intent(this, EditHabitActivity::class.java))
        }
    }

    // Hàm tiện ích tạo list ngày để hiển thị lên lịch
    private fun generateDateList(): List<String> {
        val list = mutableListOf<String>()
        val today = LocalDate.now()
        // Lấy 3 ngày trước và 3 ngày sau (Tổng 7 ngày)
        for (i in -3..3) {
            list.add(today.plusDays(i.toLong()).toString())
        }
        return list
    }
}