package id.fishku.consumer.core.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivtiyPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveActivity() = dataStore.edit { preferences ->
        preferences[FIRST_LAUNCH] = true
    }

    fun getFirstLaunch(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: false
    }

    companion object {
        private val FIRST_LAUNCH = booleanPreferencesKey("FIRST_LAUNCH")
    }
}