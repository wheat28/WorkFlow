package com.example.workflow.data.repository

import com.example.workflow.data.mapper.toDomain
import com.example.workflow.data.remote.api.FavoriteApi
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.repository.FavoriteRepository
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val api: FavoriteApi
) : FavoriteRepository {

    override suspend fun addFavorite(vacancyId: String) {
        return api.addFavorite(vacancyId)
    }

    override suspend fun removeFavorite(vacancyId: String) {
        return api.removeFavorite(vacancyId)
    }

    override suspend fun getFavorites(seekerId: String): List<Vacancy> {
        return api.getFavorites(seekerId).map { it.toDomain() }
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return api.isFavorite(vacancyId)
    }
}
