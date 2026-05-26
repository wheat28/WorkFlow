package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.FavoriteRepository

class RemoveFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(vacancyId: String) = repository.removeFavorite(vacancyId)
}
