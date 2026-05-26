package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class VacancyApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getAllVacancies(token: String): List<VacancyResponseDto> =
        client.get("$base/vacancies") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()

    suspend fun getVacancyById(token: String, id: String): VacancyResponseDto =
        client.get("$base/vacancies/$id") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()

    suspend fun getEmployerVacancies(token: String, employerId: String): List<VacancyResponseDto> =
        client.get("$base/employers/$employerId/vacancies") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()

    suspend fun createVacancy(token: String, request: VacancyRequestDto): String {
        val response = client.post("$base/vacancies") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, String>>()
        return response["id"] ?: error("No id in response")
    }

    suspend fun updateVacancy(token: String, id: String, request: VacancyRequestDto) {
        val response = client.put("$base/vacancies/$id") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления: ${response.status.value}")
    }

    suspend fun deleteVacancy(token: String, id: String) {
        val response = client.delete("$base/vacancies/$id") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка удаления: ${response.status.value}")
    }
}
