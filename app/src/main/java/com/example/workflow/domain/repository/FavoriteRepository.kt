package com.example.workflow.domain.repository

import com.example.workflow.data.remote.dto.VacancyResponseDto

interface FavoriteRepository {
    suspend fun addFavorite(vacancyId: String)
    suspend fun removeFavorite(vacancyId: String)
    suspend fun getFavorites(seekerId: String): List<VacancyResponseDto>
    suspend fun isFavorite(vacancyId: String): Boolean
}
