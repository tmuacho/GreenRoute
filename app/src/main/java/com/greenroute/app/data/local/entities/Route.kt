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
    val distance: Double = 0.0,        // Distance in km
    val duration: Int = 0,              // Duration in minutes
    val isSaved: Boolean = false        // Whether the route is saved/bookmarked
)
