package com.example.habittracker.data.local.dao

import androidx.room.*
import com.example.habittracker.data.model.UserSettings

@Dao
interface UserSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings): Long

    @Update
    suspend fun updateSettings(settings: UserSettings)

    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettingsByUser(userId: Int): UserSettings?
}
