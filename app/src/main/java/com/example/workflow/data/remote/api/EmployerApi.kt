package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EmployerApi(client: HttpClient) : BaseApi(client) {

    suspend fun getById(employerId: String): EmployerResponseDto {
        return client.get("$base/employers/$employerId").body()
    }

    suspend fun updateProfile(employerId: String, request: EmployerUpdateRequestDto) {
        client.put("$base/employers/$employerId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.checkSuccess("Ошибка обновления профиля")
    }

    suspend fun getStats(employerId: String): EmployerStatsDto {
        return client.get("$base/employers/$employerId/stats").body()
    }
}
