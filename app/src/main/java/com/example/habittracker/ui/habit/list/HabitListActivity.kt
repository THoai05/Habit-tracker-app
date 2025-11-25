package com.example.habittracker.ui.habit.list

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

        adapter = HabitAdapter(habitList,
            onEditClick = { habit ->
                val intent = Intent(this, EditHabitActivity::class.java)
                intent.putExtra("habitId", habit.id)
                startActivity(intent)
            },
            onDeleteClick = { habit ->
                deleteHabit(habit)
            }
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
}
