package com.greenroute.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager

/**
 * Gets the last known device location without triggering a new fix.
 * Tries GPS → NETWORK → PASSIVE providers in order.
 * Returns null if permission is not granted or no fix is cached.
 */
@SuppressLint("MissingPermission")
fun getLastKnownLocation(context: Context): Pair<Double, Double>? {
    return try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        loc?.let { Pair(it.latitude, it.longitude) }
    } catch (_: Exception) {
        null
    }
}
