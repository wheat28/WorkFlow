package com.example.workflow.domain.usecase.application

import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.domain.repository.ApplicationRepository

class ApplyForVacancyUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(vacancyId: String, resumeId: String, coverLetter: String?): String =
        repository.apply(ApplicationRequestDto(vacancyId, resumeId, coverLetter?.ifBlank { null }))
}
