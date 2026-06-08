package com.greenroute.app.data.repository

import android.content.Context
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.greenroute.app.data.local.dao.SavedPlaceDao
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Repository for SavedPlace operations and Google Places autocomplete.
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

    // ── Autocomplete ─────────────────────────────────────────────────────────

    // Reuse the same token within a search session (create new after place selection)
    private var sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    /**
     * Search for places using Google Places Autocomplete.
     * Results are biased toward Portugal.
     */
    suspend fun searchPlaces(context: Context, query: String): List<AutocompletePrediction> {
        if (!Places.isInitialized()) {
            Log.w("PlaceRepository", "Places SDK not initialized")
            return emptyList()
        }
        if (query.length < 2) return emptyList()

        val placesClient = Places.createClient(context)

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            // Bias results toward Portugal; still shows other countries if needed
            .setCountries(listOf("PT"))
            .build()

        return try {
            val response = placesClient.findAutocompletePredictions(request).await()
            response.autocompletePredictions
        } catch (e: Exception) {
            Log.e("PlaceRepository", "Autocomplete error: ${e.message}")
            emptyList()
        }
    }

    /** Call after the user selects a place to start a fresh billing session. */
    fun resetSessionToken() {
        sessionToken = AutocompleteSessionToken.newInstance()
    }
}
