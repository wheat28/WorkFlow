package com.example.workflow.domain.usecase.application

import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.repository.ApplicationRepository
import javax.inject.Inject

class GetVacancyApplicationsUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    suspend operator fun invoke(vacancyId: String): List<ApplicationResponseDto> {
        return repository.getByVacancyId(vacancyId)
    }
}
