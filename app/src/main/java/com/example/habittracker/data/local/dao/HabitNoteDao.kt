package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.HabitNote

@Dao
interface HabitNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: HabitNote): Long

    @Update
    suspend fun updateNote(note: HabitNote)

    @Delete
    suspend fun deleteNote(note: HabitNote)

    @Query("SELECT * FROM habit_notes WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getNotesByHabit(habitId: Int): List<HabitNote>
}
