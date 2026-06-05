package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.repository.VacancyRepository

import javax.inject.Inject

class GetVacancyByIdUseCase @Inject constructor(
    private val repository: VacancyRepository
) {
    suspend operator fun invoke(id: String): Vacancy {
        return repository.getVacancyById(id)
    }
}
