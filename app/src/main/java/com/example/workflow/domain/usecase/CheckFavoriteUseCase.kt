package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.FavoriteRepository

class CheckFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(vacancyId: String) = repository.isFavorite(vacancyId)
}
