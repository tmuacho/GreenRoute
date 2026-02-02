package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE userId = :userId")
    fun getAchievementsByUserId(userId: Int): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE earned = 1")
    fun getEarnedAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: Achievement): Long

    @Update
    suspend fun update(achievement: Achievement)

    @Delete
    suspend fun delete(achievement: Achievement)
}
