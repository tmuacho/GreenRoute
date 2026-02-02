package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.Route
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes ORDER BY dateTime DESC")
    fun getAllRoutes(): Flow<List<Route>>

    @Query("SELECT * FROM routes WHERE userId = :userId ORDER BY dateTime DESC")
    fun getRoutesByUserId(userId: Int): Flow<List<Route>>

    @Query("SELECT * FROM routes WHERE isSaved = 1 ORDER BY dateTime DESC")
    fun getSavedRoutes(): Flow<List<Route>>

    @Query("SELECT * FROM routes ORDER BY dateTime DESC LIMIT :limit")
    fun getRecentRoutes(limit: Int = 10): Flow<List<Route>>

    @Query("SELECT * FROM routes WHERE id = :id")
    suspend fun getRouteById(id: Int): Route?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: Route): Long

    @Update
    suspend fun update(route: Route)

    @Delete
    suspend fun delete(route: Route)

    @Query("UPDATE routes SET isSaved = :isSaved WHERE id = :routeId")
    suspend fun updateSavedStatus(routeId: Int, isSaved: Boolean)

    @Query("DELETE FROM routes")
    suspend fun deleteAll()
}
