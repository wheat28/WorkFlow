package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.domain.repository.VacancyRepository

import javax.inject.Inject

class CreateVacancyUseCase @Inject constructor(
    private val repository: VacancyRepository
) {
    suspend operator fun invoke(request: VacancyRequestDto): String {
        return repository.createVacancy(request)
    }
}
