package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class VacancyApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getAllVacancies(): List<VacancyResponseDto> {
        return client.get("$base/vacancies").body()
    }

    suspend fun getVacancyById(id: String): VacancyResponseDto {
        return client.get("$base/vacancies/$id").body()
    }

    suspend fun getEmployerVacancies(employerId: String): List<VacancyResponseDto> {
        return client.get("$base/employers/$employerId/vacancies").body()
    }

    suspend fun createVacancy(request: VacancyRequestDto): String {
        val response = client.post("$base/vacancies") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, String>>()
        return response["id"] ?: error("No id in response")
    }

    suspend fun updateVacancy(id: String, request: VacancyRequestDto) {
        val response = client.put("$base/vacancies/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления: ${response.status.value}")
    }

    suspend fun deleteVacancy(id: String) {
        val response = client.delete("$base/vacancies/$id")
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка удаления: ${response.status.value}")
    }

    suspend fun setVacancyActive(id: String, isActive: Boolean) {
        val response = client.patch("$base/vacancies/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("isActive" to isActive))
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления статуса")
    }
}
