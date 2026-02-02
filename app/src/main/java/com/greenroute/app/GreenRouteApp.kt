package com.greenroute.app

import android.app.Application
import com.greenroute.app.data.local.database.AppDatabase
import com.greenroute.app.data.repository.PlaceRepository
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository

/**
 * Application class for GreenRoute.
 * Initializes the database and repositories.
 */
class GreenRouteApp : Application() {

    // Database instance
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    // Repositories
    val routeRepository: RouteRepository by lazy {
        RouteRepository(database.routeDao(), database.emissionFactorDao())
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao(), database.userPreferencesDao())
    }

    val placeRepository: PlaceRepository by lazy {
        PlaceRepository(database.savedPlaceDao())
    }
}
