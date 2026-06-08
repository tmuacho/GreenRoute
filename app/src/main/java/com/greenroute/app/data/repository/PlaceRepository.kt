package com.greenroute.app.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.greenroute.app.data.local.dao.SavedPlaceDao
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Simple autocomplete result — primary text (place name) + secondary text (city/country).
 */
data class AutocompleteResult(
    val primaryText: String,
    val secondaryText: String
) {
    /** Full string used as destination for the Directions API. */
    val fullText: String
        get() = if (secondaryText.isEmpty()) primaryText else "$primaryText, $secondaryText"
}

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

    // Reuse the same token within a search session (reset after place selection)
    private var sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    /**
     * Search for places using the Places Autocomplete API.
     * Returns a [Result] so the caller can distinguish success / empty / error.
     */
    suspend fun searchPlaces(context: Context, query: String): Result<List<AutocompleteResult>> {
        if (query.length < 2) return Result.success(emptyList())

        if (!Places.isInitialized()) {
            val msg = "Places SDK not initialised — check API key setup"
            Log.e("PlaceRepository", msg)
            return Result.failure(IllegalStateException(msg))
        }

        val placesClient = Places.createClient(context)

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            // Bias toward Portugal; the API still returns other results when relevant
            .setCountries(listOf("PT"))
            .build()

        return try {
            val response = placesClient.findAutocompletePredictions(request).await()
            val results = response.autocompletePredictions.map { p ->
                AutocompleteResult(
                    primaryText = p.getPrimaryText(null).toString(),
                    secondaryText = p.getSecondaryText(null).toString()
                )
            }
            Log.d("PlaceRepository", "Got ${results.size} predictions for '$query'")
            Result.success(results)
        } catch (e: ApiException) {
            val msg = "Places API error ${e.statusCode}: ${e.message}"
            Log.e("PlaceRepository", msg)
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            val msg = "${e.javaClass.simpleName}: ${e.message}"
            Log.e("PlaceRepository", "Autocomplete failed — $msg")
            Result.failure(Exception(msg))
        }
    }

    /** Call after the user selects a place to start a fresh billing session. */
    fun resetSessionToken() {
        sessionToken = AutocompleteSessionToken.newInstance()
    }
}
