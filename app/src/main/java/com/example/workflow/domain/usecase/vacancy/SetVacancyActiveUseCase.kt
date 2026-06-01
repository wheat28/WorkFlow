package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.repository.VacancyRepository

import javax.inject.Inject

class SetVacancyActiveUseCase @Inject constructor(private val repository: VacancyRepository) {
    suspend operator fun invoke(id: String, isActive: Boolean) = repository.setVacancyActive(id, isActive)
}
