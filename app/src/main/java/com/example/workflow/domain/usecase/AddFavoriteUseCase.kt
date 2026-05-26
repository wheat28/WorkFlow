package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.FavoriteRepository

class AddFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(vacancyId: String) = repository.addFavorite(vacancyId)
}
