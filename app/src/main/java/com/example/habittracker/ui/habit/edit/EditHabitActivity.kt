package com.example.habittracker.ui.habit.edit

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.*
import android.view.View
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.R
import com.example.habittracker.data.local.DatabaseProvider
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.User
import com.example.habittracker.ui.habit.list.HabitListActivity
import kotlinx.coroutines.launch

class EditHabitActivity : AppCompatActivity() {

    private lateinit var rootLayout: LinearLayout
    private lateinit var etHabitName: EditText
    private lateinit var color1: View
    private lateinit var color2: View
    private lateinit var color3: View
    private lateinit var color4: View
    private lateinit var color5: View

    private var selectedColor: Int = Color.parseColor("#FFD6E0")

    // Field TextViews
    private lateinit var fieldUpNextValue: TextView
    private lateinit var fieldRepeatValue: TextView
    private lateinit var fieldTimeValue: TextView
    private lateinit var fieldReminderValue: TextView
    private lateinit var fieldTagValue: TextView
    private lateinit var fieldGoalValue: TextView

    // Selected values
    private var selectedUpNext: Int? = null
    private var selectedRepeat = "Daily"
    private var selectedTimeMode = "AnyTime"
    private var selectedSpecifiedTime: Int? = null
    private var selectedReminderMode = "None"
    private var selectedReminderTime: Int? = null
    private var selectedTag = "No tag"
    private var selectedTargetValue: Int? = null
    private var selectedTargetUnit: String? = null

    private val TAGS = arrayOf(
        "No tag", "Morning Routine", "Workout", "Clean Room", "Healthy LifeStyle", "Sleep Better", "Relationship"
    )

    private var habitId: Int = -1 // ⬅ biến toàn cục để phân biệt create/edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_edit)

        // --- Bind views ---
        rootLayout = findViewById(R.id.rootLayout)
        etHabitName = findViewById(R.id.etHabitName)
        color1 = findViewById(R.id.color1)
        color2 = findViewById(R.id.color2)
        color3 = findViewById(R.id.color3)
        color4 = findViewById(R.id.color4)
        color5 = findViewById(R.id.color5)

        fieldUpNextValue = findViewById<LinearLayout>(R.id.fieldUpNext).findViewById(R.id.tvFieldValue)
        fieldRepeatValue = findViewById<LinearLayout>(R.id.fieldRepeat).findViewById(R.id.tvFieldValue)
        fieldTimeValue = findViewById<LinearLayout>(R.id.fieldTime).findViewById(R.id.tvFieldValue)
        fieldReminderValue = findViewById<LinearLayout>(R.id.fieldReminder).findViewById(R.id.tvFieldValue)
        fieldTagValue = findViewById<LinearLayout>(R.id.fieldTag).findViewById(R.id.tvFieldValue)
        fieldGoalValue = findViewById<LinearLayout>(R.id.fieldGoal).findViewById(R.id.tvFieldValue)

        // --- Set icon cho từng field ---
        findViewById<LinearLayout>(R.id.fieldUpNext)
            .findViewById<ImageView>(R.id.ivFieldIcon)
            .setImageResource(R.drawable.ic_calendar)

        findViewById<LinearLayout>(R.id.fieldRepeat)
            .findViewById<ImageView>(R.id.ivFieldIcon)
            .setImageResource(R.drawable.ic_repeat)

        findViewById<LinearLayout>(R.id.fieldTime)
            .findViewById<ImageView>(R.id.ivFieldIcon)
            .setImageResource(R.drawable.ic_clock)

        findViewById<LinearLayout>(R.id.fieldReminder)
            .findViewById<ImageView>(R.id.ivFieldIcon)
            .setImageResource(R.drawable.ic_bell)

        findViewById<LinearLayout>(R.id.fieldTag)
            .findViewById<ImageView>(R.id.ivFieldIcon)
            .setImageResource(R.drawable.ic_tag)

        findViewById<LinearLayout>(R.id.fieldGoal)
            .findViewById<ImageView>(R.id.ivFieldIcon)
            .setImageResource(R.drawable.ic_goal)

        // --- Set title cho từng field ---
        findViewById<LinearLayout>(R.id.fieldUpNext)
            .findViewById<TextView>(R.id.tvFieldName)
            .text = "Up Next"
        findViewById<LinearLayout>(R.id.fieldRepeat)
            .findViewById<TextView>(R.id.tvFieldName)
            .text = "Repeat"
        findViewById<LinearLayout>(R.id.fieldTime)
            .findViewById<TextView>(R.id.tvFieldName)
            .text = "Time"
        findViewById<LinearLayout>(R.id.fieldReminder)
            .findViewById<TextView>(R.id.tvFieldName)
            .text = "Reminder"
        findViewById<LinearLayout>(R.id.fieldTag)
            .findViewById<TextView>(R.id.tvFieldName)
            .text = "Tag"
        findViewById<LinearLayout>(R.id.fieldGoal)
            .findViewById<TextView>(R.id.tvFieldName)
            .text = "Goal"

