package com.example.habittracker.ui.habit.list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitHistory
import com.example.habittracker.ui.habit.edit.EditHabitActivity
import kotlinx.coroutines.launch

class HabitListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitAdapter
    private val habitList = mutableListOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_list)

        recyclerView = findViewById(R.id.recyclerViewHabits)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = HabitAdapter(
            habitList,
            onItemClick = { habit ->
                showHabitDetailDialog(habit)
            },
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                deleteHabit(habit)
            },

        )

        recyclerView.adapter = adapter

        // nút Add Habit
        findViewById<TextView>(R.id.btnAddHabit).setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java)
            startActivity(intent)
        }

        loadHabits()
    }

    private fun loadHabits() {
        val db = DatabaseProvider.getDatabase(this)
        val habitDao = db.habitDao()
        val habitHistoryDao = db.habitHistoryDao()

        lifecycleScope.launch {
            val habitsFromDb = habitDao.getHabitsByUser(userId = 1)

            habitList.clear()
            habitList.addAll(habitsFromDb)
            adapter.notifyDataSetChanged()
        }
    }


    private fun deleteHabit(habit: Habit) {
        val db = DatabaseProvider.getDatabase(this)
        val habitDao = db.habitDao()
        lifecycleScope.launch {
            habitDao.deleteHabit(habit)
            adapter.removeItem(habit)
            Toast.makeText(this@HabitListActivity, "Habit deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadHabits() // reload mỗi khi quay lại
    }
    private fun toggleHabitCompletion(habit: Habit) {
        val db = DatabaseProvider.getDatabase(this)
        val habitHistoryDao = db.habitHistoryDao()

        lifecycleScope.launch {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(System.currentTimeMillis())

            val existing = habitHistoryDao.getByHabitAndDate(habit.id, today)

            if (existing == null) {
                // Chưa tick hôm nay → tick
                val history = HabitHistory(
                    habitId = habit.id,
                    date = today,
                    isCompleted = true
                )
                habitHistoryDao.insert(history)

                Toast.makeText(this@HabitListActivity, "${habit.name} completed!", Toast.LENGTH_SHORT).show()
            } else {
                // Đã tick → un-tick
                habitHistoryDao.delete(existing)
                Toast.makeText(this@HabitListActivity, "${habit.name} uncompleted!", Toast.LENGTH_SHORT).show()
            }

            adapter.updateItem(habit)
        }
    }



    private fun showHabitDetailDialog(habit: Habit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_habit_detail)

        val tvName = dialog.findViewById<TextView>(R.id.tvHabitDetailName)
        val tvUpNext = dialog.findViewById<TextView>(R.id.tvHabitDetailUpNext)
        val tvRepeat = dialog.findViewById<TextView>(R.id.tvHabitDetailRepeat)
        val tvTime = dialog.findViewById<TextView>(R.id.tvHabitDetailTime)
        val tvReminder = dialog.findViewById<TextView>(R.id.tvHabitDetailReminder)
        val tvTag = dialog.findViewById<TextView>(R.id.tvHabitDetailTag)
        val tvGoal = dialog.findViewById<TextView>(R.id.tvHabitDetailGoal)

        val btnEdit = dialog.findViewById<TextView>(R.id.btnDetailEdit)
        val btnDelete = dialog.findViewById<TextView>(R.id.btnDetailDelete)

        // gán dữ liệu từ Habit
        // gán dữ liệu từ Habit
        tvName.text = habit.name
        tvUpNext.text = "Up Next: ${habit.upNext}"
        tvRepeat.text = "Repeat: ${habit.repeat}"
        val timeText = if (habit.specifiedTime != null) {
            val hour = habit.specifiedTime / 60
            val minute = habit.specifiedTime % 60
            "Time: %02d:%02d".format(hour, minute)
        } else {
            "Time: ${habit.timeMode}"
        }
        tvTime.text = timeText
        val reminderText = when (habit.reminderMode) {
            "None" -> "Reminder: None"
            "Custom" -> {
                habit.reminderTime?.let { t ->
                    val hour = t / 60
                    val minute = t % 60
                    "Reminder: %02d:%02d".format(hour, minute)
                } ?: "Reminder: Custom"
            }
            else -> "Reminder: ${habit.reminderMode}"
        }
        tvReminder.text = reminderText
        tvTag.text = "Tag: ${habit.tag}"
        tvGoal.text = "Goal: ${habit.targetValue}"


        btnEdit.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, EditHabitActivity::class.java)
            intent.putExtra("habitId", habit.id)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            dialog.dismiss()
            deleteHabit(habit)
        }

        dialog.show()
    }
}
