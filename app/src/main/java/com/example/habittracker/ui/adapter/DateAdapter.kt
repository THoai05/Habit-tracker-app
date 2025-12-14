package com.example.habittracker.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DateAdapter(
    private val days: List<LocalDate>,
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = days.indexOf(LocalDate.now()) // Mặc định chọn hôm nay

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        val tvDayOfMonth: TextView = itemView.findViewById(R.id.tvDayOfMonth)
        val layout: View = itemView.findViewById(R.id.itemDateLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = days[position]

        // Format ngày
        holder.tvDayOfWeek.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        holder.tvDayOfMonth.text = date.dayOfMonth.toString()

        // Xử lý màu sắc khi được chọn
        if (position == selectedPosition) {
            holder.layout.setBackgroundResource(R.drawable.bg_date_selected)
            holder.tvDayOfWeek.setTextColor(Color.WHITE)
            holder.tvDayOfMonth.setTextColor(Color.WHITE)
        } else {
            holder.layout.setBackgroundResource(R.drawable.bg_date_unselected)
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#9E9E9E"))
            holder.tvDayOfMonth.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            val previousItem = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousItem)
            notifyItemChanged(selectedPosition)
            onDateClick(date)
        }
    }

    override fun getItemCount(): Int = days.size
}