package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class EmployerApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getById(employerId: String): EmployerResponseDto {
        return client.get("$base/employers/$employerId").body()
    }

    suspend fun updateProfile(employerId: String, request: EmployerUpdateRequestDto) {
        val response = client.put("$base/employers/$employerId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления профиля")
    }

    suspend fun getStats(employerId: String): EmployerStatsDto {
        return client.get("$base/employers/$employerId/stats").body()
    }
}
