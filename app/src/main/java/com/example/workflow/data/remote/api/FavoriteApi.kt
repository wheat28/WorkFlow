package com.example.workflow.data.remote.api

import com.example.workflow.data.remote.dto.VacancyResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FavoriteApi(client: HttpClient) : BaseApi(client) {

    suspend fun addFavorite(vacancyId: String) {
        client.post("$base/favorites/$vacancyId") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.checkSuccess("Ошибка добавления в избранное")
    }

    suspend fun removeFavorite(vacancyId: String) {
        client.delete("$base/favorites/$vacancyId")
            .checkSuccess("Ошибка удаления из избранного")
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
