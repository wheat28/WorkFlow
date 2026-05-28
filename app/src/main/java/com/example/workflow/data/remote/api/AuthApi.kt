package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.AuthResponseDto
import com.example.workflow.data.remote.dto.EmployerRegisterRequestDto
import com.example.workflow.data.remote.dto.LoginRequestDto
import com.example.workflow.data.remote.dto.SeekerRegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun login(request: LoginRequestDto): AuthResponseDto {
        val response = client.post("$base/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw Exception("Неверный email или пароль")
        return response.body()
    }

    suspend fun registerSeeker(request: SeekerRegisterRequestDto) {
        val response = client.post("$base/seekers/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw Exception("Email уже занят")
    }

    suspend fun registerEmployer(request: EmployerRegisterRequestDto) {
        val response = client.post("$base/employers/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw Exception("Email уже занят")
    }
}
