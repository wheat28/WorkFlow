package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.repository.VacancyRepository

class SetVacancyActiveUseCase(private val repository: VacancyRepository) {
    suspend operator fun invoke(id: String, isActive: Boolean) = repository.setVacancyActive(id, isActive)
}
