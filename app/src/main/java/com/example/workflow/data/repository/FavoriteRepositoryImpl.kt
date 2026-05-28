package com.example.workflow.data.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.FavoriteApi
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val api: FavoriteApi,
    private val tokenDataStore: TokenDataStore
) : FavoriteRepository {

    override suspend fun addFavorite(vacancyId: String) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.addFavorite(token, vacancyId)
    }

    override suspend fun removeFavorite(vacancyId: String) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.removeFavorite(token, vacancyId)
    }

    override suspend fun getFavorites(seekerId: String): List<VacancyResponseDto> {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getFavorites(token, seekerId)
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.isFavorite(token, vacancyId)
    }
}
