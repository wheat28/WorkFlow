package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.repository.VacancyRepository

class GetVacanciesUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke() = repository.getAllVacancies()
}
