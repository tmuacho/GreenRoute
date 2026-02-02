package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached weather data for cities.
 */
@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val city: String? = null,
    val temperature: Double = 0.0,
    val conditions: String = "",
    val timestamp: Long? = null
)
