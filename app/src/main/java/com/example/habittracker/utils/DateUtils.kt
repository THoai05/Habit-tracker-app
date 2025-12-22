package com.example.habittracker.utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
object DateUtils {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getCurrentDate(): String {
        return LocalDate.now().format(formatter)
    }

    fun getYesterdayDate(): String {
        return LocalDate.now().minusDays(1).format(formatter)
    }

    // Hàm kiểm tra khoảng cách giữa 2 ngày (trả về số ngày)
    fun daysBetween(dateString1: String, dateString2: String): Long {
        return try {
            val d1 = LocalDate.parse(dateString1, formatter)
            val d2 = LocalDate.parse(dateString2, formatter)
            // Tính trị tuyệt đối khoảng cách
            ChronoUnit.DAYS.between(d2, d1)
        } catch (e: Exception) {
            0
        }
    }
    fun today(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
