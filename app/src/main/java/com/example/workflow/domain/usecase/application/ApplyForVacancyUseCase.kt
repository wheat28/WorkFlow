package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository
import javax.inject.Inject

class ApplyForVacancyUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    suspend operator fun invoke(vacancyId: String, resumeId: String, coverLetter: String?): String {
        return repository.apply(vacancyId, resumeId, coverLetter?.ifBlank { null })
    }
}
