package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository

class GetVacancyApplicationsUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(vacancyId: String) = repository.getByVacancyId(vacancyId)
}
