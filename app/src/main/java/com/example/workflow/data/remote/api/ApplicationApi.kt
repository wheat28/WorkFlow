package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApplicationApi(client: HttpClient) : BaseApi(client) {

    suspend fun apply(request: ApplicationRequestDto): String {
        val response = client.post("$base/applications") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, String>>()
        return response["id"] ?: error("No id in response")
    }

    suspend fun getMyApplications(seekerId: String): List<ApplicationResponseDto> {
        return client.get("$base/seekers/$seekerId/applications").body()
    }

    suspend fun checkApplied(vacancyId: String): Boolean {
        val response = client.get("$base/applications/check/$vacancyId")
            .body<Map<String, Boolean>>()
        return response["applied"] ?: false
    }

    suspend fun cancelApplication(applicationId: String) {
        client.delete("$base/applications/$applicationId")
            .checkSuccess("Ошибка отмены отклика")
    }

    suspend fun getApplicationsByVacancy(vacancyId: String): List<ApplicationResponseDto> {
        return client.get("$base/vacancies/$vacancyId/applications").body()
    }

    suspend fun updateApplicationStatus(applicationId: String, status: String) {
        client.patch("$base/applications/$applicationId/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }.checkSuccess("Ошибка обновления статуса")
    }
}