        setupColorSelector()
        setupFieldClicks()

        // Lấy habitId từ intent
        habitId = intent.getIntExtra("habitId", -1)

        if (habitId == -1) {
            // CREATE NEW HABIT
            fieldUpNextValue.text = "Up Next"
            fieldRepeatValue.text = "Repeat"
            fieldTimeValue.text = "Time"
            fieldReminderValue.text = "Reminder"
            fieldTagValue.text = "Tag"
            fieldGoalValue.text = "Goal"
        } else {
            // EDIT HABIT: load dữ liệu từ DB
            val db = DatabaseProvider.getDatabase(this)
            val habitDao = db.habitDao()
            lifecycleScope.launch {
                val habit = habitDao.getHabitById(habitId)
                habit?.let {
                    etHabitName.setText(it.name)
                    selectedRepeat = it.repeat
                    fieldRepeatValue.text = it.repeat

                    selectedUpNext = it.upNext
                    fieldUpNextValue.text = it.upNext?.let { u -> "$u days" } ?: "Select"

                    selectedTimeMode = it.timeMode
                    selectedSpecifiedTime = it.specifiedTime
                    fieldTimeValue.text = if (it.timeMode == "AnyTime") "AnyTime"
                    else it.specifiedTime?.let { t -> String.format("%02d:%02d", t/60, t%60) } ?: "Select"

                    selectedReminderMode = it.reminderMode
                    selectedReminderTime = it.reminderTime
                    fieldReminderValue.text = if (it.reminderMode == "None") "None"
                    else it.reminderTime?.let { t -> String.format("%02d:%02d", t/60, t%60) } ?: "Select"

                    selectedTag = it.tag
                    fieldTagValue.text = it.tag

                    selectedTargetValue = it.targetValue
                    selectedTargetUnit = it.targetUnit
                    fieldGoalValue.text = if (it.targetValue != null) "${it.targetValue} ${it.targetUnit}" else "Select"

                    selectedColor = Color.parseColor(it.color)
                    rootLayout.setBackgroundColor(selectedColor)
                }
            }
        }

