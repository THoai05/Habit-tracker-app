package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.ui.StatsFragment
import com.example.habittracker.ui.adapter.DateAdapter
import com.example.habittracker.ui.auth.LoginActivity
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import com.example.habittracker.ui.habit.list.HabitAdapter
import com.example.habittracker.ui.settings.SettingsActivity
import com.example.habittracker.ui.statistics.StatisticsActivity
import com.example.habittracker.ui.viewmodel.MainViewModel
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var viewModel: MainViewModel

    // --- ViewModel Factory ---
    class MainViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()       // Setup RecyclerViews
        setupDrawer()   // Setup Navigation Menu (Mới thêm)
        setupObservers()
        setupListeners() // Setup FAB button
    }

    override fun onResume() {
        super.onResume()
        // Load lại dữ liệu ngày hôm nay khi quay lại app
        // (Hoặc ngày đang được chọn nếu lưu state)
        viewModel.loadHabitsForDate(LocalDate.now().toString())
    }

    // 1. Khởi tạo ViewModel
    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
        val repository = HabitRepository(db.habitDao(), db.habitHistoryDao(), db.streakCacheDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    // 2. Setup Giao diện (RecyclerViews)
    private fun setupUI() {
        // A. Setup Date RecyclerView (Danh sách ngày)
        val days = (-15..15).map { LocalDate.now().plusDays(it.toLong()).toString() }

        val dateAdapter = DateAdapter(days) { date ->
            viewModel.setCurrentSelectedDate(date) // Cập nhật ngày đang chọn trong VM
            viewModel.loadHabitsForDate(date)      // Load data ngày đó
        }

        binding.rvDateList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
            scrollToPosition(15) // Scroll tới ngày hôm nay
        }

        // B. Setup Habit RecyclerView (Danh sách thói quen)
        habitAdapter = HabitAdapter(
            habits = mutableListOf(),
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                viewModel.deleteHabit(habit)
                Toast.makeText(this, "Đã xóa thói quen", Toast.LENGTH_SHORT).show()
            },
            onCheckClick = { habit ->
                viewModel.completeHabit(habit)
                habitAdapter.notifyDataSetChanged() // Refresh UI ngay lập tức
            }
        )

        binding.rvHabitList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = habitAdapter
        }
    }

    // 3. Setup Navigation Drawer (Xử lý menu bên trái)
    private fun setupDrawer() {
        // Nút mở menu (3 gạch)
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Xử lý khi chọn item trong menu
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Đã ở trang chủ, chỉ cần đóng drawer
                }
                R.id.nav_stats -> {
                    // Chuyển sang trang Thống kê
                    startActivity(Intent(this, StatisticsActivity::class.java))
                }
                R.id.nav_stats_week_month -> {
                    // 1. Ẩn giao diện trang chủ
                    binding.layoutHome.visibility = android.view.View.GONE
                    // 2. Hiện khung chứa Fragment
                    binding.fragmentContainer.visibility = android.view.View.VISIBLE

                    // 3. Nhét cái StatsFragment (cái có Tab Tuần/Tháng) vào
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, StatsFragment())
                        .commit()
                }
                R.id.nav_settings -> {
                    // Chuyển sang trang Cài đặt
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_logout -> {
                    // Xử lý đăng xuất
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // nav_history: Có thể làm sau hoặc link tới CalendarActivity
            }
            // Đóng menu sau khi chọn
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // 4. Lắng nghe dữ liệu
    private fun setupObservers() {
        viewModel.displayHabits.observe(this) { habits ->
            habitAdapter.updateList(habits)
        }
    }

    // 5. Các sự kiện click khác (FAB)
    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, EditHabitActivity::class.java))
        }
    }

    // Xử lý nút Back của điện thoại: Nếu Drawer đang mở thì đóng nó trước
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}