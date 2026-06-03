package com.example.workflow.domain.usecase.favorite

import com.example.workflow.domain.repository.FavoriteRepository
import javax.inject.Inject

class CheckFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(vacancyId: String): Boolean {
        return repository.isFavorite(vacancyId)
    }
}
