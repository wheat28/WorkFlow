package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.domain.repository.VacancyRepository

class UpdateVacancyUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke(id: String, request: VacancyRequestDto) =
        repository.updateVacancy(id, request)
}
