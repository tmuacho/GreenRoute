package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getPreferencesByUserId(userId: Int): Flow<UserPreferences?>

    @Query("SELECT * FROM user_preferences LIMIT 1")
    fun getCurrentPreferences(): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferences: UserPreferences): Long

    @Update
    suspend fun update(preferences: UserPreferences)

    @Delete
    suspend fun delete(preferences: UserPreferences)
}
