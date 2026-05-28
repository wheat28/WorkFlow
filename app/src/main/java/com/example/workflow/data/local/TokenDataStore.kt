package com.example.workflow.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth_prefs")

class TokenDataStore(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userTypeFlow: Flow<String?> = context.dataStore.data.map { it[USER_TYPE_KEY] }
    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val displayNameFlow: Flow<String?> = context.dataStore.data.map { it[DISPLAY_NAME_KEY] }

    suspend fun saveToken(token: String, userType: String, userId: String, displayName: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[USER_TYPE_KEY] = userType
            it[USER_ID_KEY] = userId
            it[DISPLAY_NAME_KEY] = displayName
        }
    }

    suspend fun updateDisplayName(name: String) {
        context.dataStore.edit { it[DISPLAY_NAME_KEY] = name }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getToken(): String? = tokenFlow.firstOrNull()
    suspend fun getUserType(): String? = userTypeFlow.firstOrNull()
    suspend fun getUserId(): String? = userIdFlow.firstOrNull()
}
