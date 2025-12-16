package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.ui.adapter.DateAdapter
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import com.example.habittracker.ui.habit.list.HabitAdapter
import com.example.habittracker.ui.notifications.NotificationsActivity
import com.example.habittracker.ui.settings.SettingsActivity
import com.example.habittracker.ui.viewmodel.MainViewModel
import java.time.LocalDate
import androidx.core.view.GravityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var viewModel: MainViewModel

    // Factory để khởi tạo ViewModel có tham số Repository
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
        setupAdapters()
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Mặc định load ngày hôm nay hoặc ngày đang chọn trong ViewModel
        // Ở đây ta reset về ngày hôm nay hoặc lưu state trong VM.
        // Để đơn giản, ta load lại theo ngày hiện tại của DateAdapter đang focus (logic này cần xử lý kỹ hơn nếu muốn giữ trạng thái scroll).
        // Tạm thời load ngày hôm nay:
        viewModel.loadHabitsForDate(LocalDate.now())
    }

    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
        val repository = HabitRepository(db.habitDao(), db.habitHistoryDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupAdapters() {
        // 1. Date RecyclerView
        val days = (-15..15).map { LocalDate.now().plusDays(it.toLong()) }
        val dateAdapter = DateAdapter(days) { date ->
            // Khi chọn ngày -> Bảo ViewModel load dữ liệu ngày đó
            viewModel.loadHabitsForDate(date)
            Toast.makeText(this, "Ngày: $date", Toast.LENGTH_SHORT).show()
        }

        binding.rvDateList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateList.adapter = dateAdapter
        binding.rvDateList.scrollToPosition(15) // Scroll tới giữa (Hôm nay)

        // 2. Habit RecyclerView
        habitAdapter = HabitAdapter(
            mutableListOf(), // List rỗng ban đầu
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                viewModel.deleteHabit(habit)
                Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
            },
            onCheckClick = { habit ->
                viewModel.completeHabit(habit)
                Toast.makeText(this, "Hoàn thành!", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvHabitList.layoutManager = LinearLayoutManager(this)
        binding.rvHabitList.adapter = habitAdapter
    }

    private fun setupObservers() {
        // Lắng nghe dữ liệu từ ViewModel
        viewModel.displayHabits.observe(this) { habits ->
            // Khi list thay đổi (do lọc ngày, do check, do xóa...) -> Cập nhật Adapter
            habitAdapter.updateList(habits) // Cần thêm hàm updateList trong Adapter hoặc dùng DiffUtil
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, EditHabitActivity::class.java))
        }
        binding.btnMenu.setOnClickListener {
            //binding.drawerLayout.open()
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        //Link giao diện cài đặt và thông báo
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }
}