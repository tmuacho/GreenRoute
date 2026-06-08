package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Route entity representing a trip/journey in the app.
 */
@Entity(
    tableName = "routes",
    indices = [Index(value = ["userId"])],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int? = null,
    val startLocation: String? = null,
    val endLocation: String? = null,
    val transportType: String? = null,
    val co2Emission: Double? = null,
    val dateTime: Long? = null,
    val distance: Double = 0.0,
    val duration: Int = 0,
    val isSaved: Boolean = false,
    val encodedPolyline: String? = null,
    val originLat: Double? = null,
    val originLng: Double? = null,
    val destLat: Double? = null,
    val destLng: Double? = null
)
