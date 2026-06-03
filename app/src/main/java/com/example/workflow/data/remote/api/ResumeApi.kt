package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
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

class ResumeApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getMyResumes(seekerId: String): List<ResumeResponseDto> {
        return client.get("$base/seekers/$seekerId/resumes").body()
    }

    suspend fun getResumeById(id: String): ResumeResponseDto {
        return client.get("$base/resumes/$id").body()
    }

    suspend fun createResume(request: ResumeRequestDto): String {
        val response = client.post("$base/resumes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, String>>()
        return response["id"] ?: error("No id in response")
    }

    suspend fun updateResume(id: String, request: ResumeRequestDto) {
        client.put("$base/resumes/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun setResumeActive(id: String, isActive: Boolean) {
        val response = client.patch("$base/resumes/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("isActive" to isActive))
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка обновления статуса")
    }

    suspend fun deleteResume(id: String) {
        val response = client.delete("$base/resumes/$id")
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка удаления резюме")
    }
}
