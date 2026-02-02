package com.greenroute.app.data.repository

import com.greenroute.app.data.local.dao.SavedPlaceDao
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.flow.Flow

/**
 * Repository for SavedPlace operations.
 */
class PlaceRepository(
    private val savedPlaceDao: SavedPlaceDao
) {
    val allSavedPlaces: Flow<List<SavedPlace>> = savedPlaceDao.getAllSavedPlaces()

    fun getSavedPlacesByUserId(userId: Int): Flow<List<SavedPlace>> =
        savedPlaceDao.getSavedPlacesByUserId(userId)

    suspend fun getSavedPlaceById(id: Int): SavedPlace? =
        savedPlaceDao.getSavedPlaceById(id)

    suspend fun insert(place: SavedPlace): Long = savedPlaceDao.insert(place)

    suspend fun update(place: SavedPlace) = savedPlaceDao.update(place)

    suspend fun delete(place: SavedPlace) = savedPlaceDao.delete(place)
}
