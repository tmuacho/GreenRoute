package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.EmissionFactor
import kotlinx.coroutines.flow.Flow

@Dao
interface EmissionFactorDao {
    @Query("SELECT * FROM emission_factors")
    fun getAllFactors(): Flow<List<EmissionFactor>>

    @Query("SELECT * FROM emission_factors WHERE transportType = :transportType")
    suspend fun getFactorByType(transportType: String): EmissionFactor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(factor: EmissionFactor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(factors: List<EmissionFactor>)

    @Update
    suspend fun update(factor: EmissionFactor)

    @Delete
    suspend fun delete(factor: EmissionFactor)
}
