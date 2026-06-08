package com.greenroute.app

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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

    val mapsApiKey: String by lazy {
        try {
            val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            info.metaData.getString("com.google.android.geo.API_KEY") ?: ""
        } catch (e: Exception) { "" }
    }

    val placeRepository: PlaceRepository by lazy {
        PlaceRepository(
            savedPlaceDao  = database.savedPlaceDao(),
            apiKey         = mapsApiKey,
            appPackageName = packageName,
            signatureSha1  = getSignatureSha1()
        )
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
                if (!Places.isInitialized()) Places.initialize(applicationContext, mapsApiKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Returns the SHA-1 fingerprint of the app's signing certificate as an
     * uppercase colon-separated hex string, e.g. "AB:CD:EF:...".
     * Required by Google APIs when the key has "Android app" restrictions.
     */
    @Suppress("DEPRECATION")
    private fun getSignatureSha1(): String {
        return try {
            val sigs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager
                    .getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    ?.apkContentsSigners
            } else {
                packageManager
                    .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                    .signatures
            }
            val cert = sigs?.firstOrNull() ?: return ""
            val digest = java.security.MessageDigest.getInstance("SHA1").digest(cert.toByteArray())
            digest.joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            Log.w("GreenRouteApp", "Could not compute SHA1: ${e.message}")
            ""
        }
    }
}
