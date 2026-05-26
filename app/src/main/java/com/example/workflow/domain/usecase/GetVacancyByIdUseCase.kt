package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.VacancyRepository

class GetVacancyByIdUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke(id: String) = repository.getVacancyById(id)
}
