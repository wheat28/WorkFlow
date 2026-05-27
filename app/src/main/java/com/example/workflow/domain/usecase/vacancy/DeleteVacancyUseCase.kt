package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.repository.VacancyRepository

class DeleteVacancyUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke(id: String) = repository.deleteVacancy(id)
}
