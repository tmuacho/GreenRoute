package com.greenroute.app.data.remote

data class DirectionsResult(
    val encodedPolyline: String = "",
    val distanceMeters: Int = 0,
    val durationSeconds: Int = 0,
    val originLat: Double = 0.0,
    val originLng: Double = 0.0,
    val destLat: Double = 0.0,
    val destLng: Double = 0.0,
    val steps: List<DirectionsStep> = emptyList()
)

data class DirectionsStep(
    val instruction: String,
    val distanceMeters: Int,
    val durationSeconds: Int,
    val startLat: Double,
    val startLng: Double
)
