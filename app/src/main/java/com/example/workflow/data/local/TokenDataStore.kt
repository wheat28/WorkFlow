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
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userTypeFlow: Flow<String?> = context.dataStore.data.map { it[USER_TYPE_KEY] }

    suspend fun saveToken(token: String, userType: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[USER_TYPE_KEY] = userType
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getToken(): String? = tokenFlow.firstOrNull()
}
