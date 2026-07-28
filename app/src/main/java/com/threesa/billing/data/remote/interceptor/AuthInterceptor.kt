package com.threesa.billing.data.remote.interceptor

import com.threesa.billing.data.local.datastore.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        val isAuthEndpoint = path.contains("/login") || path.contains("/refresh") || path.contains("/forgot-password")
        
        val token = if (isAuthEndpoint) null else sessionManager.getCachedToken()

        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}
