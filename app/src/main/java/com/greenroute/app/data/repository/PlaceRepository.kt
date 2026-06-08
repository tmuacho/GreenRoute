package com.greenroute.app.data.repository

import android.util.Log
import com.greenroute.app.data.local.dao.SavedPlaceDao
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Simple autocomplete result — primary text (place name) + secondary text (region/country).
 *
 * [directionQuery] is the full address string passed to the Directions API.
 * Defaults to "primaryText, secondaryText" when not set.
 */
data class AutocompleteResult(
    val primaryText: String,
    val secondaryText: String,
    val directionQuery: String = ""
) {
    /** Full address string used as destination for the Directions API. */
    val fullText: String
        get() = directionQuery.ifEmpty {
            if (secondaryText.isEmpty()) primaryText else "$primaryText, $secondaryText"
        }
}

/**
 * Repository for SavedPlace operations and place autocomplete.
 *
 * Autocomplete uses Nominatim (OpenStreetMap) — free, no API key required,
 * works with any Android API key configuration.
 *
 * Google Places API was attempted but blocked by key restrictions (API_KEY_SERVICE_BLOCKED).
 * Nominatim is the standard fallback and works well for Portuguese locations.
 */
class PlaceRepository(
    private val savedPlaceDao: SavedPlaceDao,
    private val apiKey: String = "",
    private val appPackageName: String = "",
    private val signatureSha1: String = ""
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
     * Search for places using Nominatim (OpenStreetMap geocoding API).
     * Results are biased to Portugal via [countrycodes=pt].
     * Returns [Result] so callers can display errors in the UI.
     */
    suspend fun searchPlaces(query: String): Result<List<AutocompleteResult>> {
        if (query.length < 2) return Result.success(emptyList())

        return withContext(Dispatchers.IO) {
            try {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val url = "$NOMINATIM_URL?q=$encoded" +
                        "&countrycodes=pt" +
                        "&format=json" +
                        "&limit=6" +
                        "&addressdetails=1" +
                        "&accept-language=pt-PT,pt"

                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                // Nominatim policy requires a descriptive User-Agent
                conn.setRequestProperty("User-Agent", "GreenRoute Android App/1.0")
                conn.connectTimeout = 6_000
                conn.readTimeout   = 6_000

                val code = conn.responseCode
                val text = if (code == 200) {
                    conn.inputStream.bufferedReader(Charsets.UTF_8).readText()
                } else {
                    conn.disconnect()
                    return@withContext Result.failure(Exception("HTTP $code"))
                }
                conn.disconnect()

                val results = parseNominatim(text)
                Log.d(TAG, "Nominatim: ${results.size} results for '$query'")
                Result.success(results)

            } catch (e: Exception) {
                Log.e(TAG, "Autocomplete failed: ${e.javaClass.simpleName}: ${e.message}")
                Result.failure(e)
            }
        }
    }

    private fun parseNominatim(json: String): List<AutocompleteResult> {
        return try {
            val array = JSONArray(json)

            (0 until array.length()).mapNotNull { i ->
                val obj   = array.getJSONObject(i)
                val addr  = obj.optJSONObject("address")
                val displayName = obj.optString("display_name", "")

                // Primary text = most specific component available
                val primary = addr?.run {
                    optString("city",         "")
                        .ifEmpty { optString("town",         "") }
                        .ifEmpty { optString("village",      "") }
                        .ifEmpty { optString("municipality", "") }
                        .ifEmpty { optString("county",       "") }
                        .ifEmpty { optString("suburb",       "") }
                } ?: ""

                // Fallback: first segment of display_name
                val finalPrimary = primary.ifEmpty {
                    displayName.split(",").firstOrNull()?.trim() ?: ""
                }
                if (finalPrimary.isEmpty()) return@mapNotNull null

                // Secondary text = district + country
                val secondary = addr?.run {
                    val district = optString("state",   "")
                    val country  = optString("country", "")
                    when {
                        district.isNotEmpty() && country.isNotEmpty() -> "$district, $country"
                        country.isNotEmpty()  -> country
                        else -> ""
                    }
                } ?: ""

                AutocompleteResult(
                    primaryText   = finalPrimary,
                    secondaryText = secondary,
                    // Pass full display_name as the Directions API query for best geocoding
                    directionQuery = displayName
                )
            }
            // Remove duplicates that differ only in capitalisation
            .distinctBy { it.primaryText.lowercase() + "|" + it.secondaryText.lowercase() }

        } catch (e: Exception) {
            Log.e(TAG, "Nominatim parse error: ${e.message}")
            emptyList()
        }
    }

    /** No-op — kept for API compatibility. */
    fun resetSessionToken() = Unit

    private companion object {
        const val TAG = "PlaceRepository"
        const val NOMINATIM_URL = "https://nominatim.openstreetmap.org/search"
    }
}
