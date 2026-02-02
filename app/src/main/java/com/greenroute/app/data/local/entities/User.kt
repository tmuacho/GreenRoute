package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity representing a user profile in the GreenRoute app.
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String? = null,
    val email: String,
    val profileImageUri: String? = null,
    val totalRoutes: Int = 0,
    val totalEmissionSaved: Double = 0.0,
    val joinedAt: Long? = null,
    val lastActive: Long? = null
)
