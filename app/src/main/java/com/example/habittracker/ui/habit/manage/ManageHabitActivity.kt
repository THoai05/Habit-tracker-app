package com.example.habittracker.ui.habit.manage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import com.example.habittracker.ui.habit.list.HabitAdapter
import androidx.lifecycle.ViewModelProvider
import android.widget.ImageButton

class ManageHabitActivity : AppCompatActivity() {

    private lateinit var viewModel: ManageHabitViewModel
    private lateinit var adapter: HabitAdapter
    private val habitList = mutableListOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_habit)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // Quay lại activity trước
        }
        setupViewModel()
        setupRecyclerView()
        observeData()
    }

    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
        val repository = HabitRepository(
            db.habitDao(),
            db.habitHistoryDao(),
            db.streakCacheDao()
        )

        viewModel = ViewModelProvider(
            this,
            ManageHabitViewModelFactory(repository)
        )[ManageHabitViewModel::class.java]

        // Load dữ liệu
        viewModel.loadHabits()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            habits = habitList,
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                showDeleteConfirm(habit)
            },
            onCheckClick = { habit ->
                viewModel.toggleHabit(habit)
            }
        )

        findViewById<RecyclerView>(R.id.rvManageHabit).apply {
            layoutManager = LinearLayoutManager(this@ManageHabitActivity)
            adapter = this@ManageHabitActivity.adapter
        }
    }

    private fun observeData() {
        viewModel.habits.observe(this) { habits ->
            adapter.updateList(habits)
        }
    }

    private fun showDeleteConfirm(habit: Habit) {
        AlertDialog.Builder(this)
            .setTitle("Delete habit?")
            .setMessage("Bạn có chắc muốn xóa \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteHabit(habit)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
