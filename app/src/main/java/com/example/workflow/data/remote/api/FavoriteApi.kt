package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.VacancyResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class FavoriteApi(private val client: HttpClient) {

    private val base = "http://10.0.2.2:8080"

    suspend fun addFavorite(vacancyId: String) {
        val response = client.post("$base/favorites/$vacancyId") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка добавления в избранное: ${response.status.value}")
    }

    suspend fun removeFavorite(vacancyId: String) {
        val response = client.delete("$base/favorites/$vacancyId")
        response.bodyAsText()
        if (!response.status.isSuccess()) error("Ошибка удаления из избранного: ${response.status.value}")
    }

    suspend fun getFavorites(seekerId: String): List<VacancyResponseDto> {
        return client.get("$base/seekers/$seekerId/favorites").body()
    }

    suspend fun isFavorite(vacancyId: String): Boolean {
        val response = client.get("$base/favorites/check/$vacancyId")
            .body<Map<String, Boolean>>()
        return response["isFavorite"] ?: false
    }
}
