package com.greenroute.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.greenroute.app.data.local.dao.*
import com.greenroute.app.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        UserPreferences::class,
        Route::class,
        SavedPlace::class,
        EcoStats::class,
        EcoRecommendation::class,
        Achievement::class,
        EmissionFactor::class,
        WeatherCache::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun routeDao(): RouteDao
    abstract fun savedPlaceDao(): SavedPlaceDao
    abstract fun ecoStatsDao(): EcoStatsDao
    abstract fun ecoRecommendationDao(): EcoRecommendationDao
    abstract fun achievementDao(): AchievementDao
    abstract fun emissionFactorDao(): EmissionFactorDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "greenroute_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback to prepopulate database with mock data on first creation.
     */
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            // Insert default user
            val userId = database.userDao().insert(
                User(
                    name = "Tiago",
                    email = "tiago@greenroute.com",
                    totalRoutes = 15,
                    totalEmissionSaved = 2450.0,
                    joinedAt = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000,
                    lastActive = System.currentTimeMillis()
                )
            )

            // Insert user preferences
            database.userPreferencesDao().insert(
                UserPreferences(
                    userId = userId.toInt(),
                    preferredTransport = "metro",
                    ecoModeEnabled = true,
                    notificationsEnabled = true,
                    language = "pt"
                )
            )

            // Insert emission factors (grams of CO2 per km)
            database.emissionFactorDao().insertAll(
                listOf(
                    EmissionFactor("car", 120.0),
                    EmissionFactor("bus", 68.0),
                    EmissionFactor("train", 35.0),
                    EmissionFactor("metro", 40.0),
                    EmissionFactor("walk", 0.0),
                    EmissionFactor("bike", 0.0),
                    EmissionFactor("electric_car", 50.0),
                    EmissionFactor("motorcycle", 90.0)
                )
            )

            // Insert sample routes
            val now = System.currentTimeMillis()
            database.routeDao().insert(
                Route(
                    userId = userId.toInt(),
                    startLocation = "Saldanha",
                    endLocation = "Entrecampos",
                    transportType = "metro",
                    co2Emission = 188.0,
                    dateTime = now - 2 * 60 * 60 * 1000,
                    distance = 4.7,
                    duration = 17,
                    isSaved = true
                )
            )

            database.routeDao().insert(
                Route(
                    userId = userId.toInt(),
                    startLocation = "Alameda",
                    endLocation = "Baixa-Chiado",
                    transportType = "metro",
                    co2Emission = 156.0,
                    dateTime = now - 24 * 60 * 60 * 1000,
                    distance = 3.9,
                    duration = 12,
                    isSaved = false
                )
            )

            database.routeDao().insert(
                Route(
                    userId = userId.toInt(),
                    startLocation = "Rossio",
                    endLocation = "Parque das Nações",
                    transportType = "bus",
                    co2Emission = 346.0,
                    dateTime = now - 48 * 60 * 60 * 1000,
                    distance = 5.1,
                    duration = 25,
                    isSaved = true
                )
            )

            database.routeDao().insert(
                Route(
                    userId = userId.toInt(),
                    startLocation = "Marquês de Pombal",
                    endLocation = "Aeroporto",
                    transportType = "metro",
                    co2Emission = 280.0,
                    dateTime = now - 72 * 60 * 60 * 1000,
                    distance = 7.0,
                    duration = 22,
                    isSaved = false
                )
            )

            database.routeDao().insert(
                Route(
                    userId = userId.toInt(),
                    startLocation = "Campo Grande",
                    endLocation = "Cais do Sodré",
                    transportType = "train",
                    co2Emission = 174.0,
                    dateTime = now - 96 * 60 * 60 * 1000,
                    distance = 5.0,
                    duration = 23,
                    isSaved = true
                )
            )

            // Insert eco stats
            database.ecoStatsDao().insert(
                EcoStats(
                    userId = userId.toInt(),
                    totalDistance = 125.7,
                    totalEmissionSaved = 2450.0,
                    routesCount = 15,
                    lastUpdated = now
                )
            )

            // Insert achievements
            database.achievementDao().insert(
                Achievement(
                    userId = userId.toInt(),
                    title = "Primeira Viagem",
                    description = "Completaste a tua primeira viagem ecológica!",
                    earned = true,
                    dateEarned = now - 30L * 24 * 60 * 60 * 1000
                )
            )

            database.achievementDao().insert(
                Achievement(
                    userId = userId.toInt(),
                    title = "Eco Warrior",
                    description = "Poupaste mais de 1kg de CO2!",
                    earned = true,
                    dateEarned = now - 15L * 24 * 60 * 60 * 1000
                )
            )

            database.achievementDao().insert(
                Achievement(
                    userId = userId.toInt(),
                    title = "Metro Master",
                    description = "Faz 10 viagens de metro",
                    earned = false,
                    dateEarned = null
                )
            )

            // Insert saved places
            database.savedPlaceDao().insert(
                SavedPlace(
                    userId = userId.toInt(),
                    name = "Casa",
                    latitude = 38.7223,
                    longitude = -9.1393
                )
            )

            database.savedPlaceDao().insert(
                SavedPlace(
                    userId = userId.toInt(),
                    name = "Trabalho",
                    latitude = 38.7369,
                    longitude = -9.1427
                )
            )
        }
    }
}
