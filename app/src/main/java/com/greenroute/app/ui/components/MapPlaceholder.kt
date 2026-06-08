package com.greenroute.app.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Real map component using Google Maps.
 * Height adapts to screen size: 22 % of screen height, clamped to 150–220 dp.
 *
 * When [hasLocationPermission] is true, Google Maps shows the native blue-dot
 * indicator at the user's current position.
 * When [userLat]/[userLng] are provided the camera animates to that location.
 */
@SuppressLint("MissingPermission")
@Composable
fun MapPlaceholder(
    modifier: Modifier = Modifier,
    userLat: Double? = null,
    userLng: Double? = null,
    hasLocationPermission: Boolean = false,
    onLocationClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val mapHeight = (configuration.screenHeightDp * 0.22f)
        .coerceIn(150f, 220f)
        .dp

    val lisbon = LatLng(38.7223, -9.1393)
    val userLocation = if (userLat != null && userLng != null) LatLng(userLat, userLng) else null

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lisbon, 12f)
    }

    // Animate to user location when coords become available
    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f),
                durationMs = 900
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(mapHeight)
            .clip(RoundedCornerShape(16.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                // isMyLocationEnabled shows the native pulsing blue dot.
                // Only enable when permission is actually granted to avoid SecurityException.
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                // Hide the "Go to my location" button — we already centre the camera ourselves
                myLocationButtonEnabled = false
            )
        )
    }
}
