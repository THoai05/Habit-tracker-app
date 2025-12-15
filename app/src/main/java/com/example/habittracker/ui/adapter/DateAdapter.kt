package com.example.habittracker.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// Sửa constructor: private val dates: List<String>
class DateAdapter(
    private val dates: List<String>, // <--- Phải là List<String>
    private val onDateClick: (String) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = dates.size / 2

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        val tvDayOfMonth: TextView = itemView.findViewById(R.id.tvDayOfMonth)
        val container: LinearLayout = itemView.findViewById(R.id.itemDateLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val dateString = dates[position]

        // Parse String thành LocalDate để lấy thứ/ngày hiển thị
        val date = LocalDate.parse(dateString)

        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
        holder.tvDayOfWeek.text = dayOfWeek
        holder.tvDayOfMonth.text = date.dayOfMonth.toString()

        // Logic đổi màu khi chọn
        if (position == selectedPosition) {
            holder.container.setBackgroundResource(R.drawable.bg_date_selected)
            holder.tvDayOfWeek.setTextColor(Color.WHITE)
            holder.tvDayOfMonth.setTextColor(Color.WHITE)
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_date_unselected)
            holder.tvDayOfWeek.setTextColor(Color.GRAY)
            holder.tvDayOfMonth.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onDateClick(dateString) // Trả về String
        }
    }

    override fun getItemCount(): Int = dates.size
}