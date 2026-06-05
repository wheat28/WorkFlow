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
import io.ktor.http.ContentType
import io.ktor.http.contentType

class VacancyApi(client: HttpClient) : BaseApi(client) {

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
        client.put("$base/vacancies/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.checkSuccess("Ошибка обновления")
    }

    suspend fun deleteVacancy(id: String) {
        client.delete("$base/vacancies/$id")
            .checkSuccess("Ошибка удаления")
    }

    suspend fun setVacancyActive(id: String, isActive: Boolean) {
        client.patch("$base/vacancies/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("isActive" to isActive))
        }.checkSuccess("Ошибка обновления статуса")
    }
}
