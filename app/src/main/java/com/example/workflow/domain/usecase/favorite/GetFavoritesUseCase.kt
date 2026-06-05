package com.example.workflow.domain.usecase.favorite

import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(seekerId: String): List<Vacancy> {
        return repository.getFavorites(seekerId)
    }
}
