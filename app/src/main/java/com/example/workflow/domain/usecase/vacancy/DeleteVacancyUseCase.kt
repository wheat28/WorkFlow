package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.repository.VacancyRepository

import javax.inject.Inject

class DeleteVacancyUseCase @Inject constructor(
    private val repository: VacancyRepository
) {
    suspend operator fun invoke(id: String) {
        return repository.deleteVacancy(id)
    }
}
