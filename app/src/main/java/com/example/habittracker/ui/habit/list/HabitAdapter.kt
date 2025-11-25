package com.example.habittracker.ui.habit.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.model.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
        val tvEdit: TextView = itemView.findViewById(R.id.tvEdit)
        val tvDelete: TextView = itemView.findViewById(R.id.tvDelete)
        val viewColor: View = itemView.findViewById(R.id.viewColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.tvHabitName.text = habit.name
        holder.viewColor.setBackgroundColor(android.graphics.Color.parseColor(habit.color ?: "#FFD6E0"))

        holder.tvEdit.setOnClickListener {
            onEditClick(habit)
        }

        holder.tvDelete.setOnClickListener {
            onDeleteClick(habit)
        }
    }

    override fun getItemCount(): Int = habits.size

    fun removeItem(habit: Habit) {
        val index = habits.indexOf(habit)
        if (index != -1) {
            habits.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