        findViewById<TextView>(R.id.tvSaveHabit).setOnClickListener {
            saveHabitToDatabase()
        }
    }

    private fun setupColorSelector() {
        val colorViews = listOf(color1, color2, color3, color4, color5)
        val colorHex = listOf("#FFD6E0", "#FFF3B0", "#C9E4DE", "#D8E2DC", "#B8E0D2")

        colorViews.forEachIndexed { index, view ->
            val drawable = ContextCompat.getDrawable(this, R.drawable.bg_color_circle)!!.mutate() as GradientDrawable
            drawable.setColor(Color.parseColor(colorHex[index]))
            view.background = drawable

            view.setOnClickListener {
                selectedColor = Color.parseColor(colorHex[index])
                rootLayout.setBackgroundColor(selectedColor)

                colorViews.forEach { v ->
                    val bg = v.background
                    if (bg is GradientDrawable) bg.setStroke(0, Color.TRANSPARENT)
                }
                val selectedDrawable = view.background
                if (selectedDrawable is GradientDrawable) selectedDrawable.setStroke(4, Color.BLACK)
            }
        }
    }

    private fun setupFieldClicks() {
        findViewById<LinearLayout>(R.id.fieldUpNext).setOnClickListener { showUpNextDialog() }
        findViewById<LinearLayout>(R.id.fieldRepeat).setOnClickListener { showRepeatDialog() }
        findViewById<LinearLayout>(R.id.fieldTime).setOnClickListener { showTimeDialog() }
        findViewById<LinearLayout>(R.id.fieldReminder).setOnClickListener { showReminderDialog() }
        findViewById<LinearLayout>(R.id.fieldTag).setOnClickListener { showTagDialog() }
        findViewById<LinearLayout>(R.id.fieldGoal).setOnClickListener { showGoalDialog() }
    }

    private fun showUpNextDialog() {
        val items = arrayOf("1", "3", "7", "14", "30")
        AlertDialog.Builder(this)
            .setTitle("Up Next (days)")
            .setItems(items) { _, which ->
                selectedUpNext = items[which].toInt()
                fieldUpNextValue.text = "${selectedUpNext} days"
            }.show()
    }

    private fun showRepeatDialog() {
        val items = arrayOf("Daily", "Weekly", "Custom")
        AlertDialog.Builder(this)
            .setTitle("Repeat")
            .setItems(items) { _, which ->
                selectedRepeat = items[which]
                fieldRepeatValue.text = selectedRepeat
            }.show()
    }

    private fun showTimeDialog() {
        val items = arrayOf("AnyTime", "SpecifiedTime")
        AlertDialog.Builder(this)
            .setTitle("Time")
            .setItems(items) { _, which ->
                selectedTimeMode = items[which]
                if (selectedTimeMode == "AnyTime") {
                    selectedSpecifiedTime = null
                    fieldTimeValue.text = "AnyTime"
                } else {
                    val cal = java.util.Calendar.getInstance()
                    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                    val minute = cal.get(java.util.Calendar.MINUTE)
                    TimePickerDialog(this, { _, h, m ->
                        selectedSpecifiedTime = h * 60 + m
                        fieldTimeValue.text = String.format("%02d:%02d", h, m)
                    }, hour, minute, true).show()
                }
            }.show()
    }

    private fun showReminderDialog() {
        val items = arrayOf("None", "Custom")
        AlertDialog.Builder(this)
            .setTitle("Reminder")
            .setItems(items) { _, which ->
                selectedReminderMode = items[which]
                if (selectedReminderMode == "None") {
                    selectedReminderTime = null
                    fieldReminderValue.text = "None"
                } else {
                    val cal = java.util.Calendar.getInstance()
                    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                    val minute = cal.get(java.util.Calendar.MINUTE)
                    TimePickerDialog(this, { _, h, m ->
                        selectedReminderTime = h * 60 + m
                        fieldReminderValue.text = String.format("%02d:%02d", h, m)
                    }, hour, minute, true).show()
                }
            }.show()
    }

    private fun showTagDialog() {
        AlertDialog.Builder(this)
            .setTitle("Tag")
            .setItems(TAGS) { _, which ->
                selectedTag = TAGS[which]
                fieldTagValue.text = selectedTag
            }.show()
    }

    private fun showGoalDialog() {
        val input = EditText(this)
        input.hint = "Enter target value"
        AlertDialog.Builder(this)
            .setTitle("Goal")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val t = input.text.toString().trim()
                if (t.isNotEmpty()) {
                    selectedTargetValue = t.toIntOrNull()
                    selectedTargetUnit = "unit"
                    fieldGoalValue.text = "$t $selectedTargetUnit"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveHabitToDatabase() {
        val name = etHabitName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter habit name", Toast.LENGTH_SHORT).show()
            return
        }

        val db = DatabaseProvider.getDatabase(this)
        val habitDao = db.habitDao()
        val userDao = db.userDao()

        lifecycleScope.launch {
            var user = userDao.getUserById(1)
            if (user == null) {
                user = User(id = 1, name = "Demo User", email = "user@gmail.com", password = "123456")
                userDao.insertUser(user)
            }

            if (habitId == -1) {
                // CREATE NEW HABIT
                val newHabit = Habit(
                    userId = 1,
                    name = name,
                    repeat = selectedRepeat,
                    upNext = selectedUpNext,
                    timeMode = selectedTimeMode,
                    specifiedTime = selectedSpecifiedTime,
                    reminderMode = selectedReminderMode,
                    reminderTime = selectedReminderTime,
                    tag = selectedTag,
                    targetValue = selectedTargetValue,
                    targetUnit = selectedTargetUnit,
                    color = String.format("#%06X", 0xFFFFFF and selectedColor)
                )
                habitDao.insertHabit(newHabit)
                Toast.makeText(this@EditHabitActivity, "Habit created!", Toast.LENGTH_SHORT).show()
            } else {
                // UPDATE EXISTING HABIT
                val updatedHabit = Habit(
                    id = habitId, // quan trọng
                    userId = 1,
                    name = name,
                    repeat = selectedRepeat,
                    upNext = selectedUpNext,
                    timeMode = selectedTimeMode,
                    specifiedTime = selectedSpecifiedTime,
                    reminderMode = selectedReminderMode,
                    reminderTime = selectedReminderTime,
                    tag = selectedTag,
                    targetValue = selectedTargetValue,
                    targetUnit = selectedTargetUnit,
                    color = String.format("#%06X", 0xFFFFFF and selectedColor)
                )
                habitDao.updateHabit(updatedHabit)
                Toast.makeText(this@EditHabitActivity, "Habit updated!", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@EditHabitActivity, HabitListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}
