package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class SeekerApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getById(seekerId: String): SeekerResponseDto {
        return client.get("$base/seekers/$seekerId").body()
    }

    suspend fun updateProfile(seekerId: String, request: SeekerUpdateRequestDto) {
        val response = client.put("$base/seekers/$seekerId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления профиля")
    }
}
