package com.example.habittracker.ui.habit.edit

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.habittracker.R

class EditHabitActivity : AppCompatActivity() {

    private lateinit var rootLayout: LinearLayout
    private lateinit var etHabitName: EditText
    private lateinit var color1: View
    private lateinit var color2: View
    private lateinit var color3: View
    private lateinit var color4: View
    private lateinit var color5: View



    private var selectedColor: Int = Color.parseColor("#FFD6E0")
    fun setupField(
        fieldLayout: LinearLayout,
        name: String,
        iconRes: Int? = null,
        value: String? = null,
        usePlusInsteadOfArrow: Boolean = false
    ) {
        val tvName = fieldLayout.findViewById<TextView>(R.id.tvFieldName)
        val tvValue = fieldLayout.findViewById<TextView>(R.id.tvFieldValue)
        val ivFieldIcon = fieldLayout.findViewById<ImageView>(R.id.ivFieldIcon)
        val ivArrow = fieldLayout.findViewById<ImageView>(R.id.ivArrow)

        tvName.text = name
        if (iconRes != null) {
            ivFieldIcon.setImageResource(iconRes)
        }
        if (value != null) {
            tvValue.text = value
        }

        if (usePlusInsteadOfArrow) {
            ivArrow.setImageResource(R.drawable.ic_plus) // icon dấu cộng
        } else {
            ivArrow.setImageResource(R.drawable.ic_arrow_right) // mặc định arrow
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_edit)
        // Lấy layout của từng field
        val fieldUpNext = findViewById<LinearLayout>(R.id.fieldUpNext)
        val fieldRepeat = findViewById<LinearLayout>(R.id.fieldRepeat)
        val fieldTime = findViewById<LinearLayout>(R.id.fieldTime)
        val fieldReminder = findViewById<LinearLayout>(R.id.fieldReminder)
        val fieldTag = findViewById<LinearLayout>(R.id.fieldTag)
        val fieldGoal = findViewById<LinearLayout>(R.id.fieldGoal)
        val fieldSubtasks = findViewById<LinearLayout>(R.id.fieldSubtasks)

        // Lấy root layout
        rootLayout = findViewById(R.id.rootLayout)
        etHabitName = findViewById(R.id.etHabitName)
        color1 = findViewById(R.id.color1)
        color2 = findViewById(R.id.color2)
        color3 = findViewById(R.id.color3)
        color4 = findViewById(R.id.color4)
        color5 = findViewById(R.id.color5)

        val colorViews = listOf(color1, color2, color3, color4, color5)
        val colorHex = listOf("#FFD6E0", "#FFF3B0", "#C9E4DE", "#D8E2DC", "#B8E0D2")

        colorViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                selectedColor = Color.parseColor(colorHex[index])

                // Thay đổi màu nền root layout
                rootLayout.setBackgroundColor(selectedColor)

                // Highlight vòng tròn được chọn
                colorViews.forEach { v ->
                    val drawable = ContextCompat.getDrawable(this, R.drawable.bg_color_circle)!!.mutate() as GradientDrawable
                    drawable.setStroke(0, Color.TRANSPARENT)
                    v.background = drawable
                }
                // Thêm viền cho vòng tròn đang chọn
                val selectedDrawable = view.background.mutate() as GradientDrawable
                selectedDrawable.setStroke(4, Color.BLACK)
                view.background = selectedDrawable
            }
        }

        setupField(fieldUpNext, "Up Next", R.drawable.ic_calendar, "Today")
        setupField(fieldRepeat, "Repeat", R.drawable.ic_repeat, "Every day")
        setupField(fieldTime, "Time", R.drawable.ic_clock, "Anytime")
        setupField(fieldReminder, "Reminder", R.drawable.ic_bell, "No Reminder")
        setupField(fieldTag, "Tag", R.drawable.ic_tag, "No Tag")
        setupField(fieldGoal, "Goal", R.drawable.ic_goal, "20 minutes")
        setupField(fieldSubtasks, "Subtasks", R.drawable.ic_subtask, "", true)
    }
}
