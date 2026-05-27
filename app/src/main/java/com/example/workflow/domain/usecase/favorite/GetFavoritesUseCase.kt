package com.example.workflow.domain.usecase.favorite

import com.example.workflow.domain.repository.FavoriteRepository

class GetFavoritesUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(seekerId: String) = repository.getFavorites(seekerId)
}
