package com.example.workflow.data.remote

import com.example.workflow.BuildConfig
import com.example.workflow.data.local.TokenDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    fun create(tokenDataStore: TokenDataStore): HttpClient {
        val bearerAuthPlugin = createClientPlugin("BearerAuth") {
            onRequest { request, _ ->
                tokenDataStore.getToken()?.let { token ->
                    request.headers.append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
            install(bearerAuthPlugin)
        }
    }
}
