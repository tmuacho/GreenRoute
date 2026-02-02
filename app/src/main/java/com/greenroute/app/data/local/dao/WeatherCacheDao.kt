package com.greenroute.app.data.local.dao

import androidx.room.*
import com.greenroute.app.data.local.entities.WeatherCache
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE city = :city ORDER BY timestamp DESC LIMIT 1")
    suspend fun getWeatherByCity(city: String): WeatherCache?

    @Query("SELECT * FROM weather_cache ORDER BY timestamp DESC")
    fun getAllCachedWeather(): Flow<List<WeatherCache>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherCache): Long

    @Update
    suspend fun update(weather: WeatherCache)

    @Delete
    suspend fun delete(weather: WeatherCache)

    @Query("DELETE FROM weather_cache WHERE timestamp < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
}
