package id.fishku.consumer.core.data.source.local

import id.fishku.consumer.core.data.source.local.datastore.ActivtiyPreferences
import id.fishku.consumer.core.data.source.local.datastore.UserPreferences
import id.fishku.consumer.core.data.source.local.entity.FishEntity
import id.fishku.consumer.core.data.source.local.entity.UserEntity
import id.fishku.consumer.core.data.source.local.room.FishDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val userPreferences: UserPreferences,
    private val activityPreferences: ActivtiyPreferences,
    private val fishDao: FishDao,
) {
    suspend fun saveSession(token: String, user: UserEntity) =
        userPreferences.saveSession(token, user)

    fun getToken(): Flow<String> = userPreferences.getAuthToken()

    fun getId(): Flow<Int> = userPreferences.getId()

    fun getName(): Flow<String> = userPreferences.getName()

    fun getEmail(): Flow<String> = userPreferences.getEmail()

    fun getPhoneNumber(): Flow<String> = userPreferences.getPhoneNumber()

    fun getAddress(): Flow<String> = userPreferences.getAddress()

    suspend fun deleteSession() = userPreferences.deleteSession()

    suspend fun insertAllFish(fishes: List<FishEntity>) = fishDao.insertAllFish(fishes)

    fun getAllFish(): Flow<List<FishEntity>> = fishDao.getAllFish()

    suspend fun saveActivity() = activityPreferences.saveActivity()

    fun getFirstLaunch(): Flow<Boolean> = activityPreferences.getFirstLaunch()
}