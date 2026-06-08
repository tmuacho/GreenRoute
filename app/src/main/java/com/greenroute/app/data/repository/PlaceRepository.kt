package com.greenroute.app.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PlaceSuggestion
import com.google.android.libraries.places.api.net.SearchSuggestionsRequest
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
 *
 * Uses the NEW Places API (searchSuggestions) — requires "Places API (New)"
 * to be enabled in Google Cloud Console, NOT the legacy "Places API".
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

    /**
     * Search for places using the NEW Places API (searchSuggestions).
     * Returns [Result] so callers can show errors in the UI instead of silently failing.
     */
    suspend fun searchPlaces(context: Context, query: String): Result<List<AutocompleteResult>> {
        if (query.length < 2) return Result.success(emptyList())

        if (!Places.isInitialized()) {
            val msg = "Places SDK not initialised — check API key"
            Log.e("PlaceRepository", msg)
            return Result.failure(IllegalStateException(msg))
        }

        val placesClient = Places.createClient(context)

        return try {
            // searchSuggestions uses the NEW Places API endpoint — no session token needed
            val request = SearchSuggestionsRequest.builder(query)
                .setRegionCode("PT")   // bias results toward Portugal
                .build()

            val response = placesClient.searchSuggestions(request).await()

            val results = response.suggestions.mapNotNull { suggestion ->
                // Cast to PlaceSuggestion to access primaryText / secondaryText
                (suggestion as? PlaceSuggestion)?.let { ps ->
                    AutocompleteResult(
                        primaryText  = ps.primaryText.text,
                        secondaryText = ps.secondaryText?.text ?: ""
                    )
                }
            }

            Log.d("PlaceRepository", "searchSuggestions: ${results.size} results for '$query'")
            Result.success(results)

        } catch (e: ApiException) {
            val msg = "Places API error ${e.statusCode}: ${e.message}"
            Log.e("PlaceRepository", msg)
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            val msg = "${e.javaClass.simpleName}: ${e.message}"
            Log.e("PlaceRepository", "searchSuggestions failed — $msg")
            Result.failure(Exception(msg))
        }
    }

    /** No-op kept for API compatibility with SearchViewModel. */
    fun resetSessionToken() = Unit
}
