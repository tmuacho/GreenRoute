package com.greenroute.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CO2 emission factors per transport type (grams of CO2 per km).
 */
@Entity(tableName = "emission_factors")
data class EmissionFactor(
    @PrimaryKey
    val transportType: String,
    val factor: Double? = null  // grams of CO2 per km
)
