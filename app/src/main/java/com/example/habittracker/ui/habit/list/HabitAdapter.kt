package com.example.habittracker.ui.habit.list

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.model.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onItemClick: (Habit) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCheck: ImageView = itemView.findViewById(R.id.imgCheck)
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

        // --- Hiển thị tên và màu ---
        holder.tvHabitName.text = habit.name
        holder.viewColor.setBackgroundColor(android.graphics.Color.parseColor(habit.color ?: "#FFD6E0"))

        // --- Slide animation khi item xuất hiện ---
        val slideAnim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)
        holder.itemView.startAnimation(slideAnim)

        // --- Item scale khi nhấn ---
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(1.03f).scaleY(1.03f).setDuration(100).start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            false
        }


        // --- Edit ---
        holder.tvEdit.setOnClickListener {
            it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).duration = 100
            }.start()
            onEditClick(habit)
        }

        // --- Delete ---
        holder.tvDelete.setOnClickListener {
            it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).duration = 100
            }.start()
            onDeleteClick(habit)
        }

        // --- Click item bình thường ---
        holder.itemView.setOnClickListener {
            onItemClick(habit)
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

    fun updateItem(habit: Habit) {
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            notifyItemChanged(index)
        }
    }
}
