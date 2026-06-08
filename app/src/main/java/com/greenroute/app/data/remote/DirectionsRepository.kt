package com.greenroute.app.data.remote

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class DirectionsRepository(private val apiKey: String) {

    suspend fun getDirections(
        origin: String,
        destination: String,
        transportType: String
    ): DirectionsResult? = withContext(Dispatchers.IO) {
        try {
            val mode = toApiMode(transportType)
            val transitParam = toTransitMode(transportType)?.let { "&transit_mode=$it" } ?: ""

            val encodedOrigin = URLEncoder.encode(origin, "UTF-8")
            val encodedDest = URLEncoder.encode(destination, "UTF-8")

            val urlStr = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=$encodedOrigin" +
                "&destination=$encodedDest" +
                "&mode=$mode" +
                "$transitParam" +
                "&language=pt-PT" +
                "&key=$apiKey"

            val response = URL(urlStr).readText()
            val json = JSONObject(response)

            val status = json.getString("status")
            if (status != "OK") {
                Log.w("DirectionsRepo", "API status: $status for \"$origin\" -> \"$destination\"")
                return@withContext null
            }

            val route = json.getJSONArray("routes").getJSONObject(0)
            val leg = route.getJSONArray("legs").getJSONObject(0)
            val encodedPolyline = route.getJSONObject("overview_polyline").getString("points")

            val distanceMeters = leg.getJSONObject("distance").getInt("value")
            val durationSeconds = leg.getJSONObject("duration").getInt("value")
            val startLoc = leg.getJSONObject("start_location")
            val endLoc = leg.getJSONObject("end_location")

            val steps = mutableListOf<DirectionsStep>()
            val stepsArray = leg.getJSONArray("steps")
            for (i in 0 until stepsArray.length()) {
                val step = stepsArray.getJSONObject(i)
                val stepStart = step.getJSONObject("start_location")
                val instruction = step.getString("html_instructions")
                    .replace(Regex("<[^>]*>"), " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()
                steps.add(
                    DirectionsStep(
                        instruction = instruction,
                        distanceMeters = step.getJSONObject("distance").getInt("value"),
                        durationSeconds = step.getJSONObject("duration").getInt("value"),
                        startLat = stepStart.getDouble("lat"),
                        startLng = stepStart.getDouble("lng")
                    )
                )
            }

            DirectionsResult(
                encodedPolyline = encodedPolyline,
                distanceMeters = distanceMeters,
                durationSeconds = durationSeconds,
                originLat = startLoc.getDouble("lat"),
                originLng = startLoc.getDouble("lng"),
                destLat = endLoc.getDouble("lat"),
                destLng = endLoc.getDouble("lng"),
                steps = steps
            )
        } catch (e: Exception) {
            Log.e("DirectionsRepo", "Error fetching directions", e)
            null
        }
    }

    private fun toApiMode(type: String) = when (type) {
        "car" -> "driving"
        "walk" -> "walking"
        "bike" -> "bicycling"
        "bus", "train", "metro" -> "transit"
        else -> "driving"
    }

    private fun toTransitMode(type: String) = when (type) {
        "bus" -> "bus"
        "train" -> "train"
        "metro" -> "subway"
        else -> null
    }

    companion object {
        fun decodePolyline(encoded: String): List<LatLng> {
            val poly = mutableListOf<LatLng>()
            var index = 0
            var lat = 0
            var lng = 0
            while (index < encoded.length) {
                var b: Int; var shift = 0; var result = 0
                do {
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
                shift = 0; result = 0
                do {
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
                poly.add(LatLng(lat / 1E5, lng / 1E5))
            }
            return poly
        }
    }
}
