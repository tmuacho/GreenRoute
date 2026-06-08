package com.greenroute.app.data.repository

import com.greenroute.app.data.local.dao.UserDao
import com.greenroute.app.data.local.dao.UserPreferencesDao
import com.greenroute.app.data.local.entities.User
import com.greenroute.app.data.local.entities.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository for User and UserPreferences operations.
 */
class UserRepository(
    private val userDao: UserDao,
    private val userPreferencesDao: UserPreferencesDao
) {
    val currentUser: Flow<User?> = userDao.getCurrentUser()
    val currentPreferences: Flow<UserPreferences?> = userPreferencesDao.getCurrentPreferences()

    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun insertUser(user: User): Long = userDao.insert(user)

    suspend fun updateUser(user: User) = userDao.update(user)

    suspend fun deleteUser(user: User) = userDao.delete(user)

    suspend fun logout() = userDao.deleteAll()

    fun getPreferencesByUserId(userId: Int): Flow<UserPreferences?> =
        userPreferencesDao.getPreferencesByUserId(userId)

    suspend fun insertPreferences(preferences: UserPreferences): Long =
        userPreferencesDao.insert(preferences)

    suspend fun updatePreferences(preferences: UserPreferences) =
        userPreferencesDao.update(preferences)
}
