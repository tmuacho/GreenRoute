package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.EcoRecommendation
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoRecommendationDao {
    @Query("SELECT * FROM eco_recommendations")
    fun getAllRecommendations(): Flow<List<EcoRecommendation>>

    @Query("SELECT * FROM eco_recommendations WHERE routeId = :routeId")
    fun getRecommendationsByRouteId(routeId: Int): Flow<List<EcoRecommendation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recommendation: EcoRecommendation): Long

    @Update
    suspend fun update(recommendation: EcoRecommendation)

    @Delete
    suspend fun delete(recommendation: EcoRecommendation)
}
