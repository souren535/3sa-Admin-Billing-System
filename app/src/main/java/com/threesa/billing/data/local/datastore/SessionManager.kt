package com.threesa.billing.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val ADMIN_NAME_KEY = stringPreferencesKey("admin_name")

    @Volatile
    private var tokenInMemory: String? = null

    /**
     * Cold flow from DataStore that only emits when preferences are loaded from disk.
     */
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { it[ACCESS_TOKEN_KEY] }
        .onEach { token -> tokenInMemory = token }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }

    val adminNameFlow: Flow<String?> = context.dataStore.data.map { it[ADMIN_NAME_KEY] }

    /**
     * A StateFlow that holds the latest access token in memory once loaded.
     */
    val tokenState: StateFlow<String?> = tokenFlow
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        scope.launch {
            tokenFlow.collect { token ->
                tokenInMemory = token
            }
        }
    }

    /** Instant read from memory for OkHttp interceptors */
    fun getCachedToken(): String? {
        return tokenInMemory ?: tokenState.value ?: runBlocking {
            withTimeoutOrNull(500) { tokenFlow.firstOrNull() }
        }
    }

    suspend fun saveSession(token: String, refreshToken: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[ADMIN_NAME_KEY] = name
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
