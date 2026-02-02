package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Saved places/locations that users bookmark for quick access.
 */
@Entity(
    tableName = "saved_places",
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
data class SavedPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int? = null,
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
