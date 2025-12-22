package com.example.habittracker.ui.habit.suggested

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import android.content.Intent
import com.example.habittracker.ui.habit.edit.EditHabitActivity

// Adapter cho Suggested Habits
class SuggestedHabitAdapter(
    private val habits: List<SuggestedHabit>,
    private val onClick: (SuggestedHabit) -> Unit
) : RecyclerView.Adapter<SuggestedHabitAdapter.SuggestedViewHolder>() {

    class SuggestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvHabitName)
        val viewColor: View = itemView.findViewById(R.id.viewColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggested_habit, parent, false)
        return SuggestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestedViewHolder, position: Int) {
        val habit = habits[position]
        holder.tvName.text = habit.name
        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(habit.color ?: "#FFD6E0"))
        } catch (e: Exception) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#FFD6E0"))
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditHabitActivity::class.java)
            intent.putExtra("habitName", habit.name)
            intent.putExtra("habitColor", habit.color)
            intent.putExtra("habitDuration", habit.defaultDuration)
            intent.putExtra("habitUpNext", habit.upNextDays)
            intent.putExtra("habitRepeat", habit.repeat)
            intent.putExtra("habitTimeMode", habit.timeMode)
            intent.putExtra("habitReminderMode", habit.reminderMode)
            intent.putExtra("habitTag", habit.tag)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = habits.size
}

// Data class cho Suggested Habit
data class SuggestedHabit(
    val name: String,
    val color: String? = null,
    val defaultDuration: Int = 1,       // số lần / giờ
    val upNextDays: Int = 1,            // cho fieldUpNext
    val repeat: String = "Daily",       // cho fieldRepeat
    val timeMode: String = "AnyTime",   // cho fieldTime
    val reminderMode: String = "None",  // cho fieldReminder
    val tag: String = "No tag"          // cho fieldTag
): java.io.Serializable




