package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ResumeApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun getMyResumes(token: String, seekerId: String): List<ResumeResponseDto> =
        client.get("$base/seekers/$seekerId/resumes") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()

    suspend fun getResumeById(token: String, id: String): ResumeResponseDto =
        client.get("$base/resumes/$id") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.body()

    suspend fun updateResume(token: String, id: String, request: ResumeRequestDto) {
        client.put("$base/resumes/$id") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun createResume(token: String, request: ResumeRequestDto): String {
        val response = client.post("$base/resumes") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<Map<String, String>>()
        return response["id"] ?: error("No id in response")
    }
}
