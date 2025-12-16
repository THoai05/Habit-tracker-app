package com.example.habittracker.ui.habit.edit

import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.databinding.ActivityHabitEditBinding
import com.example.habittracker.ui.habit.viewmodel.EditHabitViewModel

class EditHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHabitEditBinding
    private lateinit var viewModel: EditHabitViewModel

    // Factory để khởi tạo ViewModel có tham số Repository
    class ViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditHabitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EditHabitViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        setupObservers()

        // Load data nếu là Edit mode
        val habitId = intent.getIntExtra("habitId", -1)
        viewModel.loadHabit(habitId)
    }

    private fun setupViewModel() {
        val db = DatabaseProvider.getDatabase(this)
// Repository yêu cầu 2 tham số: HabitDao và HabitHistoryDao
        val repository = HabitRepository(db.habitDao(), db.habitHistoryDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EditHabitViewModel::class.java]
    }

    private fun setupUI() {
        // Setup text & icon cho các field tĩnh
        setupFieldStaticInfo()

        // Click Listeners
        binding.tvSaveHabit.setOnClickListener {
            val name = binding.etHabitName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter habit name", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveHabit(name)
            }
        }

        binding.fieldUpNext.root.setOnClickListener { showUpNextDialog() }
        binding.fieldRepeat.root.setOnClickListener { showRepeatDialog() }
        binding.fieldTime.root.setOnClickListener { showTimeDialog() }
        binding.fieldReminder.root.setOnClickListener { showReminderDialog() }
        binding.fieldTag.root.setOnClickListener { showTagDialog() }
        binding.fieldGoal.root.setOnClickListener { showGoalDialog() }

        setupColorSelector()
    }

    private fun setupFieldStaticInfo() {
        // Sử dụng ViewBinding để set icon và text tĩnh
        // Giả sử item_habit_field.xml có các id: ivFieldIcon, tvFieldName, tvFieldValue
        binding.fieldUpNext.apply { ivFieldIcon.setImageResource(R.drawable.ic_calendar); tvFieldName.text = "Up Next" }
        binding.fieldRepeat.apply { ivFieldIcon.setImageResource(R.drawable.ic_repeat); tvFieldName.text = "Repeat" }
        binding.fieldTime.apply { ivFieldIcon.setImageResource(R.drawable.ic_clock); tvFieldName.text = "Time" }
        binding.fieldReminder.apply { ivFieldIcon.setImageResource(R.drawable.ic_bell); tvFieldName.text = "Reminder" }
        binding.fieldTag.apply { ivFieldIcon.setImageResource(R.drawable.ic_tag); tvFieldName.text = "Tag" }
        binding.fieldGoal.apply { ivFieldIcon.setImageResource(R.drawable.ic_goal); tvFieldName.text = "Goal" }
    }

    private fun setupObservers() {
        // Quan sát dữ liệu thay đổi từ ViewModel để update UI
        viewModel.habitName.observe(this) { binding.etHabitName.setText(it) }

        viewModel.selectedColor.observe(this) { color ->
            binding.rootLayout.setBackgroundColor(color)
            updateColorSelectionUI(color)
        }

        viewModel.upNext.observe(this) {
            binding.fieldUpNext.tvFieldValue.text = it?.let { "$it days" } ?: "Select"
        }
        viewModel.repeat.observe(this) {
            binding.fieldRepeat.tvFieldValue.text = it
        }
        viewModel.tag.observe(this) {
            binding.fieldTag.tvFieldValue.text = it
        }
        viewModel.targetValue.observe(this) {
            val unit = viewModel.targetUnit.value ?: "unit"
            binding.fieldGoal.tvFieldValue.text = if (it != null) "$it $unit" else "Select"
        }

        viewModel.timeMode.observe(this) { mode ->
            if (mode == "AnyTime") binding.fieldTime.tvFieldValue.text = "AnyTime"
            else {
                val t = viewModel.specifiedTime.value ?: 0
                binding.fieldTime.tvFieldValue.text = String.format("%02d:%02d", t/60, t%60)
            }
        }

        viewModel.reminderMode.observe(this) { mode ->
            if (mode == "None") binding.fieldReminder.tvFieldValue.text = "None"
            else {
                val t = viewModel.reminderTime.value ?: 0
                binding.fieldReminder.tvFieldValue.text = String.format("%02d:%02d", t/60, t%60)
            }
        }

        viewModel.saveSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    // --- Các hàm Dialog logic (Giữ lại ở Activity vì cần Context) ---
    private fun showUpNextDialog() {
        val items = arrayOf("1", "3", "7", "14", "30")
        AlertDialog.Builder(this).setTitle("Up Next").setItems(items) { _, which ->
            viewModel.updateUpNext(items[which].toInt())
        }.show()
    }

    private fun showRepeatDialog() {
        val items = arrayOf("Daily", "Weekly", "Custom")
        AlertDialog.Builder(this).setTitle("Repeat").setItems(items) { _, which ->
            viewModel.updateRepeat(items[which])
        }.show()
    }

    private fun showTagDialog() {
        val tags = arrayOf("No tag", "Morning Routine", "Workout", "Health", "Study")
        AlertDialog.Builder(this).setTitle("Tag").setItems(tags) { _, which ->
            viewModel.updateTag(tags[which])
        }.show()
    }

    private fun showGoalDialog() {
        val input = EditText(this)
        input.hint = "Target value (e.g. 10)"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(this).setTitle("Goal").setView(input).setPositiveButton("OK") { _, _ ->
            val t = input.text.toString().toIntOrNull()
            if (t != null) viewModel.updateGoal(t, "times")
        }.show()
    }

    private fun showTimeDialog() {
        val items = arrayOf("AnyTime", "SpecifiedTime")
        AlertDialog.Builder(this).setTitle("Time").setItems(items) { _, which ->
            if (items[which] == "AnyTime") viewModel.updateTime("AnyTime", null)
            else {
                val cal = java.util.Calendar.getInstance()
                TimePickerDialog(this, { _, h, m ->
                    viewModel.updateTime("SpecifiedTime", h * 60 + m)
                }, cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE), true).show()
            }
        }.show()
    }

    private fun showReminderDialog() {
        val items = arrayOf("None", "Custom")
        AlertDialog.Builder(this).setTitle("Reminder").setItems(items) { _, which ->
            if (items[which] == "None") viewModel.updateReminder("None", null)
            else {
                val cal = java.util.Calendar.getInstance()
                TimePickerDialog(this, { _, h, m ->
                    viewModel.updateReminder("Custom", h * 60 + m)
                }, cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE), true).show()
            }
        }.show()
    }

    private fun setupColorSelector() {
        val colors = listOf("#FFD6E0", "#FFF3B0", "#C9E4DE", "#D8E2DC", "#B8E0D2")
        val views = listOf(binding.color1, binding.color2, binding.color3, binding.color4, binding.color5)

        views.forEachIndexed { index, view ->
            val drawable = ContextCompat.getDrawable(this, R.drawable.bg_color_circle)!!.mutate() as GradientDrawable
            drawable.setColor(Color.parseColor(colors[index]))
            view.background = drawable
            view.setOnClickListener { viewModel.updateColor(Color.parseColor(colors[index])) }
        }
    }

    private fun updateColorSelectionUI(selectedColor: Int) {
        val views = listOf(binding.color1, binding.color2, binding.color3, binding.color4, binding.color5)
        views.forEach { v ->
            val bg = v.background
            if (bg is GradientDrawable) bg.setStroke(0, Color.TRANSPARENT)
        }

    }
}