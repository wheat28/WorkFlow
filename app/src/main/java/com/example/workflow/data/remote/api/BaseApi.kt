package com.example.workflow.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

abstract class BaseApi(protected val client: HttpClient) {
    protected val base = "http://10.0.2.2:8080"
}

internal suspend fun HttpResponse.checkSuccess(message: String) {
    bodyAsText()
    if (!status.isSuccess()) error("$message: ${status.value}")
}
