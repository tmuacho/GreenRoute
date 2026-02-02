package com.greenroute.app.data.repository

import com.greenroute.app.data.local.dao.EmissionFactorDao
import com.greenroute.app.data.local.dao.RouteDao
import com.greenroute.app.data.local.entities.Route
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Route operations with CO2 calculation.
 */
class RouteRepository(
    private val routeDao: RouteDao,
    private val emissionFactorDao: EmissionFactorDao
) {
    val allRoutes: Flow<List<Route>> = routeDao.getAllRoutes()
    val savedRoutes: Flow<List<Route>> = routeDao.getSavedRoutes()

    fun getRecentRoutes(limit: Int = 10): Flow<List<Route>> = routeDao.getRecentRoutes(limit)

    fun getRoutesByUserId(userId: Int): Flow<List<Route>> = routeDao.getRoutesByUserId(userId)

    suspend fun getRouteById(id: Int): Route? = routeDao.getRouteById(id)

    suspend fun insert(route: Route): Long {
        // Calculate CO2 emission if not already set
        val routeWithEmission = if (route.co2Emission == null && route.transportType != null) {
            val emission = calculateCo2Emission(route.transportType, route.distance)
            route.copy(co2Emission = emission)
        } else {
            route
        }
        return routeDao.insert(routeWithEmission)
    }

    suspend fun update(route: Route) = routeDao.update(route)

    suspend fun delete(route: Route) = routeDao.delete(route)

    suspend fun toggleSaved(routeId: Int, isSaved: Boolean) {
        routeDao.updateSavedStatus(routeId, isSaved)
    }

    /**
     * Calculate CO2 emission based on transport type and distance.
     * @param transportType Type of transport (car, bus, metro, etc.)
     * @param distanceKm Distance in kilometers
     * @return CO2 emission in grams
     */
    suspend fun calculateCo2Emission(transportType: String, distanceKm: Double): Double {
        val factor = emissionFactorDao.getFactorByType(transportType)?.factor ?: 0.0
        return factor * distanceKm
    }
}
