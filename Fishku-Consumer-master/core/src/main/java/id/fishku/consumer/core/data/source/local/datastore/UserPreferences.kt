package id.fishku.consumer.core.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.fishku.consumer.core.data.source.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(token: String, user: UserEntity) = dataStore.edit { preferences ->
        preferences[AUTH_KEY] = token
        preferences[ID_KEY] = user.id
        preferences[NAME_KEY] = user.name
        preferences[EMAIL_KEY] = user.email
        preferences[PHONE_NUMBER_KEY] = user.phoneNumber
        preferences[ADDRESS_KEY] = user.address
    }

    fun getAuthToken(): Flow<String> = dataStore.data.map { preferences ->
        preferences[AUTH_KEY] ?: ""
    }

    fun getId(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[ID_KEY] ?: 0
    }

    fun getName(): Flow<String> = dataStore.data.map { preferences ->
        preferences[NAME_KEY] ?: ""
    }

    fun getEmail(): Flow<String> = dataStore.data.map { preferences ->
        preferences[EMAIL_KEY] ?: ""
    }

    fun getPhoneNumber(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PHONE_NUMBER_KEY] ?: ""
    }

    fun getAddress(): Flow<String> = dataStore.data.map { preferences ->
        preferences[ADDRESS_KEY] ?: ""
    }

    suspend fun deleteSession() = dataStore.edit {
        it.clear()
    }

    companion object {
        private val AUTH_KEY = stringPreferencesKey("AUTH_KEY")
        private val ID_KEY = intPreferencesKey("ID_KEY")
        private val NAME_KEY = stringPreferencesKey("NAME_KEY")
        private val EMAIL_KEY = stringPreferencesKey("EMAIL_KEY")
        private val PHONE_NUMBER_KEY = stringPreferencesKey("PHONE_NUMBER_KEY")
        private val ADDRESS_KEY = stringPreferencesKey("ADDRESS_KEY")
    }
}