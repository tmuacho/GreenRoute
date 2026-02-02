package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Eco recommendations linked to specific routes.
 */
@Entity(
    tableName = "eco_recommendations",
    indices = [Index(value = ["routeId"])],
    foreignKeys = [
        ForeignKey(
            entity = Route::class,
            parentColumns = ["id"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EcoRecommendation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routeId: Int? = null,
    val message: String? = null,
    val score: Double = 0.0
)
