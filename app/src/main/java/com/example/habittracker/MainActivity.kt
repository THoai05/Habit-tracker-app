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
import com.example.habittracker.ui.adapter.DateAdapter
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import com.example.habittracker.ui.habit.list.HabitAdapter
import com.example.habittracker.ui.notifications.NotificationsActivity
import com.example.habittracker.ui.settings.SettingsActivity
import com.example.habittracker.ui.viewmodel.MainViewModel
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var viewModel: MainViewModel

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
        viewModel.loadHabitsForDate(LocalDate.now().toString())
    }

    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
        // CẬP NHẬT: Truyền thêm db.notificationDao()
        val repository = HabitRepository(
            db.habitDao(), 
            db.habitHistoryDao(), 
            db.streakCacheDao(),
            db.notificationDao() 
        )
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupAdapters() {
        val days = (-15..15).map {
            LocalDate.now().plusDays(it.toLong()).toString()
        }

        val dateAdapter = DateAdapter(days) { date ->
            viewModel.loadHabitsForDate(date)
            viewModel.setCurrentSelectedDate(date)
        }

        binding.rvDateList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateList.adapter = dateAdapter
        binding.rvDateList.scrollToPosition(15)

        habitAdapter = HabitAdapter(
            mutableListOf(),
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                viewModel.deleteHabit(habit)
            },
            onCheckClick = { habit ->
                viewModel.completeHabit(habit)
                habitAdapter.notifyDataSetChanged()
            }
        )
        binding.rvHabitList.layoutManager = LinearLayoutManager(this)
        binding.rvHabitList.adapter = habitAdapter
    }

    private fun setupObservers() {
        viewModel.displayHabits.observe(this) { habits ->
            habitAdapter.updateList(habits)
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, EditHabitActivity::class.java))
        }
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

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