package com.threesa.billing.data.remote.interceptor

import android.util.Log
import com.threesa.billing.data.local.datastore.SessionManager
import com.threesa.billing.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: dagger.Lazy<AuthRepository>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): okhttp3.Request? {
        // Only try to refresh once
        if (responseCount(response) >= 2) {
            Log.d("TokenAuthenticator", "Giving up refresh after multiple attempts")
            runBlocking { sessionManager.clearSession() }
            return null
        }

        Log.d("TokenAuthenticator", "401 detected, attempting token refresh...")

        val refreshed = runBlocking {
            withTimeoutOrNull(5000) {
                (authRepository.get() as? com.threesa.billing.data.repository.AuthRepositoryImpl)
                    ?.refreshAccessToken()
            } ?: false
        }

        if (!refreshed) {
            Log.d("TokenAuthenticator", "Refresh failed, clearing session and redirecting to login")
            runBlocking { sessionManager.clearSession() }
            return null
        }

        val newToken = sessionManager.getCachedToken()
        Log.d("TokenAuthenticator", "Refresh successful, retrying request with new token")

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
