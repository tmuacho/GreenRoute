package com.greenroute.app.data.repository

import android.util.Log
import com.greenroute.app.data.local.dao.SavedPlaceDao
import com.greenroute.app.data.local.entities.SavedPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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
 * Repository for SavedPlace operations and Places autocomplete.
 *
 * Autocomplete uses the Places API (New) via HTTP POST — bypasses the legacy
 * SDK endpoint which requires "Places API (Legacy)" to be enabled (error 9011).
 *
 * Endpoint: POST https://places.googleapis.com/v1/places:autocomplete
 *
 * When the API key has "Android app" restrictions in Google Cloud Console,
 * Google requires [X-Android-Package] and [X-Android-Cert] headers.
 * Pass [appPackageName] and [signatureSha1] from GreenRouteApp so these
 * are set automatically on every request.
 */
class PlaceRepository(
    private val savedPlaceDao: SavedPlaceDao,
    private val apiKey: String,
    private val appPackageName: String = "",
    private val signatureSha1: String  = ""
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
     * Search for places using the Places Autocomplete (New) REST API.
     * Returns [Result] so the caller can show errors in the UI.
     */
    suspend fun searchPlaces(query: String): Result<List<AutocompleteResult>> {
        if (query.length < 2) return Result.success(emptyList())
        if (apiKey.isEmpty()) return Result.failure(Exception("API key not configured"))

        return withContext(Dispatchers.IO) {
            try {
                val conn = URL(AUTOCOMPLETE_URL).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                // Primary auth header
                conn.setRequestProperty("X-Goog-Api-Key", apiKey)
                // Android app restriction headers — required when the API key is restricted
                // to "Android apps" in Google Cloud Console
                if (appPackageName.isNotEmpty()) {
                    conn.setRequestProperty("X-Android-Package", appPackageName)
                }
                if (signatureSha1.isNotEmpty()) {
                    conn.setRequestProperty("X-Android-Cert", signatureSha1)
                }
                conn.connectTimeout = 6_000
                conn.readTimeout   = 6_000
                conn.doOutput = true

                val safeQuery = query.replace("\\", "\\\\").replace("\"", "\\\"")
                val body = """{"input":"$safeQuery","regionCode":"PT"}"""
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                val code = conn.responseCode
                val text = if (code == 200) {
                    conn.inputStream.bufferedReader(Charsets.UTF_8).readText()
                } else {
                    val err = conn.errorStream?.bufferedReader(Charsets.UTF_8)?.readText() ?: ""
                    conn.disconnect()
                    return@withContext Result.failure(Exception("HTTP $code: $err"))
                }
                conn.disconnect()

                val results = parseResponse(text)
                Log.d(TAG, "Got ${results.size} suggestions for '$query'")
                Result.success(results)

            } catch (e: Exception) {
                Log.e(TAG, "Autocomplete failed: ${e.javaClass.simpleName}: ${e.message}")
                Result.failure(e)
            }
        }
    }

    private fun parseResponse(json: String): List<AutocompleteResult> {
        return try {
            val suggestions = JSONObject(json).optJSONArray("suggestions")
                ?: return emptyList()

            (0 until suggestions.length()).mapNotNull { i ->
                val pred = suggestions.getJSONObject(i)
                    .optJSONObject("placePrediction") ?: return@mapNotNull null
                val sf = pred.optJSONObject("structuredFormat") ?: return@mapNotNull null

                val main      = sf.optJSONObject("mainText")?.optString("text", "")      ?: ""
                val secondary = sf.optJSONObject("secondaryText")?.optString("text", "") ?: ""

                if (main.isEmpty()) null
                else AutocompleteResult(primaryText = main, secondaryText = secondary)
            }
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse error: ${e.message}")
            emptyList()
        }
    }

    /** No-op — kept for API compatibility; new Places API doesn't use session tokens. */
    fun resetSessionToken() = Unit

    private companion object {
        const val TAG = "PlaceRepository"
        const val AUTOCOMPLETE_URL = "https://places.googleapis.com/v1/places:autocomplete"
    }
}
