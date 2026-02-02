package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User preferences for app settings and customization.
 */
@Entity(
    tableName = "user_preferences",
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
data class UserPreferences(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int? = null,
    val preferredTransport: String = "car",
    val ecoModeEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val language: String = "pt"
)
