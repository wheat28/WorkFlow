package com.example.workflow.domain.usecase.vacancy

import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.repository.VacancyRepository

import javax.inject.Inject

class GetVacanciesUseCase @Inject constructor(
    private val repository: VacancyRepository
) {
    suspend operator fun invoke(): List<VacancyResponseDto> {
        return repository.getAllVacancies()
    }
}
