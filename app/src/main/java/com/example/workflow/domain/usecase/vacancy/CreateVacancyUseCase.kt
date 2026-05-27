package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.domain.repository.VacancyRepository

class CreateVacancyUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke(request: VacancyRequestDto): String =
        repository.createVacancy(request)
}
