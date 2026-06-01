package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.domain.repository.VacancyRepository

import javax.inject.Inject

class GetEmployerVacanciesUseCase @Inject constructor(private val repository: VacancyRepository) {
    suspend operator fun invoke(employerId: String) = repository.getEmployerVacancies(employerId)
}
