package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class ApplicationApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun apply(token: String, request: ApplicationRequestDto): String {
        val response = client.post("$base/applications") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, String>>()
        return response["id"] ?: error("No id in response")
    }

    suspend fun getMyApplications(token: String, seekerId: String): List<ApplicationResponseDto> {
        return client.get("$base/seekers/$seekerId/applications") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()
    }

    suspend fun checkApplied(token: String, vacancyId: String): Boolean {
        val response = client.get("$base/applications/check/$vacancyId") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body<Map<String, Boolean>>()
        return response["applied"] ?: false
    }

    suspend fun cancelApplication(token: String, applicationId: String) {
        val response = client.delete("$base/applications/$applicationId") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка отмены отклика: ${response.status.value}")
    }

    suspend fun getApplicationsByVacancy(token: String, vacancyId: String): List<ApplicationResponseDto> {
        return client.get("$base/vacancies/$vacancyId/applications") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()
    }

    suspend fun updateApplicationStatus(token: String, applicationId: String, status: String) {
        val response = client.patch("$base/applications/$applicationId/status") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления статуса: ${response.status.value}")
    }
}
