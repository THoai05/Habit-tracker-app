package com.example.habittracker.ui.habit.list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Locale

class HabitListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HabitAdapter
    private val habitList = mutableListOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_list)

        recyclerView = findViewById(R.id.recyclerViewHabits)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // --- KHỞI TẠO ADAPTER ĐẦY ĐỦ 4 HÀM ---
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
            // --- THÊM HÀM NÀY ĐỂ FIX LỖI ---
            onCheckClick = { habit ->
                completeHabit(habit)
            }
        )

        recyclerView.adapter = adapter

        // Nút Add Habit
        findViewById<TextView>(R.id.btnAddHabit).setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadHabits() // Reload mỗi khi quay lại
    }

    private fun loadHabits() {
        val db = DatabaseProvider.getDatabase(this)
        val habitDao = db.habitDao()
        val habitHistoryDao = db.habitHistoryDao()

        lifecycleScope.launch {
            // 1. Lấy danh sách Habit
            val habitsFromDb = habitDao.getHabitsByUser(userId = 1)

            // 2. Lấy ngày hôm nay
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(System.currentTimeMillis())

            // 3. Duyệt qua từng habit để kiểm tra xem hôm nay đã hoàn thành chưa
            // Bước này quan trọng để hiển thị đúng nút xanh/xám khi mở lại app
            habitsFromDb.forEach { habit ->
                val history = habitHistoryDao.getByHabitAndDate(habit.id, today)
                // Nếu tìm thấy lịch sử -> Đánh dấu là đã xong
                habit.isCompletedToday = (history != null && history.isCompleted)
            }

            // 4. Cập nhật UI
            habitList.clear()
            habitList.addAll(habitsFromDb)
            adapter.notifyDataSetChanged()
        }
    }

    // Hàm xử lý khi bấm nút Check (Chỉ hoàn thành, không hoàn tác)
    private fun completeHabit(habit: Habit) {
        val db = DatabaseProvider.getDatabase(this)
        val habitHistoryDao = db.habitHistoryDao()

        lifecycleScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(System.currentTimeMillis())

            // Kiểm tra xem đã có trong DB chưa (để tránh lỗi duplicate nếu bấm nhanh)
            val existing = habitHistoryDao.getByHabitAndDate(habit.id, today)

            if (existing == null) {
                val history = HabitHistory(
                    habitId = habit.id,
                    date = today,
                    isCompleted = true
                )
                habitHistoryDao.insert(history)

                Toast.makeText(this@HabitListActivity, "Đã hoàn thành ${habit.name}!", Toast.LENGTH_SHORT).show()

                // Cập nhật trạng thái item trong list để Adapter hiển thị đúng (không cần load lại từ DB)
                habit.isCompletedToday = true
                adapter.updateItem(habit)
            }
        }
    }

    private fun deleteHabit(habit: Habit) {
        val db = DatabaseProvider.getDatabase(this)
        val habitDao = db.habitDao()
        lifecycleScope.launch {
            habitDao.deleteHabit(habit)
            adapter.removeItem(habit)
            Toast.makeText(this@HabitListActivity, "Đã xóa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHabitDetailDialog(habit: Habit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_habit_detail)

        // Binding các view trong dialog
        val tvName = dialog.findViewById<TextView>(R.id.tvHabitDetailName)
        val tvUpNext = dialog.findViewById<TextView>(R.id.tvHabitDetailUpNext)
        val tvRepeat = dialog.findViewById<TextView>(R.id.tvHabitDetailRepeat)
        val tvTime = dialog.findViewById<TextView>(R.id.tvHabitDetailTime)
        val tvReminder = dialog.findViewById<TextView>(R.id.tvHabitDetailReminder)
        val tvTag = dialog.findViewById<TextView>(R.id.tvHabitDetailTag)
        val tvGoal = dialog.findViewById<TextView>(R.id.tvHabitDetailGoal)

        val btnEdit = dialog.findViewById<TextView>(R.id.btnDetailEdit)
        val btnDelete = dialog.findViewById<TextView>(R.id.btnDetailDelete)

        // Gán dữ liệu
        tvName.text = habit.name
        tvUpNext.text = "Up Next: ${habit.upNext ?: "None"}"
        tvRepeat.text = "Repeat: ${habit.repeat}"

        val timeText = if (habit.timeMode == "SpecifiedTime" && habit.specifiedTime != null) {
            val hour = habit.specifiedTime / 60
            val minute = habit.specifiedTime % 60
            "Time: %02d:%02d".format(hour, minute)
        } else {
            "Time: AnyTime"
        }
        tvTime.text = timeText

        val reminderText = if (habit.reminderMode == "Custom" && habit.reminderTime != null) {
            val hour = habit.reminderTime / 60
            val minute = habit.reminderTime % 60
            "Reminder: %02d:%02d".format(hour, minute)
        } else {
            "Reminder: None"
        }
        tvReminder.text = reminderText

        tvTag.text = "Tag: ${habit.tag}"
        tvGoal.text = if (habit.targetValue != null) "Goal: ${habit.targetValue} ${habit.targetUnit ?: ""}" else "Goal: None"

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