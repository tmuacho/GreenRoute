package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaceDao {
    @Query("SELECT * FROM saved_places")
    fun getAllSavedPlaces(): Flow<List<SavedPlace>>

    @Query("SELECT * FROM saved_places WHERE userId = :userId")
    fun getSavedPlacesByUserId(userId: Int): Flow<List<SavedPlace>>

    @Query("SELECT * FROM saved_places WHERE id = :id")
    suspend fun getSavedPlaceById(id: Int): SavedPlace?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: SavedPlace): Long

    @Update
    suspend fun update(place: SavedPlace)

    @Delete
    suspend fun delete(place: SavedPlace)
}
