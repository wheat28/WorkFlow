package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.VacancyRepository

class GetEmployerVacanciesUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke(employerId: String) = repository.getEmployerVacancies(employerId)
}
