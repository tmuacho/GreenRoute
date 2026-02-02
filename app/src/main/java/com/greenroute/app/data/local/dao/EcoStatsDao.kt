package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.EcoStats
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoStatsDao {
    @Query("SELECT * FROM eco_stats WHERE userId = :userId")
    fun getStatsByUserId(userId: Int): Flow<EcoStats?>

    @Query("SELECT * FROM eco_stats LIMIT 1")
    fun getCurrentStats(): Flow<EcoStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: EcoStats): Long

    @Update
    suspend fun update(stats: EcoStats)

    @Delete
    suspend fun delete(stats: EcoStats)
}
