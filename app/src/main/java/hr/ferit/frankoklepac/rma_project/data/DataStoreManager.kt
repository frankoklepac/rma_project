package hr.ferit.frankoklepac.rma_project.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object DataStoreManager {
    private val USER_ID_KEY = stringPreferencesKey("user_id")

    suspend fun saveUserId(context: Context, userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    fun getUserId(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: ""
        }
    }

    suspend fun clearUserId(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }
}