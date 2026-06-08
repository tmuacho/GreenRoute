package com.greenroute.app

import android.app.Application
import android.content.pm.PackageManager
import com.google.android.libraries.places.api.Places
import com.greenroute.app.data.local.database.AppDatabase
import com.greenroute.app.data.remote.DirectionsRepository
import com.greenroute.app.data.repository.PlaceRepository
import com.greenroute.app.data.repository.RouteRepository
import com.greenroute.app.data.repository.UserRepository

class GreenRouteApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    val routeRepository: RouteRepository by lazy {
        RouteRepository(database.routeDao(), database.emissionFactorDao())
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao(), database.userPreferencesDao())
    }

    val placeRepository: PlaceRepository by lazy {
        PlaceRepository(database.savedPlaceDao(), mapsApiKey)
    }

    val mapsApiKey: String by lazy {
        try {
            val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            info.metaData.getString("com.google.android.geo.API_KEY") ?: ""
        } catch (e: Exception) { "" }
    }

    val directionsRepository: DirectionsRepository by lazy {
        DirectionsRepository(mapsApiKey)
    }

    override fun onCreate() {
        super.onCreate()
        initializePlaces()
    }

    private fun initializePlaces() {
        try {
            if (mapsApiKey.isNotEmpty() && mapsApiKey != "YOUR_API_KEY") {
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, mapsApiKey)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
