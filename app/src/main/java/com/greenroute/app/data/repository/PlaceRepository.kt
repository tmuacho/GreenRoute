package com.greenroute.app.data.repository

import android.content.Context
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.greenroute.app.data.local.dao.SavedPlaceDao
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Repository for SavedPlace operations and Google Places integration.
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

    /**
     * Search for places using Google Places Autocomplete.
     */
    suspend fun searchPlaces(context: Context, query: String): List<AutocompletePrediction> {
        if (!Places.isInitialized()) return emptyList()
        
        val placesClient = Places.createClient(context)
        val token = AutocompleteSessionToken.newInstance()
        
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()
            
        return try {
            val response = placesClient.findAutocompletePredictions(request).await()
            response.autocompletePredictions
        } catch (e: Exception) {
            Log.e("PlaceRepository", "Error searching places", e)
            emptyList()
        }
    }
}
