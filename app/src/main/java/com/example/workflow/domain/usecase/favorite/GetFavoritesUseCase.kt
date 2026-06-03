package com.example.workflow.domain.usecase.favorite

import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(seekerId: String): List<VacancyResponseDto> {
        return repository.getFavorites(seekerId)
    }
}
