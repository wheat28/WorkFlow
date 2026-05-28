package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class SeekerApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getById(token: String, seekerId: String): SeekerResponseDto =
        client.get("$base/seekers/$seekerId") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()

    suspend fun updateProfile(token: String, seekerId: String, request: SeekerUpdateRequestDto) {
        val response = client.put("$base/seekers/$seekerId") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления профиля")
    }
}
