package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Aggregated eco statistics for a user.
 */
@Entity(
    tableName = "eco_stats",
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
data class EcoStats(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int? = null,
    val totalDistance: Double = 0.0,
    val totalEmissionSaved: Double = 0.0,
    val routesCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
