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
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ResumeApi(client: HttpClient) : BaseApi(client) {

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
        }.checkSuccess("Ошибка обновления резюме")
    }

    suspend fun setResumeActive(id: String, isActive: Boolean) {
        client.patch("$base/resumes/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("isActive" to isActive))
        }.checkSuccess("Ошибка обновления статуса")
    }

    suspend fun deleteResume(id: String) {
        client.delete("$base/resumes/$id")
            .checkSuccess("Ошибка удаления резюме")
    }
}
